package ru.school.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school.model.entity.TimeTracking;
import ru.school.repositories.TimeTrackingRepository;

@Service
public class TimeTrackingService extends BaseService<TimeTracking, Long> {

    private final TimeTrackingRepository timeTrackingRepository;

    @Autowired
    public TimeTrackingService(TimeTrackingRepository timeTrackingRepository) {
        super(timeTrackingRepository);
        this.timeTrackingRepository = timeTrackingRepository;
    }
}
