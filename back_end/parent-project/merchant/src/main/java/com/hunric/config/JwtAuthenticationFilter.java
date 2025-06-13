package com.hunric.config;

import com.hunric.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // 无需JWT认证的路径列表
    private final List<String> excludedPaths = Arrays.asList(
        "/api/merchant/register",
        "/api/merchant/send-login-code",
        "/api/merchant/login",
        "/api/merchant/login/password",
        "/api/merchant/send-reset-password-code",
        "/api/merchant/verify-reset-password-code",
        "/api/merchant/reset-password",
        "/api/verification/**",
        "/api/test/**",
        "/api/merchant/spu/template/download",
        "/actuator/**",
        "/error"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // 检查请求路径是否在排除列表中
        boolean shouldExclude = excludedPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
        
        if (shouldExclude) {
            log.debug("跳过JWT验证: {}", path);
        }
        
        return shouldExclude;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                String email = jwtUtil.getEmailFromToken(jwt);
                Long merchantId = jwtUtil.getMerchantIdFromToken(jwt);
                
                log.debug("JWT验证成功: email={}, merchantId={}", email, merchantId);
                
                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 设置到安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // 将商家信息添加到请求属性中，方便后续使用
                request.setAttribute("merchantId", merchantId);
                request.setAttribute("merchantEmail", email);
            }
        } catch (Exception ex) {
            log.error("无法设置用户认证: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取JWT token
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 