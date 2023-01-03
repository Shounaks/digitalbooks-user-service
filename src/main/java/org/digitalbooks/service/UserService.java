package org.digitalbooks.service;

//import jakarta.transaction.Transactional;

import org.digitalbooks.entity.User;
import org.digitalbooks.exception.UserServiceException;
import org.digitalbooks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepository userRepository;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<User> retrieveUserById(Long userId){
        return userRepository.findById(userId);
    }

    public Optional<User> retrieveUserByEmail(String email){
        return userRepository.findByEmailId(email);
    }

    private static User hideUserPassword(User savedUser) {
        return User.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .emailId(savedUser.getEmailId())
                .password("***************")//Hide the password and don't send the real Password
                .subscriptions(savedUser.getSubscriptions())
                .authorUser(savedUser.isAuthorUser())
                .build();
    }

    public User addUser(User user) {
        Optional<User> optionalUser = userRepository.findByEmailId(user.getEmailId());
        optionalUser.ifPresent(user1 -> {
            throw new UserServiceException("Login Error: Email already Registered.");
        });
        user.setSubscriptions(new ArrayList<>());
        User savedUser = userRepository.save(encodePassword(user));
        return hideUserPassword(savedUser);
    }

    public User loginUser(String email, String password) {
        User savedUser = userRepository
                .findByEmailId(email)
                .filter(user -> passwordEncoder.matches(password.trim(),user.getPassword()))
                .orElseThrow(() -> new UserServiceException("Login Error: User or Password Invalid"));
        return hideUserPassword(savedUser);
    }

    private User encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword().trim());
        user.setPassword(encodedPassword);
        return user;
    }
}
