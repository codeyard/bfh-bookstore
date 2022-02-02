package org.bookstore.security;

import org.bookstore.auth.entity.AppUser;
import org.bookstore.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetails implements UserDetailsService {

    private UserRepository userRepository;

    public UserDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUser = userRepository.findByUsername(username);

        if (appUser.isPresent()) {
            return User.withUsername(username).password(appUser.get().getPassword()).roles(appUser.get().getRole()).build();
        } else {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
    }
}
