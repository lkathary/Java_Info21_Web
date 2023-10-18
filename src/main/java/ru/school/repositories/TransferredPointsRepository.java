package ru.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.school.model.entity.TransferredPoints;

public interface TransferredPointsRepository extends JpaRepository<TransferredPoints, Long> {
}

