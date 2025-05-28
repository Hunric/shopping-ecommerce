package com.hunric.mapper;

import com.hunric.model.SkuInfo;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * SKU信息Mapper接口
 */
@Mapper
public interface SkuInfoMapper {

    /**
     * 插入SKU信息
     */
    @Insert("INSERT INTO sku_info (product_id, sku_name, sale_price, stock_quantity, " +
            "attribute_combination, status, exclusive_image_url, warn_stock, create_time, update_time) " +
            "VALUES (#{spuId}, #{skuName}, #{salePrice}, #{stockQuantity}, " +
            "#{attributeCombination,typeHandler=com.hunric.config.JsonTypeHandler}, #{status}, " +
            "#{exclusiveImageUrl}, #{warnStock}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "skuId")
    int insertSku(SkuInfo skuInfo);

    /**
     * 根据ID查询SKU信息
     */
    @Select("SELECT sku_id, product_id, sku_name, sale_price, stock_quantity, " +
            "attribute_combination, status, exclusive_image_url, warn_stock, create_time, update_time " +
            "FROM sku_info WHERE sku_id = #{skuId}")
    @Results({
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "spuId", column = "product_id"),
        @Result(property = "skuName", column = "sku_name"),
        @Result(property = "salePrice", column = "sale_price"),
        @Result(property = "stockQuantity", column = "stock_quantity"),
        @Result(property = "attributeCombination", column = "attribute_combination", typeHandler = com.hunric.config.JsonTypeHandler.class),
        @Result(property = "exclusiveImageUrl", column = "exclusive_image_url"),
        @Result(property = "warnStock", column = "warn_stock"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    SkuInfo selectSkuById(Integer skuId);

    /**
     * 根据SPU ID查询SKU列表
     */
    @Select("SELECT sku_id, product_id, sku_name, sale_price, stock_quantity, " +
            "attribute_combination, status, exclusive_image_url, warn_stock, create_time, update_time " +
            "FROM sku_info WHERE product_id = #{spuId}")
    @Results({
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "spuId", column = "product_id"),
        @Result(property = "skuName", column = "sku_name"),
        @Result(property = "salePrice", column = "sale_price"),
        @Result(property = "stockQuantity", column = "stock_quantity"),
        @Result(property = "attributeCombination", column = "attribute_combination", typeHandler = com.hunric.config.JsonTypeHandler.class),
        @Result(property = "exclusiveImageUrl", column = "exclusive_image_url"),
        @Result(property = "warnStock", column = "warn_stock"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<SkuInfo> selectSkuListBySpuId(Integer spuId);

    /**
     * 更新SKU信息
     */
    @Update("UPDATE sku_info SET sku_name = #{skuName}, sale_price = #{salePrice}, " +
            "stock_quantity = #{stockQuantity}, " +
            "attribute_combination = #{attributeCombination,typeHandler=com.hunric.config.JsonTypeHandler}, " +
            "status = #{status}, exclusive_image_url = #{exclusiveImageUrl}, " +
            "warn_stock = #{warnStock}, update_time = NOW() WHERE sku_id = #{skuId}")
    int updateSku(SkuInfo skuInfo);

    /**
     * 删除SKU信息
     */
    @Delete("DELETE FROM sku_info WHERE sku_id = #{skuId}")
    int deleteSku(Integer skuId);

    /**
     * 根据SPU ID删除所有SKU
     */
    @Delete("DELETE FROM sku_info WHERE product_id = #{spuId}")
    int deleteSkuBySpuId(Integer spuId);

    /**
     * 批量删除SKU信息
     */
    @Delete("<script>" +
            "DELETE FROM sku_info WHERE sku_id IN " +
            "<foreach collection='skuIds' item='skuId' open='(' separator=',' close=')'>" +
            "#{skuId}" +
            "</foreach>" +
            "</script>")
    int batchDeleteSku(@Param("skuIds") List<Integer> skuIds);

    /**
     * 批量更新SKU价格
     */
    @Update("<script>" +
            "<foreach collection='updates' item='update' separator=';'>" +
            "UPDATE sku_info SET sale_price = #{update.salePrice}, update_time = NOW() " +
            "WHERE sku_id = #{update.skuId}" +
            "</foreach>" +
            "</script>")
    int batchUpdateSkuPrice(@Param("updates") List<SkuPriceUpdate> updates);

    /**
     * 批量更新SKU库存
     */
    @Update("<script>" +
            "<foreach collection='updates' item='update' separator=';'>" +
            "UPDATE sku_info SET stock_quantity = #{update.stockQuantity}, update_time = NOW() " +
            "WHERE sku_id = #{update.skuId}" +
            "</foreach>" +
            "</script>")
    int batchUpdateSkuStock(@Param("updates") List<SkuStockUpdate> updates);

    /**
     * 批量更新SKU状态
     */
    @Update("<script>" +
            "UPDATE sku_info SET status = #{status}, update_time = NOW() WHERE sku_id IN " +
            "<foreach collection='skuIds' item='skuId' open='(' separator=',' close=')'>" +
            "#{skuId}" +
            "</foreach>" +
            "</script>")
    int batchUpdateSkuStatus(@Param("skuIds") List<Integer> skuIds, @Param("status") Integer status);

    /**
     * 检查SKU名称是否存在（在同一SPU内）
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM sku_info WHERE sku_name = #{skuName} AND product_id = #{spuId} " +
            "<if test='skuId != null'>AND sku_id != #{skuId}</if>" +
            "</script>")
    int checkSkuNameExists(@Param("skuName") String skuName, @Param("spuId") Integer spuId, @Param("skuId") Integer skuId);

    /**
     * 获取库存不足的SKU数量
     */
    @Select("SELECT COUNT(*) FROM sku_info s " +
            "INNER JOIN spu_info sp ON s.product_id = sp.product_id " +
            "WHERE sp.store_id = #{storeId} AND s.stock_quantity <= s.warn_stock")
    int getLowStockCount(Integer storeId);

    /**
     * SKU价格更新内部类
     */
    class SkuPriceUpdate {
        private Integer skuId;
        private BigDecimal salePrice;

        public SkuPriceUpdate() {}

        public SkuPriceUpdate(Integer skuId, BigDecimal salePrice) {
            this.skuId = skuId;
            this.salePrice = salePrice;
        }

        public Integer getSkuId() {
            return skuId;
        }

        public void setSkuId(Integer skuId) {
            this.skuId = skuId;
        }

        public BigDecimal getSalePrice() {
            return salePrice;
        }

        public void setSalePrice(BigDecimal salePrice) {
            this.salePrice = salePrice;
        }
    }

    /**
     * SKU库存更新内部类
     */
    class SkuStockUpdate {
        private Integer skuId;
        private Integer stockQuantity;

        public SkuStockUpdate() {}

        public SkuStockUpdate(Integer skuId, Integer stockQuantity) {
            this.skuId = skuId;
            this.stockQuantity = stockQuantity;
        }

        public Integer getSkuId() {
            return skuId;
        }

        public void setSkuId(Integer skuId) {
            this.skuId = skuId;
        }

        public Integer getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
        }
    }
} 