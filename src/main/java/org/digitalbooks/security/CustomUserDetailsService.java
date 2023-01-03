package org.digitalbooks.security;

import org.digitalbooks.entity.User;
import org.digitalbooks.exception.UserServiceException;
import org.digitalbooks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByEmailId(username)
                .orElseThrow(()-> new UserServiceException("User Not Found!"));
        return new CustomUserDetails(user);
    }
}
