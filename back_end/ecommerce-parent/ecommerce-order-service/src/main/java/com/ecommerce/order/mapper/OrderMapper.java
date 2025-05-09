package com.ecommerce.order.mapper; // 假设在 order 服务的 mapper 包下

import com.ecommerce.domain.entity.Order;
import com.ecommerce.domain.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口 (Mapper)
 */
@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO t_order (order_no, user_id, total_amount, payable_amount, status, " +
            "receiver_name, receiver_phone, receiver_address, payment_method, " +
            "created_at, updated_at) " +
            "VALUES (#{orderNo}, #{userId}, #{totalAmount}, #{payableAmount}, #{status}, " +
            "#{receiverName}, #{receiverPhone}, #{receiverAddress}, #{paymentMethod}, " +
            "#{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Order order);

    @Select("SELECT * FROM t_order WHERE id = #{id}")
    Optional<Order> findById(@Param("id") Long id);

    @Select("SELECT * FROM t_order WHERE order_no = #{orderNo}")
    Optional<Order> findByOrderNo(@Param("orderNo") String orderNo);

    @Select("SELECT * FROM t_order WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> findByUserId(@Param("userId") Long userId);

    @Update("UPDATE t_order SET status = #{status}, paid_at = #{paidAt}, " +
            "shipped_at = #{shippedAt}, completed_at = #{completedAt}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(Order order); // 可根据需要创建更具体的更新方法，如只更新状态

    @Update("UPDATE t_order SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

}