package com.ecommerce.product.command;

import com.ecommerce.domain.product.Product;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.service.ProductSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ProductSyncCommand implements CommandLineRunner {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSearchService productSearchService;

    @Override
    public void run(String... args) {
        // 从数据库获取所有商品
        List<Product> products = productMapper.findAll(0, Integer.MAX_VALUE);
        // 同步到ES
        productSearchService.syncProductsToEs(products);
        System.out.println("同步商品数据到ES完成，共同步" + products.size() + "条记录");
    }
} 