package com.ecommerce.order.mapper;

import com.ecommerce.domain.entity.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// --- OrderItemMapper.java ---
@Mapper
public interface OrderItemMapper {

    /**
     * 批量插入订单项
     * @param items 订单项列表
     * @return 影响行数
     */
    @Insert("<script>" +
            "INSERT INTO t_order_item (order_id, product_id, product_name, product_image_url, unit_price, quantity, total_price) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.orderId}, #{item.productId}, #{item.productName}, #{item.productImageUrl}, #{item.unitPrice}, #{item.quantity}, #{item.totalPrice})" +
            "</foreach>" +
            "</script>")
    int insertBatch(List<OrderItem> items);

    @Select("SELECT * FROM t_order_item WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
}

