package com.mikeliwei.aistumanage.dto;

import lombok.Data;

/**
 * 学生分页查询条件。 ww
 */
@Data
public class StudentQuery {

    private long page = 1;

    private long size = 10;

    /** 学号，模糊匹配 */
    private String studentNo;

    /** 姓名，模糊匹配 */
    private String name;

    /** 所属班级 ID，精确匹配 */
    private Long classId;

    /** 性别，精确匹配 */
    private String gender;
}
