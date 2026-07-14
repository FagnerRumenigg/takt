package org.fr.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fr.service.JwtService;
import org.fr.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try {
                String subject = jwtService.extractSubject(token);
                log.debug("JWT recebido em {} {} subject={}", request.getMethod(), request.getRequestURI(), subject);
                if (SecurityContextHolder.getContext().getAuthentication() == null && jwtService.isValidAccessToken(token, subject)) {
                    UserDetails userDetails = userService.loadUserByUsername(subject);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Autenticação carregada para subject={}", subject);
                } else {
                    log.debug("JWT ignorado em {} {}. authenticationExists={}", request.getMethod(), request.getRequestURI(),
                            SecurityContextHolder.getContext().getAuthentication() != null);
                }
            } catch (Exception ex) {
                log.warn("Falha ao processar JWT em {} {}", request.getMethod(), request.getRequestURI(), ex);
            }
        } else {
            log.debug("Sem Authorization Bearer em {} {}", request.getMethod(), request.getRequestURI());
        }
        filterChain.doFilter(request, response);
    }
}
