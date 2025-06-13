# 商品数据生成工具

这个工具用于从FakeStoreAPI获取商品数据并填充到Excel模板中。工具会生成手机（分类ID=2）和电脑（分类ID=3）两种类型的商品数据。

## 功能特点

- 从FakeStoreAPI获取真实商品数据
- 生成模拟的手机和电脑商品数据
- 根据分类ID（2=手机，3=电脑）分类商品
- 将数据填充到Excel模板中

## 使用方法

1. 确保已安装Python 3.x环境
2. 双击运行`generate_product_data.bat`批处理文件
3. 脚本会自动安装所需依赖（requests、pandas、openpyxl）
4. 生成的数据文件将保存为项目根目录下的`product_data.xlsx`

## 数据来源

- FakeStoreAPI: https://fakestoreapi.com/products（部分电子产品数据）
- 本地生成的模拟手机和电脑数据

## 数据格式

生成的Excel文件包含以下字段：

- productId: 商品ID
- productName: 商品名称
- description: 商品描述
- price: 价格
- originalPrice: 原价
- stock: 库存
- categoryId: 分类ID（2=手机，3=电脑）
- category: 分类名称
- mainImage: 主图URL
- brand: 品牌
- rating: 评分
- ratingCount: 评分数量
- status: 商品状态（ON_SHELF等）

## 注意事项

- 脚本需要网络连接才能从FakeStoreAPI获取数据
- 如果API无法访问，脚本仍会生成本地模拟数据
- 请确保`product_template.xlsx`模板文件存在于项目根目录下 