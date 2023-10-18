package ru.school.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "friends")
public class Friends {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friends_id_generator")
    @SequenceGenerator(name = "friends_id_generator",
            sequenceName = "friends_id_seq",
            allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "peer1", referencedColumnName = "nickname")
    private Peer peer1;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "peer2", referencedColumnName = "nickname")
    private Peer peer2;
}
