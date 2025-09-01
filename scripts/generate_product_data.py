#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import requests
import json
import random
from datetime import datetime

try:
    import pandas as pd
    import openpyxl
    from openpyxl import load_workbook
except ImportError:
    print("正在安装所需库...")
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "pandas", "openpyxl", "requests"])
    import pandas as pd
    import openpyxl
    from openpyxl import load_workbook

# 确保脚本路径正确
script_dir = os.path.dirname(os.path.abspath(__file__))
project_dir = os.path.dirname(script_dir)
template_path = os.path.join(project_dir, 'product_template.xlsx')
output_path = os.path.join(project_dir, 'product_data.xlsx')

# DummyJSON API 端点
DUMMY_JSON_API_URL = "https://dummyjson.com/products"
DUMMY_JSON_CATEGORY_URL = "https://dummyjson.com/products/category"

# 手机和电脑相关的分类
PHONE_CATEGORIES = ["smartphones"]
LAPTOP_CATEGORIES = ["laptops"]

# 数据库字段长度限制（根据数据库表结构定义）
MAX_PRODUCT_NAME_LENGTH = 20   # spu_info.product_name VARCHAR(20)
MAX_SKU_NAME_LENGTH = 255      # sku_info.sku_name VARCHAR(255)
MAX_DESCRIPTION_LENGTH = 100   # spu_info.description VARCHAR(100)
MAX_SELLING_POINT_LENGTH = 200 # spu_info.selling_point TEXT (保守设置)
MAX_BRAND_NAME_LENGTH = 50     # spu_info.brand_name VARCHAR(50)
MAX_IMAGE_URL_LENGTH = 255     # 保守设置

def truncate_text(text, max_length):
    """截断文本到指定长度"""
    if text and len(text) > max_length:
        return text[:max_length-3] + "..."
    return text

def get_dummy_json_data_by_category(category):
    """从DummyJSON API根据分类获取商品数据"""
    try:
        url = f"{DUMMY_JSON_CATEGORY_URL}/{category}"
        response = requests.get(url)
        if response.status_code == 200:
            data = response.json()
            # DummyJSON返回的数据结构是 {"products": [...], "total": X, "skip": 0, "limit": 30}
            return data.get('products', [])
        else:
            print(f"获取{category}分类数据失败，状态码: {response.status_code}")
            return []
    except Exception as e:
        print(f"请求{category}分类API时出错: {e}")
        return []

def get_dummy_json_phones_and_laptops():
    """专门获取手机和电脑商品数据"""
    all_products = []
    
    # 获取手机数据
    print("正在获取手机商品数据...")
    for category in PHONE_CATEGORIES:
        products = get_dummy_json_data_by_category(category)
        print(f"从{category}分类获取了 {len(products)} 个商品")
        all_products.extend(products)
    
    # 获取电脑数据
    print("正在获取电脑商品数据...")
    for category in LAPTOP_CATEGORIES:
        products = get_dummy_json_data_by_category(category)
        print(f"从{category}分类获取了 {len(products)} 个商品")
        all_products.extend(products)
    
    return all_products

def generate_phone_data(count=10):
    """生成手机类商品数据"""
    phone_brands = ["Apple", "Samsung", "Xiaomi", "Huawei", "OPPO", "Vivo", "OnePlus", "Realme"]
    phone_models = ["iPhone 15", "Galaxy S23", "Mi 13", "P50", "Find X5", "X90", "10 Pro", "GT Neo5"]
    selling_points = [
        "超长续航，一天一充足够用",
        "高清摄像头，记录精彩瞬间",
        "强劲处理器，流畅运行各类应用",
        "高清大屏，视觉体验一流",
        "轻薄机身，携带方便",
        "快速充电，30分钟充电50%"
    ]
    
    phone_data = []
    for i in range(count):
        brand = random.choice(phone_brands)
        model = random.choice(phone_models)
        product_id = 1000 + i
        price = random.randint(2000, 8000) + random.randint(0, 99) / 100
        
        # 生成SKU数据 - 不同颜色和存储容量组合
        colors = ["黑色", "白色", "蓝色", "金色"]
        storages = ["128GB", "256GB", "512GB"]
        skus = []
        
        # 确保产品名称不超过长度限制，使用更简洁的命名
        product_name = truncate_text(f"{brand}{model}", MAX_PRODUCT_NAME_LENGTH)
        
        for color in colors[:2]:  # 取前两种颜色
            for storage in storages[:2]:  # 取前两种存储容量
                sku_price = price
                if storage == "256GB":
                    sku_price += 500
                elif storage == "512GB":
                    sku_price += 1000
                
                # 确保SKU名称不超过长度限制，使用更简洁的命名
                sku_name = truncate_text(f"{brand}{model}-{color}-{storage}", MAX_SKU_NAME_LENGTH)
                
                sku = {
                    "sku_name": sku_name,
                    "sku_price": sku_price,
                    "sku_stock": random.randint(10, 100),
                    "min_stock": random.randint(5, 20),
                    "attributes": json.dumps({
                        "颜色": color,
                        "存储容量": storage
                    }, ensure_ascii=False)
                }
                skus.append(sku)
        
        product = {
            "id": product_id,
            "name": product_name,
            "description": truncate_text(f"高性能{brand} {model}手机，搭载最新处理器和高清摄像头，提供极佳的用户体验。", MAX_DESCRIPTION_LENGTH),
            "category_id": 2,
            "price": price,
            "brand": truncate_text(brand, MAX_BRAND_NAME_LENGTH),
            "selling_point": truncate_text(random.choice(selling_points), MAX_SELLING_POINT_LENGTH),
            "unit": "台",
            "image": truncate_text(f"https://fakestoreapi.com/img/phone_{i+1}.jpg", MAX_IMAGE_URL_LENGTH),
            "skus": skus
        }
        phone_data.append(product)
    
    return phone_data

def generate_computer_data(count=10):
    """生成电脑类商品数据"""
    computer_brands = ["Lenovo", "Dell", "HP", "ASUS", "Acer", "Apple", "Microsoft", "MSI"]
    computer_types = ["笔记本", "台式机", "一体机", "游戏本"]
    selling_points = [
        "高性能处理器，办公游戏两不误",
        "超大内存，多任务处理更流畅",
        "固态硬盘，读写速度快",
        "高清显示屏，呈现绚丽色彩",
        "长续航，办公一整天",
        "散热系统优秀，长时间使用不卡顿"
    ]
    
    computer_data = []
    for i in range(count):
        brand = random.choice(computer_brands)
        comp_type = random.choice(computer_types)
        product_id = 2000 + i
        price = random.randint(3000, 15000) + random.randint(0, 99) / 100
        
        # 确保产品名称不超过长度限制，使用更简洁的命名
        product_name = truncate_text(f"{brand}{comp_type}", MAX_PRODUCT_NAME_LENGTH)
        
        # 生成SKU数据 - 不同配置组合
        cpus = ["Intel i5", "Intel i7", "Intel i9", "AMD Ryzen 5", "AMD Ryzen 7"]
        memories = ["8GB", "16GB", "32GB"]
        skus = []
        
        for cpu in cpus[:2]:  # 取前两种CPU
            for memory in memories[:2]:  # 取前两种内存
                sku_price = price
                if cpu in ["Intel i7", "AMD Ryzen 7"]:
                    sku_price += 800
                elif cpu == "Intel i9":
                    sku_price += 1600
                
                if memory == "16GB":
                    sku_price += 400
                elif memory == "32GB":
                    sku_price += 800
                
                # 确保SKU名称不超过长度限制，使用更简洁的命名
                sku_name = truncate_text(f"{brand}{comp_type}-{cpu}-{memory}", MAX_SKU_NAME_LENGTH)
                
                sku = {
                    "sku_name": sku_name,
                    "sku_price": sku_price,
                    "sku_stock": random.randint(5, 50),
                    "min_stock": random.randint(2, 10),
                    "attributes": json.dumps({
                        "处理器": cpu,
                        "内存": memory
                    }, ensure_ascii=False)
                }
                skus.append(sku)
        
        product = {
            "id": product_id,
            "name": product_name,
            "description": truncate_text(f"{brand}高性能{comp_type}，搭载Intel/AMD处理器和高性能显卡，适合办公和娱乐使用。", MAX_DESCRIPTION_LENGTH),
            "category_id": 3,
            "price": price,
            "brand": truncate_text(brand, MAX_BRAND_NAME_LENGTH),
            "selling_point": truncate_text(random.choice(selling_points), MAX_SELLING_POINT_LENGTH),
            "unit": "台",
            "image": truncate_text(f"https://fakestoreapi.com/img/computer_{i+1}.jpg", MAX_IMAGE_URL_LENGTH),
            "skus": skus
        }
        computer_data.append(product)
    
    return computer_data

def process_api_data(api_data):
    """处理DummyJSON API数据，将其转换为适合我们模板的格式"""
    processed_data = []
    
    if api_data:
        for item in api_data:
            # DummyJSON分类映射
            category = item.get('category', '').lower()
            
            # 精确映射分类
            if category in ['smartphones']:
                category_id = 2  # 手机
                unit = "台"
            elif category in ['laptops']:
                category_id = 3  # 电脑
                unit = "台"
            else:
                # 如果不是预期的分类，跳过
                continue
            
            # 提取品牌名
            brand = item.get("brand", "")
            if not brand and item.get("title"):
                # 如果没有品牌，从标题提取第一个词作为品牌
                brand = item.get("title", "").split()[0]
            brand = truncate_text(brand, MAX_BRAND_NAME_LENGTH)
            
            # 确保产品名称不超过长度限制
            product_name = truncate_text(item.get("title", ""), MAX_PRODUCT_NAME_LENGTH)
            
            # 生成几个SKU变种
            skus = []
            base_price = float(item.get("price", 0)) * 7.2  # 美元转人民币汇率
            
            if category_id == 2:  # 手机
                colors = ["黑色", "白色", "蓝色", "金色"]
                storages = ["128GB", "256GB", "512GB"]
                
                for color in colors[:2]:  # 取前两种颜色
                    for storage in storages[:2]:  # 取前两种存储
                        sku_price = base_price
                        if storage == "256GB":
                            sku_price += 500
                        elif storage == "512GB":
                            sku_price += 1000
                        
                        # 确保SKU名称不超过长度限制
                        sku_name = truncate_text(f"{product_name}-{color}-{storage}", MAX_SKU_NAME_LENGTH)
                        
                        sku = {
                            "sku_name": sku_name,
                            "sku_price": sku_price,
                            "sku_stock": random.randint(10, 100),
                            "min_stock": random.randint(5, 20),
                            "attributes": json.dumps({
                                "颜色": color,
                                "存储容量": storage
                            }, ensure_ascii=False)
                        }
                        skus.append(sku)
            else:  # 电脑
                cpus = ["Intel i5", "Intel i7", "AMD Ryzen 5", "AMD Ryzen 7"]
                memories = ["8GB", "16GB", "32GB"]
                
                for cpu in cpus[:2]:  # 取前两种CPU
                    for memory in memories[:2]:  # 取前两种内存
                        sku_price = base_price
                        if cpu in ["Intel i7", "AMD Ryzen 7"]:
                            sku_price += 800
                        if memory == "16GB":
                            sku_price += 400
                        elif memory == "32GB":
                            sku_price += 800
                        
                        # 确保SKU名称不超过长度限制
                        sku_name = truncate_text(f"{product_name}-{cpu}-{memory}", MAX_SKU_NAME_LENGTH)
                        
                        sku = {
                            "sku_name": sku_name,
                            "sku_price": sku_price,
                            "sku_stock": random.randint(5, 50),
                            "min_stock": random.randint(2, 10),
                            "attributes": json.dumps({
                                "处理器": cpu,
                                "内存": memory
                            }, ensure_ascii=False)
                        }
                        skus.append(sku)
            
            # 处理评分信息
            rating = item.get('rating', 0)
            selling_point = f"评分: {rating}/5"
            if item.get('stock'):
                selling_point += f"，库存充足"
            
            # 使用DummyJSON提供的高质量图片
            image_url = item.get("thumbnail", item.get("images", [""])[0] if item.get("images") else "")
            
            product = {
                "id": item.get("id", 0),
                "name": product_name,
                "description": truncate_text(item.get("description", ""), MAX_DESCRIPTION_LENGTH),
                "category_id": category_id,
                "price": base_price,
                "brand": brand,
                "selling_point": truncate_text(selling_point, MAX_SELLING_POINT_LENGTH),
                "unit": unit,
                "image": truncate_text(image_url, MAX_IMAGE_URL_LENGTH),
                "skus": skus
            }
            processed_data.append(product)
    
    return processed_data

def generate_excel_data(products):
    """将产品数据转换为Excel数据格式"""
    excel_data = []
    used_sku_names = set()  # 用于跟踪已使用的SKU名称
    
    for product in products:
        skus = product.get("skus", [])
        
        for sku in skus:
            # 确保SKU名称唯一性
            original_sku_name = sku.get("sku_name", "")
            sku_name = original_sku_name
            counter = 1
            
            while sku_name in used_sku_names:
                # 如果名称重复，添加数字后缀
                if len(original_sku_name) + len(str(counter)) + 1 <= MAX_SKU_NAME_LENGTH:
                    sku_name = f"{original_sku_name}-{counter}"
                else:
                    # 如果加上后缀会超长，则截断原名称
                    truncated_name = original_sku_name[:MAX_SKU_NAME_LENGTH - len(str(counter)) - 1]
                    sku_name = f"{truncated_name}-{counter}"
                counter += 1
            
            used_sku_names.add(sku_name)
            
            row = {
                "商品名称(必填)": product.get("name", ""),
                "商品描述": product.get("description", ""),
                "分类ID(必填)": product.get("category_id", ""),
                "展示价格(必填)": product.get("price", ""),
                "品牌名称": product.get("brand", ""),
                "卖点描述": product.get("selling_point", ""),
                "计量单位(必填)": product.get("unit", "台"),
                "商品图片链接": product.get("image", ""),
                "SKU名称(必填)": sku_name,  # 使用确保唯一性的SKU名称
                "SKU价格(必填)": sku.get("sku_price", ""),
                "SKU库存(必填)": sku.get("sku_stock", ""),
                "警戒库存": sku.get("min_stock", ""),
                "属性组合(JSON格式)": sku.get("attributes", "")
            }
            excel_data.append(row)
    
    return excel_data

def create_excel_from_template(data, template_path, output_path):
    """根据模板创建Excel文件"""
    try:
        # 从模板读取
        df_template = pd.read_excel(template_path)
        column_names = df_template.columns.tolist()
        
        # 创建数据DataFrame
        df = pd.DataFrame(data)
        
        # 确保列名一致
        for col in column_names:
            if col not in df.columns:
                df[col] = ""
        
        # 只保留模板中的列，按模板列顺序排序
        df = df[column_names]
        
        # 保存到新文件
        df.to_excel(output_path, index=False)
        print(f"数据已成功保存到: {output_path}")
        return True
    except Exception as e:
        print(f"创建Excel文件时出错: {e}")
        return False

def main():
    print("开始生成手机和电脑商品数据...")
    
    # 专门获取手机和电脑数据
    api_data = get_dummy_json_phones_and_laptops()
    print(f"从DummyJSON API获取了 {len(api_data) if api_data else 0} 条手机和电脑数据")
    
    # 处理API数据
    processed_api_data = process_api_data(api_data)
    print(f"处理了 {len(processed_api_data)} 条有效的手机和电脑数据")
    
    # 生成少量额外的手机和电脑数据作为补充
    phone_data = generate_phone_data(2)
    computer_data = generate_computer_data(2)
    print(f"额外生成了 {len(phone_data)} 条手机数据和 {len(computer_data)} 条电脑数据")
    
    # 合并所有数据
    all_products = processed_api_data + phone_data + computer_data
    
    # 统计分类数量
    phone_count = sum(1 for p in all_products if p.get('category_id') == 2)
    computer_count = sum(1 for p in all_products if p.get('category_id') == 3)
    print(f"总计: {phone_count} 个手机商品, {computer_count} 个电脑商品")
    
    # 将产品数据转换为Excel数据格式
    excel_data = generate_excel_data(all_products)
    print(f"总共生成了 {len(excel_data)} 条SKU数据")
    
    # 创建Excel文件
    success = create_excel_from_template(excel_data, template_path, output_path)
    
    if success:
        print("手机和电脑商品数据生成成功!")
        print(f"Excel文件已保存到: {output_path}")
        print("文件包含:")
        print(f"- {phone_count} 个手机商品 (分类ID: 2)")
        print(f"- {computer_count} 个电脑商品 (分类ID: 3)")
        print("- 所有商品都有高质量的图片和真实的商品信息")
        print("现在可以使用这个文件导入到数据库中")
    else:
        print("数据生成或填充过程中出现错误")

if __name__ == "__main__":
    main()