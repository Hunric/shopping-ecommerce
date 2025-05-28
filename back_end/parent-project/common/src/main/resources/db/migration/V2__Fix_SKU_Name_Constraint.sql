-- 修复SKU名称唯一性约束
-- 从全局唯一改为在同一SPU内唯一

-- 删除原有的全局唯一约束
ALTER TABLE sku_info DROP INDEX uk_sku_name;

-- 添加新的复合唯一约束（SPU内唯一）
ALTER TABLE sku_info ADD CONSTRAINT uk_sku_name_spu UNIQUE (product_id, sku_name); 