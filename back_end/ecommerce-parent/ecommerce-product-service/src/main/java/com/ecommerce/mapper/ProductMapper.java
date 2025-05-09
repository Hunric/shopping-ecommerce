package com.ecommerce.mapper;

import com.ecommerce.domain.dto.ProductQueryDTO; // Import query DTO
import com.ecommerce.domain.entity.Product;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL; // For SQL builder in provider

import java.util.List;
import java.util.Optional;

/**
 * 商品数据访问接口 (Mapper)
 * 定义与 t_product 表相关的数据库操作方法
 */
@Mapper
public interface ProductMapper {

    /**
     * 插入新商品
     */
    @Insert("INSERT INTO t_product (name, description, category_id, price, stock_quantity, image_url, status, created_at, updated_at) " +
            "VALUES (#{name}, #{description}, #{categoryId}, #{price}, #{stockQuantity}, #{imageUrl}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Product product);

    /**
     * 根据 ID 查找商品
     */
    @Select("SELECT id, name, description, category_id, price, stock_quantity, image_url, status, created_at, updated_at " +
            "FROM t_product WHERE id = #{id}")
    Optional<Product> findById(@Param("id") Long id);

    /**
     * 更新商品信息
     */
    @Update("UPDATE t_product SET name = #{name}, description = #{description}, category_id = #{categoryId}, price = #{price}, " +
            "stock_quantity = #{stockQuantity}, image_url = #{imageUrl}, status = #{status}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(Product product);

    /**
     * 根据 ID 删除商品 (物理删除)
     */
    @Delete("DELETE FROM t_product WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 根据 ID 更新商品状态 (例如，用于逻辑删除或上下架)
     * @param id 商品ID
     * @param status 新的状态值
     * @return 影响行数
     */
    @Update("UPDATE t_product SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);


    /**
     * 根据分类 ID 统计未删除的商品数量
     */
    @Select("SELECT COUNT(*) FROM t_product WHERE category_id = #{categoryId} AND status != 2")
    int countByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 扣减商品库存 (原子操作)
     */
    @Update("UPDATE t_product SET stock_quantity = stock_quantity - #{quantity}, updated_at = NOW() " +
            "WHERE id = #{id} AND stock_quantity >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 增加商品库存
     */
    @Update("UPDATE t_product SET stock_quantity = stock_quantity + #{quantity}, updated_at = NOW() WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 根据查询条件动态查询商品列表 (带分页和排序)
     * 使用 @SelectProvider 指向动态 SQL 构建类
     * @param queryDTO 包含查询、过滤、分页、排序参数的 DTO
     * @return 商品列表
     */
    @SelectProvider(type = ProductSqlProvider.class, method = "buildFindProductsSQL")
    List<Product> findProducts(ProductQueryDTO queryDTO);

    /**
     * 根据查询条件动态统计商品总数 (用于分页)
     * 使用 @SelectProvider 指向动态 SQL 构建类
     * @param queryDTO 包含查询、过滤参数的 DTO
     * @return 符合条件的商品总数
     */
    @SelectProvider(type = ProductSqlProvider.class, method = "buildCountProductsSQL")
    long countProducts(ProductQueryDTO queryDTO);

    /**
     * 检查指定 ID 的商品是否存在
     * @param id 商品 ID
     * @return 存在则返回 true
     */
    @Select("SELECT EXISTS(SELECT 1 FROM t_product WHERE id = #{id})")
    boolean existsById(@Param("id") Long id);


    // --- Dynamic SQL Provider Class ---
    class ProductSqlProvider {

        private static final String TABLE_NAME = "t_product";

        // 构建查询商品列表的动态 SQL
        public String buildFindProductsSQL(ProductQueryDTO dto) {
            // 使用 MyBatis 内置的 SQL 构建器
            return new SQL() {{
                SELECT("id, name, description, category_id, price, stock_quantity, image_url, status, created_at, updated_at");
                FROM(TABLE_NAME);
                applyWhereConditions(this, dto);
                applyOrderBy(this, dto);
                // LIMIT 和 OFFSET
                LIMIT("#{pageSize}"); // Use DTO's pageSize property
                OFFSET("#{offset}"); // Use DTO's getOffset() method (or pass offset directly if preferred)

            }}.toString();
            // Note: MyBatis passes the DTO properties directly using #{propertyName}
            // Ensure ProductQueryDTO has getOffset() and getPageSize() methods.
        }

        // 构建统计商品总数的动态 SQL
        public String buildCountProductsSQL(ProductQueryDTO dto) {
            return new SQL() {{
                SELECT("COUNT(*)");
                FROM(TABLE_NAME);
                applyWhereConditions(this, dto);
            }}.toString();
        }

        // 辅助方法：应用 WHERE 条件
        private void applyWhereConditions(SQL sql, ProductQueryDTO dto) {
            if (dto.getCategoryId() != null) {
                sql.WHERE("category_id = #{categoryId}");
            }
            if (dto.getStatus() != null) {
                sql.WHERE("status = #{status}");
            } else {
                // 默认可能只查询非删除状态的商品？
                sql.WHERE("status != 2"); // Example: Exclude deleted by default if status is not specified
            }
            if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
                // Adapt keyword search as needed (e.g., use CONCAT for LIKE)
                // dto.setKeyword("%" + dto.getKeyword().trim() + "%"); // Prepare keyword in service layer if needed
                sql.WHERE("name LIKE #{keyword}"); // Assumes keyword already has % wildcards if needed
            }
            if (dto.getMinPrice() != null) {
                sql.WHERE("price >= #{minPrice}");
            }
            if (dto.getMaxPrice() != null) {
                sql.WHERE("price <= #{maxPrice}");
            }
            // Add more conditions as needed
        }

        // 辅助方法：应用 ORDER BY 条件
        private void applyOrderBy(SQL sql, ProductQueryDTO dto) {
            String sortBy = dto.getSortBy();
            String sortOrder = dto.getSortOrder() != null && "asc".equalsIgnoreCase(dto.getSortOrder()) ? "ASC" : "DESC"; // Default to DESC

            // Validate sortBy field against allowed columns to prevent SQL injection if sortBy comes directly from user input
            // Basic example allowing specific fields:
            String validSortBy;
            if ("name".equalsIgnoreCase(sortBy)) {
                validSortBy = "name";
            } else if ("price".equalsIgnoreCase(sortBy)) {
                validSortBy = "price";
            } else if ("createdAt".equalsIgnoreCase(sortBy)) {
                validSortBy = "created_at";
            } else {
                // Default sort column if sortBy is invalid or not provided
                validSortBy = "updated_at";
            }
            sql.ORDER_BY(validSortBy + " " + sortOrder);
            // Add secondary sort for consistent ordering if needed
            if (!"id".equals(validSortBy)) {
                sql.ORDER_BY("id DESC"); // Example secondary sort
            }
        }
    }
}
