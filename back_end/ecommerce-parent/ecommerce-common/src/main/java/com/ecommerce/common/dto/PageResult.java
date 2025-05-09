package com.ecommerce.common.dto; // Assuming a dto package in common module

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 通用分页结果数据传输对象 (DTO)
 * @param <T> 列表数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 当前页数据列表
     */
    private List<T> list;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private int page;

    /**
     * 每页数量
     */
    private int pageSize;

    /**
     * 总页数 (计算得出)
     */
    private int totalPages;

    // Constructor to automatically calculate totalPages
    public PageResult(List<T> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (pageSize > 0) ? (int) Math.ceil((double) total / pageSize) : 0;
    }

    // Static factory method (optional)
    public static <T> PageResult<T> create(List<T> list, long total, int page, int pageSize) {
        return new PageResult<>(list, total, page, pageSize);
    }
}