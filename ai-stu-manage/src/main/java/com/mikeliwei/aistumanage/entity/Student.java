package com.mikeliwei.aistumanage.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生信息。
 * 可空字段使用 updateStrategy = ALWAYS，保证前端清空内容时能把数据库字段更新为 NULL。
 */
@Data
@TableName("student")
public class Student {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "学号不能为空")
    private String studentNo;

    @NotBlank(message = "姓名不能为空")
    private String name;

    /** 男 / 女 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String gender;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer age;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private LocalDate birthDate;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String phone;

    @Email(message = "邮箱格式不正确")
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String email;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long classId;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String address;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
