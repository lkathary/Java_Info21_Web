package ru.school.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "xp")
public class XP {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "xp_id_generator")
    @SequenceGenerator(name = "xp_id_generator",
            sequenceName = "xp_id_seq",
            allocationSize = 1
    )
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "check_id", referencedColumnName = "id", nullable = false)
    private Check check_id;

    @Column(name = "xp_amount")
    private Long amount;
}