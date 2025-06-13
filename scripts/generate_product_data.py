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

# FakeStoreAPI 端点
FAKE_STORE_API_URL = "https://fakestoreapi.com/products"

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

def get_fake_store_data():
    """从FakeStoreAPI获取商品数据"""
    try:
        response = requests.get(FAKE_STORE_API_URL)
        if response.status_code == 200:
            return response.json()
        else:
            print(f"获取数据失败，状态码: {response.status_code}")
            return None
    except Exception as e:
        print(f"请求API时出错: {e}")
        return None

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
    """处理API数据，将其转换为适合我们模板的格式"""
    processed_data = []
    
    if api_data:
        for item in api_data:
            # 只处理电子产品
            if 'electronics' in str(item.get('category', '')).lower():
                # 随机分配为手机或电脑类别
                category_id = random.choice([2, 3])
                category_name = "手机" if category_id == 2 else "电脑"
                unit = "台"
                
                # 提取品牌名（假设为标题的第一个词）
                brand = item.get("title", "").split()[0] if item.get("title") else ""
                brand = truncate_text(brand, MAX_BRAND_NAME_LENGTH)
                
                # 确保产品名称不超过长度限制
                product_name = truncate_text(item.get("title", ""), MAX_PRODUCT_NAME_LENGTH)
                
                # 生成几个SKU变种
                skus = []
                base_price = float(item.get("price", 0)) * 6.5  # 假设美元转人民币
                
                if category_id == 2:  # 手机
                    colors = ["黑色", "白色", "蓝色"]
                    storages = ["64GB", "128GB"]
                    
                    for color in colors[:2]:
                        for storage in storages:
                            sku_price = base_price
                            if storage == "128GB":
                                sku_price += 300
                            
                            # 确保SKU名称不超过长度限制，使用更简洁的命名
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
                    cpus = ["Intel i5", "AMD Ryzen 5"]
                    memories = ["8GB", "16GB"]
                    
                    for cpu in cpus:
                        for memory in memories:
                            sku_price = base_price
                            if memory == "16GB":
                                sku_price += 400
                            
                            # 确保SKU名称不超过长度限制，使用更简洁的命名
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
                
                product = {
                    "id": item.get("id", 0),
                    "name": product_name,
                    "description": truncate_text(item.get("description", ""), MAX_DESCRIPTION_LENGTH),
                    "category_id": category_id,
                    "price": base_price,
                    "brand": brand,
                    "selling_point": truncate_text(f"评分: {item.get('rating', {}).get('rate', 0)}/5，{item.get('rating', {}).get('count', 0)}人评价", MAX_SELLING_POINT_LENGTH),
                    "unit": unit,
                    "image": truncate_text(item.get("image", ""), MAX_IMAGE_URL_LENGTH),
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
    print("开始生成商品数据...")
    
    # 获取FakeStoreAPI数据
    api_data = get_fake_store_data()
    print(f"从FakeStoreAPI获取了 {len(api_data) if api_data else 0} 条数据")
    
    # 处理API数据
    processed_api_data = process_api_data(api_data)
    print(f"处理了 {len(processed_api_data)} 条API数据")
    
    # 生成额外的手机和电脑数据
    phone_data = generate_phone_data(5)
    computer_data = generate_computer_data(5)
    print(f"生成了 {len(phone_data)} 条手机数据和 {len(computer_data)} 条电脑数据")
    
    # 合并所有数据
    all_products = processed_api_data + phone_data + computer_data
    
    # 将产品数据转换为Excel数据格式
    excel_data = generate_excel_data(all_products)
    print(f"总共生成了 {len(excel_data)} 条SKU数据")
    
    # 创建Excel文件
    success = create_excel_from_template(excel_data, template_path, output_path)
    
    if success:
        print("数据生成并填充成功!")
    else:
        print("数据生成或填充过程中出现错误")

if __name__ == "__main__":
    main()