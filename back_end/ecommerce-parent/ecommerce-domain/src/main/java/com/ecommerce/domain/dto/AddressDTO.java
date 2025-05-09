package com.ecommerce.domain.dto; // 替换为您的实际包名

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 地址数据传输对象 (DTO)
 * 用于创建或更新用户收货地址
 */
@Data
public class AddressDTO {

    /**
     * 收货人姓名
     * - 不能为空白字符串
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名不能超过50个字符")
    private String receiverName;

    /**
     * 收货人联系电话
     * - 不能为空白字符串
     * - (可选) 可以添加正则表达式校验手机号格式
     */
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的中国大陆手机号码") // 简单的手机号正则示例
    @Size(max = 20, message = "手机号码不能超过20个字符")
    private String phoneNumber;

    /**
     * 省份
     * - 不能为空白字符串
     */
    @NotBlank(message = "省份不能为空")
    @Size(max = 50, message = "省份名称不能超过50个字符")
    private String province;

    /**
     * 城市
     * - 不能为空白字符串
     */
    @NotBlank(message = "城市不能为空")
    @Size(max = 50, message = "城市名称不能超过50个字符")
    private String city;

    /**
     * 区/县
     * - 不能为空白字符串
     */
    @NotBlank(message = "区/县不能为空")
    @Size(max = 50, message = "区/县名称不能超过50个字符")
    private String district;

    /**
     * 详细街道门牌号
     * - 不能为空白字符串
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址不能超过255个字符")
    private String detailedAddress;

    /**
     * 是否设置为默认地址
     * - 不能为 null (使用 @NotNull 而不是 @NotBlank)
     */
    @NotNull(message = "请指定是否为默认地址")
    private Boolean isDefault; // 使用 Boolean 类型

}
