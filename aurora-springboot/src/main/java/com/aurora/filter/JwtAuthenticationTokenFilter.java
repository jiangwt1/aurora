package com.aurora.filter;


import com.aurora.model.dto.UserDetailsDTO;
import com.aurora.service.TokenService;
import com.aurora.util.UserUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
@SuppressWarnings("all")
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    public TokenService tokenService;

    @Autowired
    public AuthenticationEntryPoint authenticationEntryPoint;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            UserDetailsDTO userDetailsDTO = tokenService.getUserDetailDTO(request);
            if (Objects.nonNull(userDetailsDTO) && Objects.isNull(UserUtil.getAuthentication())) {
                tokenService.renewToken(userDetailsDTO);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetailsDTO, null, userDetailsDTO.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            // Redis超时或其他异常时，记录日志但不阻塞请求
            logger.warn("Failed to get user details from token, error: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
