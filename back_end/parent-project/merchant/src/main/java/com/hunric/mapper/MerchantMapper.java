package com.hunric.mapper;

import com.hunric.model.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import java.util.List;

/**
 * 商家数据访问接口
 */
@Mapper
public interface MerchantMapper {
    
    /**
     * 插入新商家
     * @param merchant 商家对象
     * @return 影响的行数
     */
    int insert(Merchant merchant);
    
    /**
     * 更新商家信息
     * @param merchant 商家对象
     * @return 影响的行数
     */
    int update(Merchant merchant);
    
    /**
     * 根据ID查询商家
     * @param merchantId 商家ID
     * @return 商家对象
     */
    Merchant selectById(@Param("merchantId") Long merchantId);
    
    /**
     * 根据邮箱查询商家
     * @param email 邮箱
     * @return 商家对象
     */
    Merchant selectByEmail(@Param("email") String email);
    
    /**
     * 根据商家名称查询商家
     * @param merchantName 商家名称
     * @return 商家对象
     */
    Merchant selectByMerchantName(@Param("merchantName") String merchantName);
    
    /**
     * 根据营业执照编号查询商家
     * @param businessLicenseNo 营业执照编号
     * @return 商家对象
     */
    Merchant selectByBusinessLicenseNo(@Param("businessLicenseNo") String businessLicenseNo);
    
    /**
     * 根据法人身份证号查询商家
     * @param legalPersonIdCard 法人身份证号
     * @return 商家对象
     */
    Merchant selectByLegalPersonIdCard(@Param("legalPersonIdCard") String legalPersonIdCard);
    
    /**
     * 根据条件查询商家列表
     * @param merchant 查询条件
     * @return 商家列表
     */
    List<Merchant> selectList(Merchant merchant);
    
    /**
     * 更新商家密码
     * @param merchant 商家对象
     * @return 影响行数
     */
    @Update("UPDATE merchant_info SET password = #{password} WHERE merchant_id = #{merchantId}")
    int updatePassword(Merchant merchant);
} 