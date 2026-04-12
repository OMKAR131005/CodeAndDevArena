package com.devconnect.bakend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    @Value("${app.cookie.name}")
    private String cookieName;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("REQUEST URI: " + request.getRequestURI());
        Cookie[] cookies = request.getCookies();
        String token=null;
        if(cookies!=null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals(cookieName)){
                    token=cookie.getValue();
                    break;
                }
            }
            if(token!=null&&jwtUtil.validateToken(token)&&SecurityContextHolder.getContext().getAuthentication()==null){
                if(jwtUtil.isPending2FA(token)&&request.getRequestURI().equals("/api/auth/verify-2fa")){
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(jwtUtil.getId(token), null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                if(!jwtUtil.isPending2FA(token)){
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(jwtUtil.getId(token), null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            }
        }
        filterChain.doFilter(request,response);
    }
}
