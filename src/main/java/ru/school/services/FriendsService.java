package ru.school.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school.model.entity.Friends;
import ru.school.repositories.FriendsRepository;

@Service
public class FriendsService extends BaseService<Friends, Long> {

    private final FriendsRepository friendsRepository;

    @Autowired
    public FriendsService( FriendsRepository friendsRepository) {
        super(friendsRepository);
        this.friendsRepository = friendsRepository;
    }
}
