package com.kapture.howisthemood.filter;

import com.google.api.client.auth.oauth2.Credential;
import com.kapture.howisthemood.service.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthFilter extends OncePerRequestFilter {

    private final GoogleOAuthService googleOAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authToken = GoogleOAuthService.getAuthTokenFromCookies(request);
        if (authToken == null || authToken.isEmpty()) {
            googleOAuthService.initOAuth(response);
        }
        var authentication = new UsernamePasswordAuthenticationToken(
                "username",
                "password",
                List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

}
