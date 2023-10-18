package ru.school.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.school.model.Status;

import javax.persistence.*;
import java.time.LocalTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "verter")
public class Verter {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verter_id_generator")
    @SequenceGenerator(name = "verter_id_generator",
            sequenceName = "verter_id_seq",
            allocationSize = 1
    )
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "check_id", referencedColumnName = "id", nullable = false)
    private Check check_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private Status status;

    @Column(name = "time")
    private LocalTime time;
}
