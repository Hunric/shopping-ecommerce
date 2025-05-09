package com.ecommerce.mapper;

import com.ecommerce.domain.entity.Category; // 假设 Category 实体在 domain 包下
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * 商品分类数据访问接口 (Mapper)
 * 定义与 t_category 表相关的数据库操作方法
 */
@Mapper // 标记为 MyBatis Mapper 接口
public interface CategoryMapper {

    /**
     * 插入新的商品分类
     *
     * @param category 包含分类信息的对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO t_category (name, parent_id, sort_order, created_at, updated_at) " +
            "VALUES (#{name}, #{parentId}, #{sortOrder}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Category category);

    /**
     * 根据 ID 查找分类
     *
     * @param id 分类 ID
     * @return Optional<Category>
     */
    @Select("SELECT id, name, parent_id, sort_order, created_at, updated_at FROM t_category WHERE id = #{id}")
    Optional<Category> findById(@Param("id") Long id);

    /**
     * 根据父分类 ID 查找直接子分类列表
     * 注意：查询顶级分类时，需要特殊处理 parentId 为 null 的情况
     *
     * @param parentId 父分类 ID (可以为 null)
     * @return 子分类列表
     */
    // 使用 <if> 处理 null，推荐在 XML 中实现更复杂的逻辑
    // 这里是一个简化的注解示例，可能需要调整以正确处理 null
    @Select("<script>" +
            "SELECT id, name, parent_id, sort_order, created_at, updated_at FROM t_category " +
            "WHERE " +
            "<if test='parentId == null'>parent_id IS NULL</if>" +
            "<if test='parentId != null'>parent_id = #{parentId}</if>" +
            " ORDER BY sort_order ASC, name ASC" +
            "</script>")
    List<Category> findByParentId(@Param("parentId") Long parentId);

    /**
     * 查找所有分类
     *
     * @return 所有分类的列表
     */
    @Select("SELECT id, name, parent_id, sort_order, created_at, updated_at FROM t_category ORDER BY parent_id ASC, sort_order ASC, name ASC")
    List<Category> findAll();

    /**
     * 更新商品分类信息
     *
     * @param category 包含更新后信息的分类对象 (必须包含 id)
     * @return 影响的行数
     */
    @Update("UPDATE t_category SET name = #{name}, parent_id = #{parentId}, sort_order = #{sortOrder}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Category category);

    /**
     * 根据 ID 删除分类
     * 注意：通常在 Service 层需要检查该分类下是否有子分类或商品，再决定是否删除
     *
     * @param id 分类 ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM t_category WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 检查指定分类下是否存在子分类
     *
     * @param id 分类 ID
     * @return 如果存在子分类则返回大于 0 的数，否则返回 0
     */
    @Select("SELECT COUNT(*) FROM t_category WHERE parent_id = #{id}")
    int countChildrenById(@Param("id") Long id);

    /**
     * 根据名称和父ID查找分类 (用于校验同级下名称是否重复)
     *
     * @param name 分类名称
     * @param parentId 父分类 ID (null 表示顶级)
     * @param excludeId 要排除的分类 ID (更新时排除自身)
     * @return Optional<Category>
     */
    @SelectProvider(type = CategorySqlProvider.class, method = "findByNameAndParentIdExcludingId")
    Optional<Category> findByNameAndParentIdExcludingId(@Param("name") String name, @Param("parentId") Long parentId, @Param("excludeId") Long excludeId);

    // --- (可选) 辅助方法 ---

    /**
     * 检查指定 ID 的分类是否存在
     * @param id 分类 ID
     * @return 存在则返回 true
     */
    @Select("SELECT EXISTS(SELECT 1 FROM t_category WHERE id = #{id})")
    boolean existsById(@Param("id") Long id);


    // SQL Provider class for complex queries
    class CategorySqlProvider {
        public String findByNameAndParentIdExcludingId(@Param("name") String name, @Param("parentId") Long parentId, @Param("excludeId") Long excludeId) {
            StringBuilder sql = new StringBuilder("SELECT id, name, parent_id, sort_order, created_at, updated_at FROM t_category WHERE name = #{name}");
            if (parentId == null) {
                sql.append(" AND parent_id IS NULL");
            } else {
                sql.append(" AND parent_id = #{parentId}");
            }
            if (excludeId != null) {
                sql.append(" AND id != #{excludeId}");
            }
            sql.append(" LIMIT 1");
            return sql.toString();
        }
    }
}