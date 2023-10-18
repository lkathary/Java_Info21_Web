package ru.school.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transferred_points")
public class TransferredPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transferred_points_id_generator")
    @SequenceGenerator(name = "transferred_points_id_generator",
            sequenceName = "transferred_points_id_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checking_peer", referencedColumnName = "nickname", nullable = false)
    private Peer checkingPeer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checked_peer", referencedColumnName = "nickname", nullable = false)
    private Peer checkedPeer;

    @Column(name = "points_amount")
    private Long amount;
}
