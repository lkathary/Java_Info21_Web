package ru.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.school.model.entity.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
}
