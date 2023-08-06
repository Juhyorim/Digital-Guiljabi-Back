package com.connecter.digitalguiljabiback.config;

import com.connecter.digitalguiljabiback.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader;
        authHeader = request.getHeader("Authorization");

        final String jwt;
        final String userId; //oauth uid

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("@@@: #1");

        jwt = authHeader.substring(7); //Bearer 제외
        userId = jwtService.extractUsername(jwt);

        if (userId == null)
            throw new JwtException("유효하지 않은 토큰");
        log.info("@@@: #2");
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);
            log.info("@@@: #3");
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("@@@: #4");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null, //우리는 credentials가 없는 사용자 사용
                  userDetails.getAuthorities()
                );

                authToken.setDetails(
                  new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                throw new JwtException("유효하지 않은 토큰");
            }
            log.info("@@@: #5");
        }
        log.info("@@@: d#6");

        filterChain.doFilter(request, response);
    }
}
