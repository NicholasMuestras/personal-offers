package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.exception.TooMuchUsersException;
import org.skypro.projects.personaloffers.exception.UserNotFoundException;
import org.skypro.projects.personaloffers.model.User;
import org.skypro.projects.personaloffers.repository.UserExternalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserExternalRepository userExternalRepository;

    @Autowired
    public UserService(UserExternalRepository userExternalRepository) {
        this.userExternalRepository = userExternalRepository;
    }

    public List<User> findUsersByUserName(String userName) {
        return userExternalRepository.findByUserName(userName);
    }
    
    public User getOneUserByUserNameOrFail(String userName) {
        List<User> users = this.findUsersByUserName(userName);
        
        if (users.isEmpty()) {
            throw new UserNotFoundException("User with userName: '" + userName + "' was not found.");
        }
        
        if (users.size() > 1) {
            throw new TooMuchUsersException("Found more than one user with userName: '" + userName + "'");
        }
        
        return users.get(0);
    }
}
