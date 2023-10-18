package ru.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.school.model.entity.Check;

import java.util.List;

public interface CheckRepository extends JpaRepository<Check, Long> {

    @Query(value = "select id from checks order by id asc", nativeQuery = true)
    List<Long> findAllIdsAsc();
}
