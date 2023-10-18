package ru.school.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "checks")
public class Check {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "checks_id_generator")
    @SequenceGenerator(name = "checks_id_generator",
            sequenceName = "checks_id_seq",
            allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "peer", referencedColumnName = "nickname", nullable = false)
    private Peer peer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Task", referencedColumnName = "title", nullable = false)
    private Task task;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date")
    private LocalDate date;
}
