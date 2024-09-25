package com.backend.ecommerce_backend.api.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.dao.LocalUserRepo;
import com.backend.ecommerce_backend.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private JWTService jwtService;
    private LocalUserRepo localUserRepo;

    public JWTRequestFilter(JWTService jwtService, LocalUserRepo localUserRepo) {
        this.jwtService = jwtService;
        this.localUserRepo = localUserRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            try{
                String username = jwtService.getUsernameFromToken(token);
                Optional<LocalUser> opUser = localUserRepo.findByUsernameIgnoreCase(username);
                if(opUser.isPresent()) {
                    LocalUser localUser = opUser.get();
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(localUser, null, new ArrayList<>());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }catch (JWTDecodeException ignored){

            }

        }
        filterChain.doFilter(request, response);
    }
}
