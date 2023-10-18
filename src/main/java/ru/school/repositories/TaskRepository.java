package ru.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.school.model.entity.Task;

import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    @Query(value = "select title from tasks", nativeQuery = true)
    List<String> getAllTitles();
}
