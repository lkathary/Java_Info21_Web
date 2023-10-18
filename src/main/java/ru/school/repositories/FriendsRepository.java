package ru.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.school.model.entity.Friends;

public interface FriendsRepository extends JpaRepository<Friends, Long> {
}
