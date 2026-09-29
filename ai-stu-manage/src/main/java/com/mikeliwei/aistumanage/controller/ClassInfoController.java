package com.mikeliwei.aistumanage.controller;

import com.mikeliwei.aistumanage.common.PageResult;
import com.mikeliwei.aistumanage.common.Result;
import com.mikeliwei.aistumanage.dto.ClassQuery;
import com.mikeliwei.aistumanage.entity.ClassInfo;
import com.mikeliwei.aistumanage.service.ClassInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 班级信息管理模块接口。
 */
@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassInfoController {

    private final ClassInfoService classInfoService;

    @GetMapping
    public Result<PageResult<ClassInfo>> page(ClassQuery query) {
        return Result.ok(classInfoService.page(query));
    }

    /** 全量列表，供学生表单的班级下拉框使用 */
    @GetMapping("/all")
    public Result<List<ClassInfo>> all() {
        return Result.ok(classInfoService.listAll());
    }

    @GetMapping("/{id}")
    public Result<ClassInfo> detail(@PathVariable Long id) {
        return Result.ok(classInfoService.getExisting(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody ClassInfo entity) {
        classInfoService.create(entity);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ClassInfo entity) {
        classInfoService.update(id, entity);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        classInfoService.delete(id);
        return Result.ok();
    }
}
