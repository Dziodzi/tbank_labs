package io.github.dziodzi.service;

import io.github.dziodzi.entity.User;
import io.github.dziodzi.exception.AlreadyExistsException;
import io.github.dziodzi.exception.AuthException;
import io.github.dziodzi.exception.NotFoundException;
import io.github.dziodzi.repository.UserRepository;
import io.github.dziodzi.tools.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Service for handling user-related operations, including creation, retrieval,
 * updating, and authentication of users. Also provides methods for checking
 * user existence and retrieving the current authenticated user.
 */
@Service
@RequiredArgsConstructor
@LogExecutionTime
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * Saves a new user to the repository.
     *
     * @param user the user to save
     * @return the saved user
     * @throws RuntimeException if there is an error while saving the user
     */
    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error while saving user", e);
        }
    }
    
    /**
     * Creates a new user after checking if the user already exists.
     *
     * @param user the user to create
     * @return the created user
     * @throws AlreadyExistsException if a user with the same username or email already exists
     */
    public User create(User user) {
        checkUserExistence(user);
        return save(user);
    }
    
    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the found user
     * @throws NotFoundException if no user with the specified username is found
     */
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
    
    /**
     * Provides a `UserDetailsService` to load user details by username.
     *
     * @return a `UserDetailsService` instance
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }
    
    /**
     * Updates the details of an existing user.
     *
     * @param user the user with updated details
     * @throws NotFoundException if the user does not exist
     */
    public void update(User user) {
        if (userRepository.existsById(user.getId())) {
            userRepository.save(user);
        } else {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }
    }
    
    /**
     * Retrieves the currently authenticated user.
     *
     * @return the currently authenticated user
     * @throws AuthException if the user is not authenticated
     */
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException("User is not authenticated");
        }
        return getByUsername(authentication.getName());
    }
    
    /**
     * Checks if a user already exists by username or email.
     *
     * @param user the user to check
     * @throws AlreadyExistsException if the user already exists by username or email
     */
    private void checkUserExistence(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new AlreadyExistsException("User with this username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AlreadyExistsException("User with this email already exists");
        }
    }
}
