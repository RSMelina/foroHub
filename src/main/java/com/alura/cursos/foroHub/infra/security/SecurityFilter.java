package com.alura.cursos.foroHub.infra.security;

import com.alura.cursos.foroHub.domain.user.User;
import com.alura.cursos.foroHub.domain.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Filters incoming requests to authenticate using JWT token.
     * @param request HTTP request object.
     * @param response HTTP response object.
     * @param filterChain Filter chain for additional filters.
     * @throws ServletException If an error occurs during filtering.
     * @throws IOException If an I/O error occurs.
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract Authorization header containing JWT token
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.replace("Bearer ", "");
                // Get subject (username) from token
                String subject = tokenService.getSubject(token);

                if (subject != null) {
                    // Retrieve user from UserRepository based on username
                    User user = (User) userRepository.findByUsername(subject);
                    if (user != null) {
                        // Create Authentication object and set it in SecurityContextHolder
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        throw new IllegalArgumentException("User not found for username: " + subject);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            // Handle specific exceptions or log errors
            logger.error("Authentication error: " + e.getMessage(), e);
        }
        // Proceed with the filter chain
        filterChain.doFilter(request, response);
    }


}

