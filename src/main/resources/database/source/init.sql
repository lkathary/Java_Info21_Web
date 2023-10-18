-- Drop tables:
DROP TABLE IF EXISTS peers CASCADE;
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS checks CASCADE;
DROP TABLE IF EXISTS p2p CASCADE;
DROP TABLE IF EXISTS verter CASCADE;
DROP TABLE IF EXISTS xp CASCADE;
DROP TABLE IF EXISTS transferred_points CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS recommendations CASCADE;
DROP TABLE IF EXISTS time_tracking CASCADE;

-- Drop enum:
DROP TYPE IF EXISTS status CASCADE;

-- Create enum:
CREATE TYPE status AS ENUM ('Start', 'Success', 'Failure');
CREATE CAST (character varying AS status) WITH INOUT AS ASSIGNMENT;

-- Create tables:
CREATE TABLE IF NOT EXISTS peers (
    nickname varchar not null primary key,
    birthday date not null
);

CREATE TABLE IF NOT EXISTS tasks (
    title varchar not null primary key,
    parent_task varchar default null,
    max_xp int not null,
    CHECK (max_xp >= 0),
    constraint fk_tasks_parent_task foreign key (parent_task) references tasks (title) on delete set null
);

CREATE TABLE IF NOT EXISTS checks (
    id serial primary key,
    peer varchar not null,
    task varchar not null,
    date date,
    constraint fk_check_peer foreign key (peer) references peers (nickname) on delete cascade,
    constraint fk_check_task foreign key (task) references tasks (title) on delete cascade
);

CREATE TABLE IF NOT EXISTS p2p (
    id serial primary key,
    check_id int not null,
    checking_peer varchar not null,
    state status not null,
    time time,
    constraint fk_p2p_check_id foreign key (check_id) references checks (id) on delete cascade,
    constraint fk_p2p_checking_peer_id foreign key (checking_peer) references peers (nickname) on delete cascade
);

CREATE TABLE IF NOT EXISTS verter (
    id serial primary key,
    check_id int not null,
    state status not null,
    time time,
    constraint fk_p2p_verter_id foreign key (check_id) references checks (id) on delete cascade
);

CREATE TABLE IF NOT EXISTS xp (
    id serial primary key,
    check_id int not null,
    xp_amount int not null,
    CHECK (xp_amount >= 0),
    constraint fk_xp_check_id foreign key (check_id) references checks (id) on delete cascade
);

CREATE TABLE IF NOT EXISTS transferred_points (
    id serial primary key,
    checking_peer varchar not null,
    checked_peer varchar not null,
    points_amount int not null,
    constraint fk_transferredpoints_checkingpeer foreign key (checking_peer) references peers (nickname) on delete cascade,
    constraint fk_transferredpoints_checkedpeer foreign key (checked_peer) references peers (nickname) on delete cascade,
    CHECK (points_amount > 0),
    CHECK (checking_peer != checked_peer),
    UNIQUE (checked_peer, checking_peer)
);

CREATE TABLE IF NOT EXISTS friends (
    id serial primary key,
    peer1 varchar not null,
    peer2 varchar not null,
    constraint fk_friends_peer1 foreign key (peer1) references peers (nickname) on delete cascade,
    constraint fk_friends_peer2 foreign key (peer2) references peers (nickname) on delete cascade,
    CHECK (peer1 != peer2),
    UNIQUE (peer1, peer2)
);

CREATE TABLE IF NOT EXISTS recommendations (
    id serial primary key,
    peer varchar not null,
    recommended_peer varchar not null,
    constraint fk_recommendations_peer foreign key (peer) references peers (nickname) on delete cascade,
    constraint fk_recommendations_recommendedpeer foreign key (recommended_peer) references peers (nickname) on delete cascade,
    CHECK (peer != recommended_peer),
    UNIQUE (peer, recommended_peer)
);

CREATE TABLE IF NOT EXISTS time_tracking (
    id serial primary key,
    peer varchar not null,
    date date,
    time time,
    state int,
    CHECK (state IN (1, 2)),                                   -- 1 'in', 2 'out'
    constraint fk_timetraking_peer foreign key (peer) references peers (nickname) on delete cascade
);

-- Validate Friends
CREATE OR REPLACE FUNCTION fnc_check_friends() RETURNS TRIGGER AS $BODY$
    BEGIN
        IF (TG_OP = 'INSERT') THEN
            DECLARE
                num int := (SELECT count(id)
                            FROM friends
                            WHERE peer1 = NEW.peer2  AND peer2 = NEW.peer1);
            BEGIN
                RAISE NOTICE 'INSERT:';
                RAISE NOTICE 'num: %', num;
                RAISE NOTICE 'peer1: %', NEW.peer1;
                RAISE NOTICE 'peer2: %', NEW.peer2;
                IF (num = 0) THEN
                    INSERT INTO friends (peer1, peer2) VALUES (NEW.peer2, NEW.peer1);
                END IF;
                RETURN NEW;
            END;
        ELSEIF (TG_OP = 'DELETE') THEN
            DELETE FROM friends WHERE peer1 = OLD.peer2  AND peer2 = OLD.peer1;
            RETURN OLD;
        END IF;
        RETURN NULL;
    END;
$BODY$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS check_friends_trigger on friends;
CREATE TRIGGER check_friends_trigger
    AFTER INSERT OR DELETE ON friends
        FOR EACH ROW
    EXECUTE PROCEDURE fnc_check_friends();
-- End --


--- Calls ---

-- 1) Написать функцию, возвращающую таблицу TransferredPoints в более человекочитаемом виде
-- Ник пира 1, ник пира 2, количество переданных пир поинтов.
-- Количество отрицательное, если пир 2 получил от пира 1 больше поинтов.
DROP FUNCTION IF EXISTS fnc_transferred_points();
CREATE OR REPLACE FUNCTION fnc_transferred_points() RETURNS TABLE(
                                                                peer1 peers.nickname%TYPE,
                                                                peer2 peers.nickname%TYPE,
                                                                points_amount int) AS $BODY$
BEGIN
RETURN QUERY (
    WITH cleaned_tp AS(
                SELECT DISTINCT
                    CASE checking_peer < checked_peer WHEN true THEN checking_peer ELSE checked_peer END AS peer_1,
                    CASE checking_peer < checked_peer WHEN true THEN checked_peer ELSE checking_peer END AS peer_2
                FROM transferred_points
            )
            SELECT peer_1, peer_2, COALESCE(t1.points_amount, 0) - COALESCE(t2.points_amount, 0) AS points_amount
            FROM cleaned_tp
            LEFT JOIN transferred_points AS t1 ON peer_1 = t1.checking_peer AND peer_2 = t1.checked_peer
            LEFT JOIN transferred_points AS t2 ON peer_2 = t2.checking_peer AND peer_1 = t2.checked_peer
            ORDER BY peer_1, peer_2
    );
RETURN;
END;
$BODY$ LANGUAGE plpgsql;

-- 2) Написать функцию, которая возвращает таблицу вида: ник пользователя, название проверенного задания,
-- кол-во полученного XP
-- В таблицу включать только задания, успешно прошедшие проверку (определять по таблице Checks).
-- Одна задача может быть успешно выполнена несколько раз. В таком случае в таблицу включать все успешные проверки.
DROP FUNCTION IF EXISTS fnc_check_peers_xp();
CREATE OR REPLACE FUNCTION fnc_check_peers_xp() RETURNS TABLE (
                                                            peer peers.nickname%TYPE,
                                                            task tasks.title%TYPE,
                                                            xp xp.xp_amount%TYPE) AS $BODY$
BEGIN
RETURN QUERY (SELECT with_check_id.peer, with_check_id.task, with_check_id.xp
    FROM (SELECT DISTINCT (c.id), c.peer AS peer,
                                  c.task AS task,
                                  xp.xp_amount xp
                        FROM p2p p
                                 LEFT JOIN checks c ON p.check_id = c.id
                                 LEFT JOIN verter v ON v.check_id = c.id
                                 LEFT JOIN xp ON c.id = xp.check_id
                                 LEFT JOIN tasks t ON t.title = c.task
                        WHERE p.state = 'Success'
                          AND COALESCE(v.state, 'Success') = 'Success'
                          AND xp.xp_amount >= t.max_xp * 0.8) AS with_check_id);
END;
$BODY$ LANGUAGE plpgsql;

-- 3) Написать функцию, определяющую пиров, которые не выходили из кампуса в течение всего дня
-- Параметры функции: день, например 12.05.2022.
-- Функция возвращает только список пиров.
DROP FUNCTION IF EXISTS fnc_peers_not_left_campus;
CREATE OR REPLACE FUNCTION fnc_peers_not_left_campus(date_ date) RETURNS TABLE (peer varchar(30)) AS $BODY$
BEGIN
RETURN QUERY (  SELECT time_tracking.peer
                FROM time_tracking
                WHERE date = date_
                GROUP BY time_tracking.peer
                HAVING SUM(state) = 1);
END;
$BODY$ LANGUAGE plpgsql;

-- 4) Найти процент успешных и неуспешных проверок за всё время
-- Формат вывода: процент успешных, процент неуспешных
DROP PROCEDURE IF EXISTS prc_success_ratio;
CREATE OR REPLACE PROCEDURE prc_success_ratio(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
DECLARE
all_checks NUMERIC;
    failure_p2p NUMERIC;
    failure_verter NUMERIC;
	failure_percent NUMERIC;
BEGIN
    all_checks := (SELECT COUNT(*) FROM p2p WHERE state = 'Start');
	failure_p2p := (SELECT COUNT(*) FROM p2p WHERE state = 'Failure');
	failure_verter := (SELECT COUNT(*) FROM verter WHERE state = 'Failure');
    failure_percent := ROUND((failure_p2p + failure_verter) * 100 / all_checks);
    OPEN results FOR
        SELECT (100 - failure_percent) AS successful_checks, failure_percent AS unsuccessful_checks;
END;
$BODY$;

-- 5) Посчитать изменение в количестве пир поинтов каждого пира по таблице TransferredPoints
-- Результат вывести отсортированным по изменению числа поинтов.
-- Формат вывода: ник пира, изменение в количество пир поинтов
DROP PROCEDURE IF EXISTS prc_points_change;
CREATE OR REPLACE PROCEDURE prc_points_change(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        SELECT peer, SUM(points_change) AS points_change
        FROM (SELECT checking_peer AS peer,
                    points_amount AS points_change
            FROM transferred_points
            UNION ALL
            SELECT checked_peer, -points_amount
            FROM transferred_points) changes
        GROUP BY peer
        ORDER BY points_change;
END;
$BODY$;

-- 6) Посчитать изменение в количестве пир поинтов каждого пира по таблице, возвращаемой первой функцией из Part 3
-- Результат вывести отсортированным по изменению числа поинтов.
-- Формат вывода: ник пира, изменение в количество пир поинтов
DROP PROCEDURE IF EXISTS prc_points_change_by_func;
CREATE OR REPLACE PROCEDURE prc_points_change_by_func(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        SELECT peer,
            SUM(points_change) AS points_change
        FROM (  SELECT peer1 AS peer, points_amount AS points_change
                FROM fnc_transferred_points()
                UNION ALL
                SELECT peer2 AS peer, -points_amount AS points_change
                FROM fnc_transferred_points()
        ) AS changes
        GROUP BY peer
        ORDER BY points_change;
END;
$BODY$;

-- 7) Определить самое часто проверяемое задание за каждый день
-- При одинаковом количестве проверок каких-то заданий в определенный день, вывести их все.
-- Формат вывода: день, название задания
DROP PROCEDURE IF EXISTS prc_most_frequent_tasks;
CREATE OR REPLACE PROCEDURE prc_most_frequent_tasks(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        WITH counts AS (SELECT date, task, COUNT(*) AS count_ FROM checks GROUP BY task, date ORDER BY date, task)
        SELECT c.date, c.task
        FROM counts c
        WHERE count_ = (SELECT MAX(count_) FROM counts c2 WHERE c2.date = c.date)
        ORDER BY date, task;
END;
$BODY$;

-- 8) Определить длительность последней P2P проверки
-- Под длительностью подразумевается разница между временем,
-- указанным в записи со статусом "начало", и временем, указанным в записи со статусом "успех" или "неуспех".
-- Формат вывода: длительность проверки
DROP PROCEDURE IF EXISTS prc_latest_check_duration;
CREATE OR REPLACE PROCEDURE prc_latest_check_duration(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        WITH t1 AS (SELECT *
                    FROM p2p
                    WHERE id = (SELECT max(id) FROM p2p) or id = (SELECT max(id) FROM p2p) - 1)
        SELECT tt2.time - tt1.time AS duration_time
        FROM t1 tt1, t1 tt2
        WHERE tt2.id = (SELECT max(id) FROM t1) and tt1.id = (SELECT max(id) FROM t1) - 1;
END;
$BODY$;

-- 9) Найти всех пиров, выполнивших весь заданный блок задач и дату завершения последнего задания
-- Параметры процедуры: название блока, например "CPP".
-- Результат вывести отсортированным по дате завершения.
-- Формат вывода: ник пира, дата завершения блока (т.е. последнего выполненного задания из этого блока)
DROP PROCEDURE IF EXISTS prc_who_finished_block;
CREATE OR REPLACE PROCEDURE prc_who_finished_block(INOUT results refcursor, block_name IN varchar) LANGUAGE plpgsql
AS $BODY$
BEGIN
    OPEN results FOR
        SELECT checks.peer, date
        FROM checks
        FULL JOIN p2p ON p2p.check_id = checks.id
        FULL JOIN verter ON verter.check_id = checks.id
        WHERE p2p.state = 'Success' AND  (verter.state = 'Success' or verter.state is null)
                AND checks.task = (SELECT title
                                    FROM (SELECT title, substring(tasks.title from '^[A-Z]*') as block
                                            FROM tasks) as t1
                                    WHERE t1.block = block_name
                                    ORDER BY title DESC
                                    LIMIT 1);
END;
$BODY$;

-- 10) Определить, к какому пиру стоит идти на проверку каждому обучающемуся
-- Определять нужно исходя из рекомендаций друзей пира, т.е. нужно найти пира,
-- проверяться у которого рекомендует наибольшее число друзей.
-- Формат вывода: ник пира, ник найденного проверяющего
DROP PROCEDURE IF EXISTS prc_choose_peer_for_checking;
CREATE OR REPLACE PROCEDURE prc_choose_peer_for_checking(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        SELECT peer1 AS peer, recommended_peer
        FROM (WITH t1 AS (  SELECT peer1, peer2 AS friend FROM friends
                            union all
                            SELECT peer2, peer1 AS friend FROM friends)
                SELECT DISTINCT ON (peer1) peer1, recommended_peer, count(friend) AS num
                FROM t1
                FULL JOIN recommendations ON t1.friend = recommendations.peer
                WHERE peer1 != recommended_peer
                GROUP BY peer1, recommended_peer
                ORDER BY peer1, num DESC) AS tt2;
END;
$BODY$;

-- 11) Определить процент пиров, которые:
-- - Приступили к блоку 1
-- - Приступили к блоку 2
-- - Приступили к обоим
-- - Не приступили ни к одному
--
-- Параметры процедуры: название блока 1, например CPP, название блока 2, например A.
-- Формат вывода: процент приступивших к первому блоку, процент приступивших ко второму блоку, процент приступивших к обоим, процент не приступивших ни к одному
DROP PROCEDURE IF EXISTS prc_count_for_two_blocks;
CREATE OR REPLACE PROCEDURE prc_count_for_two_blocks(INOUT results refcursor, block1 varchar, block2 varchar)
LANGUAGE plpgsql AS $BODY$
DECLARE
    total_peers integer;
BEGIN
    total_peers := (SELECT COUNT(nickname) FROM peers);
    OPEN results FOR
        WITH peers1 AS (SELECT DISTINCT peer
                        FROM checks
                        WHERE checks.task ~ CONCAT('^', block1, '\d+')
                        ),
             peers2 AS (SELECT DISTINCT peer
                        FROM checks
                        WHERE checks.task ~ CONCAT('^', block2, '\d+')
                        ),
             peers_Both AS (SELECT *
                            FROM peers1
                            INTERSECT
                            SELECT *
                            FROM peers2
                        ),
             peers_any AS (SELECT DISTINCT *
                            FROM peers1
                            UNION
                            SELECT DISTINCT *
                            FROM peers2
                        ),
             peers_none AS (SELECT nickname AS peer
                            FROM peers
                            EXCEPT
                            SELECT *
                            FROM peers_any)

        SELECT  round((SELECT COUNT(*)
                        FROM peers1) * 100.0 / total_peers) AS started_block1,
                round((SELECT COUNT(*)
                        FROM peers2) * 100.0 / total_peers) AS started_block2,
                round((SELECT COUNT(*)
                        FROM peers_both) * 100.0 / total_peers) AS started_both_blocks,
                round((SELECT COUNT(*)
                        FROM peers_none) * 100.0 / total_peers) AS didnt_start_any_block;
END ;
$BODY$;

-- 12) Определить N пиров с наибольшим числом друзей
-- Параметры процедуры: количество пиров N.
-- Результат вывести отсортированным по кол-ву друзей.
-- Формат вывода: ник пира, количество друзей
DROP PROCEDURE IF EXISTS prc_find_most_friendly;
CREATE OR REPLACE PROCEDURE prc_find_most_friendly(INOUT results refcursor, max_friends_count integer) LANGUAGE plpgsql
AS $BODY$
BEGIN
    OPEN results FOR
        SELECT peer1 AS peer, COUNT(peer2) AS friends_count
        FROM friends
        GROUP BY peer1
        ORDER BY friends_count DESC
        LIMIT max_friends_count;
END;
$BODY$;

-- 13) Определить процент пиров, которые когда-либо успешно проходили проверку в свой день рождения
-- Также определите процент пиров, которые хоть раз проваливали проверку в свой день рождения.
-- Формат вывода: процент успехов в день рождения, процент неуспехов в день рождения
DROP PROCEDURE IF EXISTS prc_find_birthday_checks;
CREATE OR REPLACE PROCEDURE prc_find_birthday_checks(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
DECLARE
    total_peers int;
BEGIN
    total_peers := (SELECT COUNT(nickname) FROM peers);
    OPEN results FOR
        WITH bday_checks AS (SELECT c.id,
                                    peer,
                                    p2.state,
                                    TO_CHAR(c.date, 'DD.MM')     AS c_date,
                                    TO_CHAR(p.birthday, 'DD.MM') AS b_date
                            FROM checks c
                            JOIN peers p ON c.peer = p.nickname
                            JOIN p2p p2 ON c.id = p2.check_id
                            ),
             fail_or_ok AS (SELECT *
                            FROM bday_checks
                            WHERE c_date = b_date AND state != 'Start')
        SELECT round((SELECT count(DISTINCT peer)
        FROM fail_or_ok
        WHERE state = 'Success') * 100 / total_peers::numeric) AS successful_checks,
                round((SELECT count(DISTINCT peer)
                        FROM fail_or_ok
                        WHERE state = 'Failure') * 100 / total_peers::numeric) AS unsuccessful_checks;
END ;
$BODY$;

-- 14) Определить кол-во XP, полученное в сумме каждым пиром
-- Если одна задача выполнена несколько раз, полученное за нее кол-во XP равно максимальному за эту задачу.
-- Результат вывести отсортированным по кол-ву XP.
-- Формат вывода: ник пира, количество XP
DROP PROCEDURE IF EXISTS prc_total_xp_by_peer;
CREATE OR REPLACE PROCEDURE prc_total_xp_by_peer(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        WITH max_per_task AS (SELECT peer, task, max(xp_amount) AS max_xp
                                FROM xp
                                JOIN checks ON xp.check_id = checks.id
                                GROUP BY peer, task),
        sum_per_user AS (SELECT peer, sum(max_xp) AS xp
                          FROM max_per_task
                          GROUP BY peer)
        SELECT peer, xp
        FROM sum_per_user
        ORDER BY xp DESC;
END;
$BODY$;


-- 15) Определить всех пиров, которые сдали заданные задания 1 и 2, но не сдали задание 3
-- Параметры процедуры: названия заданий 1, 2 и 3.
-- Формат вывода: список пиров
DROP PROCEDURE IF EXISTS prc_find_passed_one_two_but_not_three;
CREATE OR REPLACE PROCEDURE prc_find_passed_one_two_but_not_three(INOUT results refcursor,
                                                                    task1 varchar,
                                                                    task2 varchar,
                                                                    task3 varchar) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        WITH passed_checks AS (SELECT DISTINCT peer, task
                                FROM checks c
                                JOIN p2p p ON c.id = p.check_id
                                LEFT JOIN public.verter v ON c.id = v.check_id
                                WHERE p.state = 'Success' AND (v.state IS NULL OR v.state = 'Success'))
        ((SELECT DISTINCT peer
            FROM passed_checks
            WHERE task = task1)
                INTERSECT
            (SELECT DISTINCT peer
            FROM passed_checks
            WHERE task = task2))
                EXCEPT
            (SELECT DISTINCT peer
            FROM passed_checks
            WHERE task = task3);
END ;
$BODY$;

-- 16) Используя рекурсивное обобщенное табличное выражение,
-- для каждой задачи вывести кол-во предшествующих ей задач
-- То есть сколько задач нужно выполнить, исходя из условий входа,
-- чтобы получить доступ к текущей.
-- Формат вывода: название задачи, количество предшествующих
DROP PROCEDURE IF EXISTS prc_count_previous_tasks;
CREATE OR REPLACE PROCEDURE prc_count_previous_tasks(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        WITH RECURSIVE r AS (SELECT title AS task, parent_task AS parent, 0 AS level
                             FROM tasks t
                             WHERE parent_task IS NULL
                             UNION
                             SELECT t2.title AS task, t2.parent_task AS parent, r.level + 1 AS level
                             FROM tasks t2
                             JOIN r ON t2.parent_task = r.task)

        SELECT task, level AS prev_count
        FROM r;
END;
$BODY$;

-- 17) Найти "удачные" для проверок дни. День считается "удачным",
-- если в нем есть хотя бы N идущих подряд успешных проверки
-- Параметры процедуры: количество идущих подряд успешных проверок N.
-- Временем проверки считать время начала P2P этапа.
-- Под идущими подряд успешными проверками подразумеваются успешные проверки, между которыми нет неуспешных.
-- При этом кол-во опыта за каждую из этих проверок должно быть не меньше 80% от максимального.
-- Формат вывода: список дней
DROP PROCEDURE IF EXISTS prc_lucky_days;
CREATE OR REPLACE PROCEDURE prc_lucky_days(INOUT results refcursor, min_count integer) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        WITH data AS(SELECT date,time, status_check, LEAD(status_check) OVER (ORDER BY date, time) AS next_status_check
                     FROM (SELECT checks.date,
                                    CASE WHEN 100 * xp.xp_amount / tasks.max_xp >= 80 THEN true
                                    ELSE false
                                    END AS status_check, p2p.time
                            FROM checks
                            JOIN tasks ON checks.task = tasks.title
                            JOIN xp ON checks.id = xp.check_id
                            JOIN p2p ON checks.id = p2p.check_id AND p2p.state in('Success', 'Failure'))
        ch),
        data_prev_checks AS (   SELECT t1.date, t1.time, t1.status_check, t1.next_status_check, count(t2.date)
                                FROM data t1
                                JOIN data t2 on t1.date = t2.date AND t1.time <= t2.time
                                                                  AND t1.status_check = t2.next_status_check
                                GROUP BY t1.date, t1.time, t1.status_check, t1.next_status_check)
        SELECT date
        FROM (  SELECT date, max(success_count) AS max_success_count
                FROM (  SELECT date, count as success_count
                        FROM data_prev_checks
                        WHERE status_check
                     ) success_checks
                GROUP BY date) m
        WHERE max_success_count >= min_count;
END ;
$BODY$;

-- 18) Определить пира с наибольшим числом выполненных заданий
-- Формат вывода: ник пира, число выполненных заданий
DROP PROCEDURE IF EXISTS prc_max_success_tasks_peer;
CREATE OR REPLACE PROCEDURE prc_max_success_tasks_peer(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        SELECT peer, count(*) AS xp
        FROM xp
        INNER JOIN checks ON checks.id = xp.check_id
        INNER JOIN tasks  ON tasks.title = checks.task
        WHERE 100 * xp.xp_amount / tasks.max_xp >= 80
        GROUP BY peer
        ORDER BY xp DESC
        LIMIT 1;
END;
$BODY$;

-- 19) Определить пира с наибольшим количеством XP
-- Формат вывода: ник пира, количество XP
DROP PROCEDURE IF EXISTS prc_max_xp_peer;
CREATE OR REPLACE PROCEDURE prc_max_xp_peer(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        SELECT peer, sum(xp_amount) AS xp
        FROM xp
        INNER JOIN checks ON checks.id = xp.check_id
        GROUP BY peer
        ORDER BY xp DESC
        LIMIT 1;
END;
$BODY$;

-- 20) Определить пира, который провел сегодня в кампусе большесего времени
-- Формат вывода: ник пира
DROP PROCEDURE IF EXISTS prc_max_time_spent_peer;
CREATE OR REPLACE PROCEDURE prc_max_time_spent_peer(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        WITH start_state AS (SELECT peer, time AS in_time, state
                            FROM time_tracking
                            WHERE state = 1 AND date = CURRENT_DATE),
            finish_state AS (SELECT peer, time AS out_time, state
                             FROM time_tracking
                             WHERE state = 2 AND date = CURRENT_DATE)

        SELECT start_state.peer
        FROM start_state
        JOIN finish_state ON start_state.peer = finish_state.peer
        ORDER BY finish_state.out_time - start_state.in_time DESC
        LIMIT 1;
END;
$BODY$;

-- 21) Определить пиров, приходивших раньше заданного времени не менее N раз за всё время
-- Параметры процедуры: время, количество раз N.
-- Формат вывода: список пиров
DROP PROCEDURE IF EXISTS prc_early_arriving_peers;
CREATE OR REPLACE PROCEDURE prc_early_arriving_peers(INOUT results refcursor, checked_time time, num integer)
LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        SELECT peer
        FROM time_tracking
        WHERE state = 1 AND time < checked_time
        GROUP BY peer
        HAVING count(peer) > num;
END;
$BODY$;

-- 22) Определить пиров, выходивших за последние N дней из кампуса больше M раз
-- Параметры процедуры: количество дней N, количество раз M.
-- Формат вывода: список пиров
DROP PROCEDURE IF EXISTS prc_going_out_peers;
CREATE OR REPLACE PROCEDURE prc_going_out_peers(INOUT results refcursor, days_count integer, num integer)
LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        SELECT peer
        FROM time_tracking
        WHERE time_tracking.state = 2 AND CURRENT_DATE - time_tracking.date <= days_count
        GROUP BY peer
        HAVING count(peer) > num;
END;
$BODY$;

-- 23) Определить пира, который пришел сегодня последним
-- Формат вывода: ник пира
DROP PROCEDURE IF EXISTS prc_last_came_today_peer;
CREATE OR REPLACE PROCEDURE prc_last_came_today_peer(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        SELECT peer
        FROM time_tracking
        WHERE date = CURRENT_DATE AND state = 1
        ORDER BY time DESC
        LIMIT 1;
END;
$BODY$;

-- 24) Определить пиров, которые выходили вчера из кампуса больше чем на N минут
-- Параметры процедуры: количество минут N.
-- Формат вывода: список пиров
DROP PROCEDURE IF EXISTS prc_yesterday_smoking_peers;
CREATE OR REPLACE PROCEDURE prc_yesterday_smoking_peers(INOUT results refcursor, minutes_counts integer)
LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        WITH start_state AS (SELECT *
                            FROM time_tracking
                            WHERE state = 1 AND date = CURRENT_DATE - 1),
             finish_state AS (SELECT *
                                FROM time_tracking
                                WHERE state = 2 AND date = CURRENT_DATE - 1)

        SELECT DISTINCT start_state.peer
        FROM start_state
        JOIN finish_state ON start_state.peer = finish_state.peer
                  AND finish_state.time < start_state.time
                  AND start_state.time - finish_state.time > concat(minutes_counts, 'minutes')::interval;
END;
$BODY$;

-- 25) Определить для каждого месяца процент ранних входов
-- Для каждого месяца посчитать, сколько раз люди, родившиеся в этот месяц,
-- приходили в кампус за всё время (будем называть это общим числом входов).
-- Для каждого месяца посчитать, сколько раз люди, родившиеся в этот месяц,
-- приходили в кампус раньше 12:00 за всё время (будем называть это числом ранних входов).
-- Для каждого месяца посчитать процент ранних входов в кампус относительно общего числа входов.
-- Формат вывода: месяц, процент ранних входов
DROP PROCEDURE IF EXISTS prc_early_visits;
CREATE OR REPLACE PROCEDURE prc_early_visits(INOUT results refcursor) LANGUAGE plpgsql AS $BODY$
BEGIN
    OPEN results FOR
        WITH month_id AS (SELECT generate_series(1, 12) AS month_),
            all_enters AS (
                SELECT date_part('month', time_tracking.date) AS month_, sum(state) AS visits
                FROM time_tracking
                LEFT JOIN peers ON peers.nickname = time_tracking.peer
                WHERE date_part('month', peers.birthday) = date_part('month', time_tracking.date) AND state = 1
                GROUP BY month_
            ),
            early_enters AS (
                SELECT date_part('month', time_tracking.date) AS month_, sum(state) AS visits
                FROM time_tracking
                LEFT JOIN peers ON peers.nickname = time_tracking.peer
                WHERE date_part('month', peers.birthday) = date_part('month', time_tracking.date) AND
                      state = 1 AND time < '12:00'
                GROUP BY month_
            )
        SELECT to_char(to_timestamp(month_id.month_::text, 'MM'), 'Month') AS month,
                CASE WHEN all_enters.visits is null THEN 0
                    ELSE
                        CASE WHEN early_enters.visits is null THEN 0
                            ELSE 100 * early_enters.visits / all_enters.visits
                        END
                END AS early_entries
        FROM month_id
        LEFT JOIN all_enters ON all_enters.month_ = month_id.month_
        LEFT JOIN early_enters ON early_enters.month_ = month_id.month_;
END;
$BODY$;
