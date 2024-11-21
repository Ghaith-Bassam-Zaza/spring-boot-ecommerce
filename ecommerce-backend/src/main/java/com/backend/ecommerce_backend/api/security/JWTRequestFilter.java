package com.backend.ecommerce_backend.api.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.dao.LocalUserRepo;
import com.backend.ecommerce_backend.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter implements ChannelInterceptor{

    private JWTService jwtService;
    private LocalUserRepo localUserRepo;

    /**
     * Constructor.
     *
     * @param jwtService injected by spring boot
     * @param localUserRepo injected by spring boot
     */
    public JWTRequestFilter(JWTService jwtService, LocalUserRepo localUserRepo) {
        this.jwtService = jwtService;
        this.localUserRepo = localUserRepo;
    }


    /**
     * Filters incoming HTTP requests to authenticate users based on JWT tokens.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if an error occurs during the filtering process
     * @throws IOException if an I/O error occurs during the filtering process
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Retrieve the Authorization header from the request
        String tokenHeader = request.getHeader("Authorization");
        UsernamePasswordAuthenticationToken authentication = checkToken(tokenHeader);
        if (authentication != null) {
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }


        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken checkToken(String token) {
        // Check if the Authorization header is present and starts with "Bearer "
        if (token != null && token.startsWith("Bearer ")) {
            // Extract the token from the header
            token = token.substring(7);
            try {
                // Get the username from the token using the JWT service
                String username = jwtService.getUsernameFromToken(token);

                // Find the user in the repository by username (case-insensitive)
                Optional<LocalUser> opUser = localUserRepo.findByUsernameIgnoreCase(username);

                // If the user is found, set up the authentication token
                if (opUser.isPresent()) {
                    LocalUser localUser = opUser.get();
                    if(localUser.getEmailVerified()){
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(localUser, null, new ArrayList<>());
                        // Set the authentication in the security context
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        return authToken;
                    }

                }
            } catch (JWTDecodeException ignored) {
                // In case of JWT decode exceptions do nothing (invalid token)
            }
        }
        SecurityContextHolder.getContext().setAuthentication(null);
        return null;
    }


    // METHODS FOR WEBSOCKET



    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        if(message.getHeaders().get("SimpMessageType").equals(SimpMessageType.CONNECT)) {
            Map nativeHeaders = (Map) message.getHeaders().get("nativeHeaders");
            if (nativeHeaders != null) {
                List authTokenList = (List) nativeHeaders.get("Authorization");
                if (authTokenList != null) {
                    String tokenHeader = (String) authTokenList.get(0);
                    checkToken(tokenHeader);
                }
            }
        }
        return message;

    }
}
