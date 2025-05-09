package com.ecommerce.controller;

import com.ecommerce.common.ApiResponse;
import com.ecommerce.common.dto.PageResult; // 引入 PageResult
import com.ecommerce.domain.dto.ProductCreateDTO;
import com.ecommerce.domain.dto.ProductDTO;
import com.ecommerce.domain.dto.ProductQueryDTO; // 引入 QueryDTO
import com.ecommerce.domain.dto.ProductUpdateDTO; // 引入 UpdateDTO
import com.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters; // 可选，用于组合多个 Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn; // 用于描述参数位置
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema; // 用于描述参数类型
import io.swagger.v3.oas.annotations.responses.ApiResponses; // 引入 ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max; // 引入校验注解
import jakarta.validation.constraints.Min; // 引入校验注解
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 * 处理与商品相关的 API 请求
 */
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "商品管理接口")
@Validated // 开启方法级别的参数校验
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "创建商品", description = "创建一个新的商品")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @Valid @RequestBody ProductCreateDTO createDTO) {
        ProductDTO createdProduct = productService.createProduct(createDTO);
        ApiResponse<ProductDTO> response = ApiResponse.success(createdProduct, "商品创建成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据商品ID获取单个商品的详细信息")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(
            @Parameter(description = "要获取的商品ID") @PathVariable Long id) {
        ProductDTO productDTO = productService.getProductById(id);
        ApiResponse<ProductDTO> response = ApiResponse.success(productDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "获取商品列表", description = "根据条件分页、过滤、排序查询商品列表")
    @Parameters({
            @Parameter(name = "keyword", description = "搜索关键字 (商品名称)", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "categoryId", description = "分类ID", in = ParameterIn.QUERY, schema = @Schema(type = "integer", format = "int64")),
            @Parameter(name = "minPrice", description = "最低价格", in = ParameterIn.QUERY, schema = @Schema(type = "number", format = "double")),
            @Parameter(name = "maxPrice", description = "最高价格", in = ParameterIn.QUERY, schema = @Schema(type = "number", format = "double")),
            @Parameter(name = "status", description = "商品状态 (1: 上架, 0: 下架)", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "page", description = "页码 (从1开始)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "1")),
            @Parameter(name = "pageSize", description = "每页数量", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(name = "sortBy", description = "排序字段 (如: price, createdAt, updatedAt, name)", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "updatedAt")),
            @Parameter(name = "sortOrder", description = "排序顺序 (asc/desc)", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "desc", allowableValues = {"asc", "desc"}))
    })
    public ResponseEntity<ApiResponse<PageResult<ProductDTO>>> listProducts(
            @Valid ProductQueryDTO queryDTO) {
        PageResult<ProductDTO> pageResult = productService.listProducts(queryDTO);
        ApiResponse<PageResult<ProductDTO>> response = ApiResponse.success(pageResult);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新商品信息
     * @param id 要更新的商品ID
     * @param updateDTO 包含更新信息的DTO (字段可选)
     * @return 更新后的商品信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新商品信息", description = "更新指定ID的商品信息，只更新提供的字段")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @Parameter(description = "要更新的商品ID") @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO updateDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, updateDTO);
        ApiResponse<ProductDTO> response = ApiResponse.success(updatedProduct, "商品更新成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 删除商品
     * @param id 要删除的商品ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "删除指定ID的商品（物理删除或逻辑删除，取决于服务层实现）")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商品未找到", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "要删除的商品ID") @PathVariable Long id) {
        productService.deleteProduct(id); // Service 层会处理 ProductNotFoundException
        // 成功时返回 200 OK 或 204 No Content
        ApiResponse<Void> response = ApiResponse.success(null, "商品删除成功");
        return ResponseEntity.ok(response);
        // 或者: return ResponseEntity.noContent().build(); // 返回 204
    }

    /**
     * 更新商品状态 (上架/下架)
     * @param id 商品ID
     * @param status 更新后的状态值 (请求体中)
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新商品状态", description = "更新指定ID商品的状态 (例如：1-上架, 0-下架)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "状态更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "无效的状态值", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商品未找到", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> updateProductStatus(
            @Parameter(description = "要更新状态的商品ID") @PathVariable Long id,
            @Parameter(description = "新的商品状态 (例如: 1 表示上架, 0 表示下架)", required = true)
            @Min(value = 0, message = "状态值不能小于0") // 基础校验
            @Max(value = 1, message = "状态值不能大于1") // 基础校验 (如果允许删除状态2，需调整)
            @RequestParam Integer status) { // 从请求参数获取 status
        productService.updateProductStatus(id, status);
        ApiResponse<Void> response = ApiResponse.success(null, "商品状态更新成功");
        return ResponseEntity.ok(response);
    }
}