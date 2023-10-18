package ru.school.model.entity;

import lombok.*;
import ru.school.model.Status;

import javax.persistence.*;
import java.time.LocalTime;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p2p")
public class PeerToPeer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p2p_id_generator")
    @SequenceGenerator(name = "p2p_id_generator",
            sequenceName = "p2p_id_seq",
            allocationSize = 1
    )
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "check_id", referencedColumnName = "id", nullable = false)
    private Check check_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checking_peer", referencedColumnName = "nickname", nullable = false)
    private Peer checkingPeer;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private Status status;

    @Column(name = "time")
    private LocalTime time;
}
