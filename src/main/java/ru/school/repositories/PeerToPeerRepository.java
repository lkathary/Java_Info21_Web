package ru.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.school.model.entity.PeerToPeer;

public interface PeerToPeerRepository extends JpaRepository<PeerToPeer, Long> {
}
