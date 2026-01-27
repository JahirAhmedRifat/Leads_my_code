package com.example.jwtToken.jwtConfigurations;

import com.example.jwtToken.commonException.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // টোকেন নাই, পাবলিক endpoint হতে পারে
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7).trim();
        String email;

        try {
            email = jwtUtil.extractEmail(token);

            if (jwtUtil.isTokenExpired(token)) {
                logger.debug("JWT expired for token: {}", token);
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            }

            // Context এ কিছু সেট নাই, এখন auth বসাই
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.debug("Authenticated user: {}", email);
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");

        } catch (LockedException e) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (JwtException e) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Invalid token");

        } catch (Exception e) {
            logger.error("Unexpected error in JwtFilter", e);
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Invalid token");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.setStatus(status);

        ErrorResponse error = new ErrorResponse(status, message, false);
        String json = objectMapper.writeValueAsString(error);
        response.getWriter().write(json);
        response.getWriter().flush();

        SecurityContextHolder.clearContext(); // ভুল token এ context clear
    }
}
