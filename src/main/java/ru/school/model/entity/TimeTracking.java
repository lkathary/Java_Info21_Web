package ru.school.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "time_tracking")
public class TimeTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "time_tracking_id_generator")
    @SequenceGenerator(name = "time_tracking_id_generator",
            sequenceName = "time_tracking_id_seq",
            allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "peer", referencedColumnName = "nickname", nullable = false)
    private Peer peer;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "state")
    private Long status;
}
