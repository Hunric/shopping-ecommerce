package com.hunric.controller;

import com.hunric.model.dto.MerchantRegisterDTO;
import com.hunric.model.dto.MerchantResponseDTO;
import com.hunric.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 商家控制器
 */
@RestController
@RequestMapping("/api/merchant")
@Slf4j
public class MerchantController {

    @Autowired
    private MerchantService merchantService;
    
    /**
     * 商家注册
     * 
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<MerchantResponseDTO> register(@RequestBody MerchantRegisterDTO registerDTO) {
        log.info("接收到商家注册请求: {}", registerDTO.getMerchantName());
        
        try {
            // 数据校验
            if (registerDTO.getMerchantName() == null || registerDTO.getMerchantName().trim().isEmpty()) {
                return ResponseEntity.ok(MerchantResponseDTO.builder()
                        .success(false)
                        .message("商家名称不能为空")
                        .build());
            }
            
            if (registerDTO.getEmail() == null || registerDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.ok(MerchantResponseDTO.builder()
                        .success(false)
                        .message("邮箱不能为空")
                        .build());
            }
            
            // 邮箱格式验证
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!registerDTO.getEmail().matches(emailRegex)) {
                return ResponseEntity.ok(MerchantResponseDTO.builder()
                        .success(false)
                        .message("邮箱格式不正确")
                        .build());
            }
            
            // 调用服务进行注册
            MerchantResponseDTO response = merchantService.register(registerDTO);
            
            // 记录注册结果
            if (response.getSuccess()) {
                log.info("商家注册成功: {}, ID: {}", registerDTO.getMerchantName(), response.getMerchantId());
            } else {
                log.warn("商家注册失败: {}, 原因: {}", registerDTO.getMerchantName(), response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("商家注册异常", e);
            return ResponseEntity.ok(MerchantResponseDTO.builder()
                    .success(false)
                    .message("注册过程中发生异常: " + e.getMessage())
                    .build());
        }
    }
} 