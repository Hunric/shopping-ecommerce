-- 修复SPU表的image_url字段长度问题
-- 将TEXT字段改为LONGTEXT以支持大的base64图片数据

-- 修改spu_info表的image_url字段
ALTER TABLE spu_info MODIFY COLUMN image_url LONGTEXT COMMENT '商品图片链接或Base64数据';

-- 同时修改sku_info表的exclusive_image_url字段，以防将来也需要存储base64数据
ALTER TABLE sku_info MODIFY COLUMN exclusive_image_url LONGTEXT COMMENT '专属图片链接（默认：所属SPU图片链接）';