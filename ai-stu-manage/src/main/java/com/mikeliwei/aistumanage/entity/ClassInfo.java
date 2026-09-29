package com.mikeliwei.aistumanage.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级信息。
 * 可空字段使用 updateStrategy = ALWAYS，保证前端清空内容时能把数据库字段更新为 NULL。
 */
@Data
@TableName("class_info")
public class ClassInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "班级编号不能为空")
    private String classCode;

    @NotBlank(message = "班级名称不能为空")
    private String className;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String grade;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String headTeacher;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
