package io.github.dziodzi.service;

import io.github.dziodzi.tools.LogExecutionTime;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing logout operations by invalidating JWT tokens.
 * Maintains a set of invalidated tokens to ensure they cannot be reused.
 */
@Service
@LogExecutionTime
public class LogoutService {
    
    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();
    
    /**
     * Invalidates the specified JWT token by adding it to the set of invalidated tokens.
     *
     * @param token the JWT token to invalidate
     */
    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }
    
    /**
     * Checks if the specified JWT token has been invalidated.
     *
     * @param token the JWT token to check
     * @return true if the token is invalidated, false otherwise
     */
    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }
}
