package ru.school.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school.model.entity.Recommendation;
import ru.school.repositories.RecommendationRepository;

@Service
public class RecommendationService extends BaseService<Recommendation, Long> {

    private final RecommendationRepository recommendationRepository;

    @Autowired
    public RecommendationService(RecommendationRepository recommendationRepository) {
        super(recommendationRepository);
        this.recommendationRepository = recommendationRepository;
    }
}
