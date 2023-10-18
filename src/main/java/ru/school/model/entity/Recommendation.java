package ru.school.model.entity;


import lombok.*;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recommendations_id_generator")
    @SequenceGenerator(name = "recommendations_id_generator",
            sequenceName = "recommendations_id_seq",
            allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "peer", referencedColumnName = "nickname", nullable = false)
    private Peer peer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recommended_peer", referencedColumnName = "nickname", nullable = false)
    private Peer recommendedPeer;
}
