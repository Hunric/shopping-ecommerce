package com.ecommerce.controller; // 替换为您的实际包名

import com.ecommerce.common.ApiResponse; // 导入统一响应体
import com.ecommerce.domain.dto.AddressDTO;
import com.ecommerce.domain.entity.Address;
import com.ecommerce.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication; // 用于获取当前登录用户信息
// import org.springframework.security.core.context.SecurityContextHolder; // 用于获取当前登录用户信息
// import org.springframework.security.core.userdetails.UserDetails; // 用于获取当前登录用户信息
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 * 处理用户收货地址相关的 API 请求
 * 注意：所有地址操作都需要用户已登录
 */
@RestController
@RequestMapping("/api/v1/addresses") // 基础路径
@Tag(name = "Address Management", description = "用户收货地址管理接口")
@Validated
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * 获取当前登录用户的所有地址列表
     * @return 地址列表响应
     */
    @GetMapping // 处理 GET /api/v1/addresses 请求
    @Operation(summary = "获取用户地址列表", description = "获取当前登录用户的所有收货地址")
    public ResponseEntity<ApiResponse<List<Address>>> getUserAddresses() {
        // TODO: 获取当前登录用户的 ID
        // 在配置了 Spring Security 和 JWT 后，可以通过 SecurityContextHolder 获取用户信息
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String currentPrincipalName = authentication.getName(); // 通常是用户名
        // UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // 更详细信息
        // Long userId = ... ; // 需要一种方式从 Principal 或 UserDetails 获取用户 ID (例如，在 UserDetails 实现中添加 getId 方法)

        // 临时硬编码用户 ID，需要替换为实际获取逻辑
        Long userId = 1L; // ！！！【注意】这里需要替换为从安全上下文中获取的真实用户 ID ！！！

        List<Address> addresses = addressService.listAddressesByUserId(userId);
        ApiResponse<List<Address>> response = ApiResponse.success(addresses);
        return ResponseEntity.ok(response);
    }

    /**
     * 添加新地址
     * @param addressDTO 新地址信息
     * @return 创建成功的地址信息响应
     */
    @PostMapping // 处理 POST /api/v1/addresses 请求
    @Operation(summary = "添加新地址", description = "为当前登录用户添加一个新的收货地址")
    public ResponseEntity<ApiResponse<Address>> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        // TODO: 获取当前登录用户的 ID (同上)
        Long userId = 1L; // ！！！【注意】替换为真实用户 ID 获取逻辑 ！！！

        Address createdAddress = addressService.addAddress(userId, addressDTO);
        ApiResponse<Address> response = ApiResponse.success(createdAddress, "地址添加成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 更新指定 ID 的地址
     * @param addressId 路径变量，要更新的地址 ID
     * @param addressDTO 更新后的地址信息
     * @return 更新成功的地址信息响应
     */
    @PutMapping("/{addressId}") // 处理 PUT /api/v1/addresses/{addressId} 请求
    @Operation(summary = "更新地址", description = "更新当前登录用户指定的收货地址")
    public ResponseEntity<ApiResponse<Address>> updateAddress(
            @Parameter(description = "要更新的地址ID") @PathVariable Long addressId, // @PathVariable 从路径中获取参数
            @Valid @RequestBody AddressDTO addressDTO) {

        // TODO: 获取当前登录用户的 ID (同上)
        Long userId = 1L; // ！！！【注意】替换为真实用户 ID 获取逻辑 ！！！

        Address updatedAddress = addressService.updateAddress(userId, addressId, addressDTO);
        ApiResponse<Address> response = ApiResponse.success(updatedAddress, "地址更新成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 删除指定 ID 的地址
     * @param addressId 路径变量，要删除的地址 ID
     * @return 无数据体的成功响应
     */
    @DeleteMapping("/{addressId}") // 处理 DELETE /api/v1/addresses/{addressId} 请求
    @Operation(summary = "删除地址", description = "删除当前登录用户指定的收货地址")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @Parameter(description = "要删除的地址ID") @PathVariable Long addressId) {

        // TODO: 获取当前登录用户的 ID (同上)
        Long userId = 1L; // ！！！【注意】替换为真实用户 ID 获取逻辑 ！！！

        addressService.deleteAddress(userId, addressId);
        ApiResponse<Void> response = ApiResponse.success(null, "地址删除成功"); // 成功时 data 可以为 null
        // 通常删除成功返回 204 No Content，但也可以返回 200 OK 带消息体
        return ResponseEntity.ok(response);
        // 或者 return ResponseEntity.noContent().build(); // 返回 204
    }

    /**
     * 设置指定 ID 的地址为默认地址
     * @param addressId 路径变量，要设为默认的地址 ID
     * @return 无数据体的成功响应
     */
    @PutMapping("/{addressId}/default") // 处理 PUT /api/v1/addresses/{addressId}/default 请求
    @Operation(summary = "设置默认地址", description = "将当前登录用户指定的地址设为默认收货地址")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(
            @Parameter(description = "要设为默认的地址ID") @PathVariable Long addressId) {

        // TODO: 获取当前登录用户的 ID (同上)
        Long userId = 1L; // ！！！【注意】替换为真实用户 ID 获取逻辑 ！！！

        addressService.setDefaultAddress(userId, addressId);
        ApiResponse<Void> response = ApiResponse.success(null, "默认地址设置成功");
        return ResponseEntity.ok(response);
    }
}
