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
        Credential credential = googleOAuthService.getCredentialFromCookies(request);
        if (credential == null) {
            System.out.println("======= CREDENTIAL IS NULL =======");
            System.out.println(request.getServletPath());
            if (!request.getServletPath().contains("/auth/") && !request.getServletPath().contains("callback")) {
                response.sendRedirect("/auth/init" + "?return=" + request.getServletPath());
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }
        request.setAttribute("credential", credential);
        var authentication = new UsernamePasswordAuthenticationToken(
                "username",
                "password",
                List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

}
