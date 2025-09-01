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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器 - 订单服务
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // 无需JWT认证的路径列表
    private final List<String> excludedPaths = Arrays.asList(
        "/actuator",
        "/swagger-ui",
        "/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        log.debug("处理请求路径: {}", path);
        
        // 检查是否需要跳过JWT验证
        boolean shouldExclude = excludedPaths.stream()
            .anyMatch(excludedPath -> path.startsWith(excludedPath));
        
        if (shouldExclude) {
            log.debug("跳过JWT验证: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);
            log.info("处理请求: {}, JWT: {}", path, jwt != null ? "存在" : "不存在");

            if (StringUtils.hasText(jwt)) {
                log.info("开始验证JWT token");
                boolean isValid = jwtUtil.validateToken(jwt);
                log.info("JWT验证结果: {}", isValid ? "成功" : "失败");
                
                if (isValid) {
                    String email = jwtUtil.getEmailFromToken(jwt);
                    String userType = jwtUtil.getUserTypeFromToken(jwt);
                    Long userId = null;
                    
                    // 根据用户类型获取对应的ID
                    if ("user".equals(userType)) {
                        userId = jwtUtil.getUserIdFromToken(jwt);
                        log.info("用户JWT验证成功: email={}, userId={}", email, userId);
                    } else if ("merchant".equals(userType)) {
                        userId = jwtUtil.getMerchantIdFromToken(jwt);
                        log.info("商家JWT验证成功: email={}, merchantId={}", email, userId);
                    } else {
                        log.warn("未知的用户类型: {}", userType);
                        userId = jwtUtil.getUserIdFromToken(jwt); // 默认尝试获取用户ID
                        log.info("默认用户JWT验证: email={}, userId={}", email, userId);
                    }

                    if (userId != null) {
                        // 创建认证对象 - 将userId作为principal，这样SecurityUtils.getCurrentUserId()可以正确获取
                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 设置到安全上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("用户认证设置完成: email={}, userId={}, principal={}", email, userId, authentication.getPrincipal());

                        // 将userId和email添加到请求属性中，供Controller使用
                        request.setAttribute("userId", userId.intValue());
                        request.setAttribute("userEmail", email);
                        request.setAttribute("userType", userType);
                        log.info("请求属性设置完成: userId={}, email={}, userType={}", userId, email, userType);
                    } else {
                        log.warn("无法从JWT中获取用户ID");
                    }
                } else {
                    log.warn("JWT验证失败");
                }
            } else {
                log.warn("请求中未找到JWT token");
            }
        } catch (Exception e) {
            log.error("JWT认证过程中发生异常", e);
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