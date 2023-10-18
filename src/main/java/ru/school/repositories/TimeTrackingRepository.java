package ru.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.school.model.entity.TimeTracking;

public interface TimeTrackingRepository extends JpaRepository<TimeTracking, Long> {
}
