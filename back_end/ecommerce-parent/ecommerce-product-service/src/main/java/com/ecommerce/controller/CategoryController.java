package com.ecommerce.controller;

import com.ecommerce.common.ApiResponse; // Import common response structure
import com.ecommerce.domain.dto.CategoryCreateDTO;
import com.ecommerce.domain.dto.CategoryDTO;
import com.ecommerce.domain.dto.CategoryUpdateDTO;
import com.ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation; // OpenAPI annotations
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // For request body validation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated; // Enable validation
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 * 处理与商品分类相关的 API 请求
 */
@RestController
@RequestMapping("/api/v1/categories") // Base path for category APIs
@Tag(name = "Category Management", description = "商品分类管理接口")
@Validated // Enables validation for request parameters and path variables if needed
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 创建新的商品分类
     *
     * @param createDTO 包含新分类信息的 DTO
     * @return 创建成功的分类信息
     */
    @PostMapping
    @Operation(summary = "创建商品分类", description = "创建一个新的顶级或子级商品分类")
    // TODO: Add @PreAuthorize("hasRole('ADMIN')") or similar for access control
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(
            @Valid @RequestBody CategoryCreateDTO createDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(createDTO);
        ApiResponse<CategoryDTO> response = ApiResponse.success(createdCategory, "分类创建成功");
        // Return 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 获取商品分类树
     *
     * @return 嵌套的分类列表
     */
    @GetMapping("/tree")
    @Operation(summary = "获取分类树", description = "获取所有商品分类，并以树状结构返回")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategoryTree() {
        List<CategoryDTO> categoryTree = categoryService.getAllCategoriesAsTree();
        ApiResponse<List<CategoryDTO>> response = ApiResponse.success(categoryTree);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取子分类列表（或顶级分类）
     *
     * @param parentId 父分类ID (可选, 不传或传 null 获取顶级分类)
     * @return 分类列表
     */
    @GetMapping
    @Operation(summary = "获取子分类列表", description = "根据父分类ID获取其直接子分类列表，不传则获取顶级分类")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getSubCategories(
            @Parameter(description = "父分类ID (不传获取顶级分类)") @RequestParam(required = false) Long parentId) {
        List<CategoryDTO> categories = categoryService.getSubCategories(parentId);
        ApiResponse<List<CategoryDTO>> response = ApiResponse.success(categories);
        return ResponseEntity.ok(response);
    }


    /**
     * 根据 ID 获取分类详情
     *
     * @param id 分类 ID
     * @return 分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "根据分类ID获取单个分类的详细信息")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(
            @Parameter(description = "要获取的分类ID") @PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        ApiResponse<CategoryDTO> response = ApiResponse.success(category);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新商品分类
     *
     * @param id        要更新的分类 ID
     * @param updateDTO 包含更新信息的 DTO
     * @return 更新后的分类信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新商品分类", description = "根据分类ID更新分类信息")
    // TODO: Add @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @Parameter(description = "要更新的分类ID") @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateDTO updateDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, updateDTO);
        ApiResponse<CategoryDTO> response = ApiResponse.success(updatedCategory, "分类更新成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 删除商品分类
     *
     * @param id 要删除的分类 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品分类", description = "根据分类ID删除分类（如果符合删除条件）")
    // TODO: Add @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "要删除的分类ID") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        ApiResponse<Void> response = ApiResponse.success(null, "分类删除成功"); // Or just ApiResponse.success()
        return ResponseEntity.ok(response); // Return 200 OK with success message
        // Alternatively, return ResponseEntity.noContent().build(); // Return 204 No Content
    }

}