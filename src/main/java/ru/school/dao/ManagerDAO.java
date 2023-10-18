package ru.school.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ManagerDAO {

    public static HashMap<Integer, String> CALL;

    static {
        CALL = new HashMap<>();
        CALL.put(1, "select * from fnc_transferred_points()");
        CALL.put(2, "select * from fnc_check_peers_xp()");
        CALL.put(3, "select * from fnc_peers_not_left_campus(?)");
        CALL.put(4, "{call prc_success_ratio(?::refcursor)}");
        CALL.put(5, "{call prc_points_change(?::refcursor)}");
        CALL.put(6, "{call prc_points_change_by_func(?::refcursor)}");
        CALL.put(7, "{call prc_most_frequent_tasks(?::refcursor)}");
        CALL.put(8, "{call prc_latest_check_duration(?::refcursor)}");
        CALL.put(9, "{call prc_who_finished_block(?::refcursor, ?::varchar)}");
        CALL.put(10, "{call prc_choose_peer_for_checking(?::refcursor)}");
        CALL.put(11, "{call prc_count_for_two_blocks(?::refcursor, ?::varchar, ?::varchar)}");
        CALL.put(12, "{call prc_find_most_friendly(?::refcursor, ?::int)}");
        CALL.put(13, "{call prc_find_birthday_checks(?::refcursor)}");
        CALL.put(14, "{call prc_total_xp_by_peer(?::refcursor)}");
        CALL.put(15, "{call prc_find_passed_one_two_but_not_three(?::refcursor, ?::varchar, ?::varchar, ?::varchar)}");
        CALL.put(16, "{call prc_count_previous_tasks(?::refcursor)}");
        CALL.put(17, "{call prc_lucky_days(?::refcursor, ?::int)}");
        CALL.put(18, "{call prc_max_success_tasks_peer(?::refcursor)}");
        CALL.put(19, "{call prc_max_xp_peer(?::refcursor)}");
        CALL.put(20, "{call prc_max_time_spent_peer(?::refcursor)}");
        CALL.put(21, "{call prc_early_arriving_peers(?::refcursor, ?::time, ?::int)}");
        CALL.put(22, "{call prc_going_out_peers(?::refcursor, ?::int, ?::int)}");
        CALL.put(23, "{call prc_last_came_today_peer(?::refcursor)}");
        CALL.put(24, "{call prc_yesterday_smoking_peers(?::refcursor, ?::int)}");
        CALL.put(25, "{call prc_early_visits(?::refcursor)}");
    }

    private final JdbcTemplate jdbcTemplate;

    public Map<String, List<Object>> executeQuery(String SQL) throws SQLException {
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            ResultSet result = preparedStatement.executeQuery();
            return parseResultSet(result);
        }
    }

    public Map<String, List<Object>> callFunction(String SQL, Object... args) throws SQLException {
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            if (args != null) {
                int index = 0;
                for (Object it : args) {
                    preparedStatement.setObject(++index, it);
                }
            }
            ResultSet result = preparedStatement.executeQuery();
            return parseResultSet(result);
        }
    }

    public Map<String, List<Object>> callProcedure(String SQL, Object... args) throws SQLException {
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             CallableStatement callableStatement = connection.prepareCall(SQL)) {
            connection.setAutoCommit(false);
            callableStatement.registerOutParameter(1, Types.REF_CURSOR);
            if (args != null) {
                int index = 1;
                for (Object it : args) {
                    callableStatement.setObject(++index, it);
                }
            }
            callableStatement.execute();
            ResultSet result = (ResultSet) callableStatement.getObject(1);
            return parseResultSet(result);
        }
    }

    private Map<String, List<Object>> parseResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int countColumn = metaData.getColumnCount();
        Map<String, List<Object>> resultMap = new LinkedHashMap<>();
        if (!resultSet.next()) {
            return resultMap;
        }
        List<String> headerTable = new ArrayList<>();
        List<List<Object>> dataTable = new ArrayList<>();
        for (int i = 0; i < countColumn; ++i) {
            headerTable.add(metaData.getColumnName(i + 1));
            dataTable.add(new ArrayList<>());
            dataTable.get(i).add(resultSet.getObject(i + 1));
        }
        while (resultSet.next()) {
            for (int i = 0; i < countColumn; ++i) {
                dataTable.get(i).add(resultSet.getObject(i + 1));
            }
        }
        for (int i = 0; i < countColumn; ++i) {
            resultMap.put(headerTable.get(i), dataTable.get(i));
        }
        return resultMap;
    }
}
