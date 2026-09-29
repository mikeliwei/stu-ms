package com.mikeliwei.aistumanage.vo;

import com.mikeliwei.aistumanage.entity.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生信息 + 班级名称，供列表展示使用。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentVO extends Student {

    private String className;
}
