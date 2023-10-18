package ru.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.school.model.entity.XP;

import java.util.List;

public interface XPRepository extends JpaRepository<XP, Long> {

    @Override
    @Query(value = "select * from xp order by id asc", nativeQuery = true)
    List<XP> findAll();
}
