package ru.school.model.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @Column(name = "title")
    private String title;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_task", referencedColumnName = "title")
    private Task parentTask;

    @Column(name = "max_xp", nullable = false)
    private Long maxExperience;
}
