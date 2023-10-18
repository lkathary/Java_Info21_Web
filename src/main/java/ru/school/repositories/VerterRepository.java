package ru.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.school.model.entity.Verter;

public interface VerterRepository extends JpaRepository<Verter, Long> {
}
