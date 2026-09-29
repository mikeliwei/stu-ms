package com.mikeliwei.aistumanage.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果，与 MyBatis-Plus 的 IPage 解耦后返回给前端。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 当前页数据 */
    private List<T> records;

    /** 总记录数 */
    private long total;

    /** 当前页码 */
    private long current;

    /** 每页条数 */
    private long size;

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        return new PageResult<>(records, total, current, size);
    }
}
