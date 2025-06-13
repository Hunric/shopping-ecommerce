package com.hunric.controller;

import com.hunric.common.model.ApiResponse;
import com.hunric.model.SpuInfo;
import com.hunric.model.dto.SpuCreateDTO;
import com.hunric.model.dto.SpuStatsDTO;
import com.hunric.model.dto.SkuCreateDTO;
import com.hunric.service.SpuInfoService;
import com.hunric.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * SPU信息控制器
 */
@RestController
@RequestMapping("/api/merchant/spu")
@Slf4j
public class SpuInfoController {

    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 创建SPU
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSpu(@RequestBody SpuCreateDTO spuCreateDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            SpuInfo spuInfo = spuInfoService.createSpu(spuCreateDTO);
            response.put("success", true);
            response.put("message", "创建SPU成功");
            response.put("data", spuInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量创建SPU
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchCreateSpu(@RequestBody List<SpuCreateDTO> spuCreateDTOList) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SpuInfo> spuInfoList = spuInfoService.batchCreateSpu(spuCreateDTOList);
            response.put("success", true);
            response.put("message", "批量创建SPU成功");
            response.put("data", spuInfoList);
            response.put("totalCreated", spuInfoList.size());
            response.put("totalRequested", spuCreateDTOList.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量创建SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 下载商品Excel导入模板
     */
    @GetMapping("/template/download")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            // 创建Excel工作簿
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("商品数据");
            
            // 创建表头样式
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            // 创建表头行
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "商品名称(必填)", "商品描述", "分类ID(必填)", "展示价格(必填)", 
                "品牌名称", "卖点描述", "计量单位(必填)", "商品图片链接",
                "SKU名称(必填)", "SKU价格(必填)", "SKU库存(必填)", "警戒库存", "属性组合(JSON格式)"
            };
            
            // 设置列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 5000);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 添加示例数据
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("示例商品");
            exampleRow.createCell(1).setCellValue("这是一个示例商品描述");
            exampleRow.createCell(2).setCellValue(1);  // 分类ID
            exampleRow.createCell(3).setCellValue(199.99);
            exampleRow.createCell(4).setCellValue("示例品牌");
            exampleRow.createCell(5).setCellValue("这是商品卖点");
            exampleRow.createCell(6).setCellValue("件");
            exampleRow.createCell(7).setCellValue("https://example.com/image.jpg");
            exampleRow.createCell(8).setCellValue("示例SKU-红色-L");
            exampleRow.createCell(9).setCellValue(199.99);
            exampleRow.createCell(10).setCellValue(100);
            exampleRow.createCell(11).setCellValue(10);
            exampleRow.createCell(12).setCellValue("{\"颜色\":\"红色\",\"尺寸\":\"L\"}");
            
            // 添加说明行
            Row noteRow = sheet.createRow(3);
            Cell noteCell = noteRow.createCell(0);
            noteCell.setCellValue("注意：一个商品可以有多个SKU，每个SKU占一行。相同商品名称的行将被视为同一个商品的不同SKU。");
            
            // 将工作簿写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            // 设置响应头
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            responseHeaders.setContentDispositionFormData("attachment", "product_template.xlsx");
            
            return new ResponseEntity<>(outputStream.toByteArray(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.error("生成模板文件失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 解析上传的Excel模板
     */
    @PostMapping("/parse-template")
    public ResponseEntity<Map<String, Object>> parseTemplate(@RequestParam("file") MultipartFile file,
                                                           @RequestParam("storeId") Integer storeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("开始解析Excel模板文件: 文件名={}, 大小={}KB, 内容类型={}, 店铺ID={}", 
                    file.getOriginalFilename(), file.getSize()/1024, file.getContentType(), storeId);
            
            // 检查参数
            if (file == null || file.isEmpty()) {
                log.error("文件为空");
                response.put("success", false);
                response.put("message", "请上传文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (storeId == null || storeId <= 0) {
                log.error("店铺ID无效: {}", storeId);
                response.put("success", false);
                response.put("message", "店铺ID无效");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 检查文件是否为Excel
            if (!file.getOriginalFilename().endsWith(".xlsx") && !file.getOriginalFilename().endsWith(".xls")) {
                log.error("文件格式不正确: {}", file.getOriginalFilename());
                response.put("success", false);
                response.put("message", "请上传Excel文件(.xlsx或.xls)");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 解析Excel文件
            Workbook workbook;
            try {
                if (file.getOriginalFilename().endsWith(".xlsx")) {
                    workbook = new XSSFWorkbook(file.getInputStream());
                    log.info("成功创建XLSX工作簿");
                } else {
                    workbook = new HSSFWorkbook(file.getInputStream());
                    log.info("成功创建XLS工作簿");
                }
            } catch (Exception e) {
                log.error("读取Excel文件失败", e);
                response.put("success", false);
                response.put("message", "读取Excel文件失败: " + e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                log.error("Excel文件不包含工作表");
                response.put("success", false);
                response.put("message", "Excel文件不包含工作表");
                workbook.close();
                return ResponseEntity.badRequest().body(response);
            }
            
            log.info("工作表名称: {}, 行数: {}", sheet.getSheetName(), sheet.getLastRowNum() + 1);
            
            // 获取表头行
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                log.error("Excel文件格式不正确，缺少表头");
                response.put("success", false);
                response.put("message", "Excel文件格式不正确，缺少表头");
                workbook.close();
                return ResponseEntity.badRequest().body(response);
            }
            
            // 检查表头是否正确
            String[] expectedHeaders = {
                "商品名称(必填)", "商品描述", "分类ID(必填)", "展示价格(必填)", 
                "品牌名称", "卖点描述", "计量单位(必填)", "商品图片链接",
                "SKU名称(必填)", "SKU价格(必填)", "SKU库存(必填)", "警戒库存", "属性组合(JSON格式)"
            };
            
            boolean headerValid = true;
            StringBuilder actualHeaders = new StringBuilder();
            for (int i = 0; i < expectedHeaders.length; i++) {
                Cell cell = headerRow.getCell(i);
                String actualHeader = cell == null ? "null" : cell.getStringCellValue();
                actualHeaders.append(actualHeader).append(", ");
                if (cell == null || !expectedHeaders[i].equals(actualHeader)) {
                    headerValid = false;
                }
            }
            
            if (!headerValid) {
                log.error("表头格式不符合模板要求, 实际表头: {}", actualHeaders.toString());
                response.put("success", false);
                response.put("message", "表头格式不符合模板要求，请使用下载的模板");
                workbook.close();
                return ResponseEntity.badRequest().body(response);
            }
            
            log.info("开始处理数据行，总行数: {}", sheet.getLastRowNum());
            
            // 存储解析后的商品数据，按商品名称分组
            Map<String, SpuCreateDTO> spuMap = new HashMap<>();
            int totalRows = 0;
            int processedRows = 0;
            int errorRows = 0;
            
            // 从第一行开始解析数据（跳过表头，包含示例行）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                // 跳过空行或说明行
                Cell firstCell = row.getCell(0);
                if (firstCell == null || firstCell.toString().trim().isEmpty() || 
                    firstCell.toString().startsWith("注意")) {
                    continue;
                }
                
                try {
                    // 读取每一列的数据
                    String spuName = getCellStringValue(row.getCell(0));
                    String spuDescription = getCellStringValue(row.getCell(1));
                    String categoryIdStr = getCellStringValue(row.getCell(2));
                    String displayPriceStr = getCellStringValue(row.getCell(3));
                    String brandName = getCellStringValue(row.getCell(4));
                    String sellingPoint = getCellStringValue(row.getCell(5));
                    String unit = getCellStringValue(row.getCell(6));
                    String productImage = getCellStringValue(row.getCell(7));
                    String skuName = getCellStringValue(row.getCell(8));
                    String skuPriceStr = getCellStringValue(row.getCell(9));
                    String stockQuantityStr = getCellStringValue(row.getCell(10));
                    String warnStockStr = getCellStringValue(row.getCell(11));
                    String attributeJson = getCellStringValue(row.getCell(12));
                    
                    // 转换数据类型
                    Integer categoryId = null;
                    BigDecimal displayPrice = null;
                    BigDecimal skuPrice = null;
                    Integer stockQuantity = 0;
                    Integer warnStock = 10; // 默认警戒库存
                    
                    try {
                        categoryId = categoryIdStr == null ? null : Integer.parseInt(categoryIdStr.trim());
                    } catch (NumberFormatException e) {
                        log.warn("第{}行的分类ID格式不正确: {}", i+1, categoryIdStr);
                        throw new IllegalArgumentException("第" + (i+1) + "行：分类ID必须是数字");
                    }
                    
                    try {
                        if (displayPriceStr != null && !displayPriceStr.trim().isEmpty()) {
                            displayPrice = new BigDecimal(displayPriceStr.trim());
                        }
                    } catch (NumberFormatException e) {
                        log.warn("第{}行的展示价格格式不正确: {}", i+1, displayPriceStr);
                        throw new IllegalArgumentException("第" + (i+1) + "行：展示价格格式不正确");
                    }
                    
                    try {
                        if (skuPriceStr != null && !skuPriceStr.trim().isEmpty()) {
                            skuPrice = new BigDecimal(skuPriceStr.trim());
                        }
                    } catch (NumberFormatException e) {
                        log.warn("第{}行的SKU价格格式不正确: {}", i+1, skuPriceStr);
                        throw new IllegalArgumentException("第" + (i+1) + "行：SKU价格格式不正确");
                    }
                    
                    try {
                        if (stockQuantityStr != null && !stockQuantityStr.trim().isEmpty()) {
                            stockQuantity = Integer.parseInt(stockQuantityStr.trim());
                        }
                    } catch (NumberFormatException e) {
                        log.warn("第{}行的库存数量格式不正确: {}", i+1, stockQuantityStr);
                        throw new IllegalArgumentException("第" + (i+1) + "行：库存数量必须是整数");
                    }
                    
                    try {
                        if (warnStockStr != null && !warnStockStr.trim().isEmpty()) {
                            warnStock = Integer.parseInt(warnStockStr.trim());
                        }
                    } catch (NumberFormatException e) {
                        // 使用默认值
                        warnStock = 10;
                    }
                    
                    // 验证必填字段
                    if (spuName == null || spuName.trim().isEmpty()) {
                        throw new IllegalArgumentException("第" + (i+1) + "行：商品名称不能为空");
                    }
                    if (categoryId == null || categoryId <= 0) {
                        throw new IllegalArgumentException("第" + (i+1) + "行：分类ID无效");
                    }
                    if (displayPrice == null || displayPrice.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("第" + (i+1) + "行：展示价格必须大于0");
                    }
                    if (unit == null || unit.trim().isEmpty()) {
                        unit = "件"; // 默认单位
                    }
                    if (skuName == null || skuName.trim().isEmpty()) {
                        throw new IllegalArgumentException("第" + (i+1) + "行：SKU名称不能为空");
                    }
                    if (skuPrice == null || skuPrice.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("第" + (i+1) + "行：SKU价格必须大于0");
                    }
                    if (stockQuantity < 0) {
                        throw new IllegalArgumentException("第" + (i+1) + "行：库存不能小于0");
                    }
                    
                    // 解析属性组合JSON
                    Map<String, Object> attributeCombination = new HashMap<>();
                    if (attributeJson != null && !attributeJson.isEmpty()) {
                        try {
                            attributeCombination = new ObjectMapper().readValue(attributeJson, Map.class);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("第" + (i+1) + "行：属性组合JSON格式不正确");
                        }
                    }
                    
                    // 创建或获取SPU对象
                    SpuCreateDTO spuDTO;
                    if (spuMap.containsKey(spuName)) {
                        spuDTO = spuMap.get(spuName);
                    } else {
                        spuDTO = new SpuCreateDTO();
                        spuDTO.setMerchantId(spuInfoService.getStoreMerchantId(storeId));
                        spuDTO.setStoreId(storeId);
                        spuDTO.setCategoryId(categoryId);
                        spuDTO.setSpuName(spuName);
                        spuDTO.setSpuDescription(spuDescription);
                        spuDTO.setProductImage(productImage);
                        spuDTO.setDisplayPrice(displayPrice);
                        spuDTO.setBrandName(brandName);
                        spuDTO.setSellingPoint(sellingPoint);
                        spuDTO.setUnit(unit);
                        spuDTO.setStatus("draft");
                        spuDTO.setBasicAttributes(new HashMap<>());
                        spuDTO.setNonBasicAttributes(new HashMap<>());
                        spuDTO.setSkus(new ArrayList<>());
                        
                        spuMap.put(spuName, spuDTO);
                    }
                    
                    // 创建SKU
                    SkuCreateDTO skuDTO = new SkuCreateDTO();
                    skuDTO.setSkuName(skuName);
                    skuDTO.setSalePrice(skuPrice);
                    skuDTO.setStockQuantity(stockQuantity);
                    skuDTO.setAttributeCombination(attributeCombination);
                    skuDTO.setWarnStock(warnStock);
                    skuDTO.setStatus(1); // 1表示上架状态
                    
                    // 添加SKU到SPU
                    spuDTO.getSkus().add(skuDTO);
                    processedRows++;
                    
                } catch (Exception e) {
                    // 记录错误但继续处理
                    errorRows++;
                    log.error("解析第{}行出错: {}", i+1, e.getMessage());
                }
            }
            
            workbook.close();
            
            log.info("Excel处理完成: 总行数={}, 成功行数={}, 错误行数={}, 解析出商品数={}", 
                    totalRows, processedRows, errorRows, spuMap.size());
            
            // 检查是否解析到商品
            if (spuMap.isEmpty()) {
                response.put("success", false);
                response.put("message", "没有解析到有效的商品数据");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 返回解析结果
            List<SpuCreateDTO> spuList = new ArrayList<>(spuMap.values());
            response.put("success", true);
            response.put("message", "解析成功，共" + spuList.size() + "个商品");
            response.put("data", spuList);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("解析Excel模板失败", e);
            response.put("success", false);
            response.put("message", "解析失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 辅助方法：获取单元格的字符串值
    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 对于数字，去掉可能的小数点后的零
                    double value = cell.getNumericCellValue();
                    if (value == (long) value) {
                        return String.valueOf((long) value);
                    } else {
                        return String.valueOf(value);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return cell.getCellFormula();
                    }
                }
            default:
                return "";
        }
    }
    
    // 辅助方法：获取单元格的数值
    private double getCellNumericValue(Cell cell) {
        if (cell == null) return 0;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            case BOOLEAN:
                return cell.getBooleanCellValue() ? 1 : 0;
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (Exception e) {
                    try {
                        return Double.parseDouble(cell.getStringCellValue().trim());
                    } catch (Exception ex) {
                        return 0;
                    }
                }
            default:
                return 0;
        }
    }

    /**
     * 根据ID获取SPU信息
     */
    @GetMapping("/{spuId}")
    public ResponseEntity<Map<String, Object>> getSpuById(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            SpuInfo spuInfo = spuInfoService.getSpuById(spuId);
            if (spuInfo != null) {
                response.put("success", true);
                response.put("data", spuInfo);
            } else {
                response.put("success", false);
                response.put("message", "SPU不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新SPU信息
     */
    @PutMapping("/{spuId}")
    public ResponseEntity<Map<String, Object>> updateSpu(@PathVariable Integer spuId, 
                                                         @RequestBody SpuCreateDTO spuCreateDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            SpuInfo spuInfo = spuInfoService.updateSpu(spuId, spuCreateDTO);
            response.put("success", true);
            response.put("message", "更新SPU成功");
            response.put("data", spuInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除SPU
     */
    @DeleteMapping("/{spuId}")
    public ResponseEntity<Map<String, Object>> deleteSpu(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = spuInfoService.deleteSpu(spuId);
            response.put("success", success);
            response.put("message", success ? "删除SPU成功" : "删除SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量删除SPU
     */
    @DeleteMapping("/batch-delete")
    public ResponseEntity<Map<String, Object>> batchDeleteSpu(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> spuIds = (List<Integer>) request.get("spuIds");
            boolean success = spuInfoService.batchDeleteSpu(spuIds);
            response.put("success", success);
            response.put("message", success ? "批量删除SPU成功" : "批量删除SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量删除SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 分页查询SPU列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getSpuList(
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = spuInfoService.getSpuList(storeId, categoryId, keyword, 
                                                                   status, page, pageSize, sortBy, sortOrder);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取SPU列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 分页查询SPU列表（简化路径）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSpuListSimple(
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        return getSpuList(storeId, categoryId, keyword, status, page, pageSize, sortBy, sortOrder);
    }

    /**
     * 获取SPU统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSpuStats(@RequestParam Integer storeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            SpuStatsDTO stats = spuInfoService.getSpuStats(storeId);
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取SPU统计失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 上架SPU
     */
    @PostMapping("/{spuId}/publish")
    public ResponseEntity<Map<String, Object>> publishSpu(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = spuInfoService.publishSpu(spuId);
            response.put("success", success);
            response.put("message", success ? "上架SPU成功" : "上架SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "上架SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 下架SPU
     */
    @PostMapping("/{spuId}/unpublish")
    public ResponseEntity<Map<String, Object>> unpublishSpu(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = spuInfoService.unpublishSpu(spuId);
            response.put("success", success);
            response.put("message", success ? "下架SPU成功" : "下架SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "下架SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量上架SPU
     */
    @PostMapping("/batch-publish")
    public ResponseEntity<Map<String, Object>> batchPublishSpu(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> spuIds = (List<Integer>) request.get("spuIds");
            boolean success = spuInfoService.batchPublishSpu(spuIds);
            response.put("success", success);
            response.put("message", success ? "批量上架SPU成功" : "批量上架SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量上架SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量下架SPU
     */
    @PostMapping("/batch-unpublish")
    public ResponseEntity<Map<String, Object>> batchUnpublishSpu(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> spuIds = (List<Integer>) request.get("spuIds");
            boolean success = spuInfoService.batchUnpublishSpu(spuIds);
            response.put("success", success);
            response.put("message", success ? "批量下架SPU成功" : "批量下架SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量下架SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 复制SPU
     */
    @PostMapping("/{spuId}/copy")
    public ResponseEntity<Map<String, Object>> copySpu(@PathVariable Integer spuId, 
                                                       @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String newName = (String) request.get("newName");
            SpuInfo spuInfo = spuInfoService.copySpu(spuId, newName);
            response.put("success", true);
            response.put("message", "复制SPU成功");
            response.put("data", spuInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "复制SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 生成SKU组合
     */
    @PostMapping("/generate-sku-combinations")
    public ResponseEntity<Map<String, Object>> generateSkuCombinations(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> basicAttributes = (Map<String, Object>) request.get("basicAttributes");
            List<Map<String, Object>> combinations = spuInfoService.generateSkuCombinations(basicAttributes);
            response.put("success", true);
            response.put("data", combinations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "生成SKU组合失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // SKU相关API（通过SPU路径访问）
    
    /**
     * 获取指定SPU的SKU列表
     */
    @GetMapping("/{spuId}/sku")
    public ResponseEntity<Map<String, Object>> getSkuListBySpuId(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 这里需要注入SkuInfoService
            List<com.hunric.model.SkuInfo> skuList = skuInfoService.getSkuListBySpuId(spuId);
            response.put("success", true);
            response.put("data", skuList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取SKU列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 为指定SPU创建SKU
     */
    @PostMapping("/{spuId}/sku")
    public ResponseEntity<Map<String, Object>> createSkuForSpu(@PathVariable Integer spuId, 
                                                               @RequestBody com.hunric.model.dto.SkuCreateDTO skuCreateDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            com.hunric.model.SkuInfo skuInfo = skuInfoService.createSku(spuId, skuCreateDTO);
            response.put("success", true);
            response.put("message", "创建SKU成功");
            response.put("data", skuInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建SKU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 