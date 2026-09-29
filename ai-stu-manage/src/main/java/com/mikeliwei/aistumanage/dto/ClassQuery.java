package com.mikeliwei.aistumanage.dto;

import lombok.Data;

/**
 * 班级分页查询条件。
 */
@Data
public class ClassQuery {

    private long page = 1;

    private long size = 10;

    /** 班级名称，模糊匹配 */
    private String className;

    /** 所属年级，精确匹配 */
    private String grade;
}
