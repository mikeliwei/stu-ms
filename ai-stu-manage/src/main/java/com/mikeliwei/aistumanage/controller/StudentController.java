package com.mikeliwei.aistumanage.controller;

import com.mikeliwei.aistumanage.common.PageResult;
import com.mikeliwei.aistumanage.common.Result;
import com.mikeliwei.aistumanage.dto.StudentQuery;
import com.mikeliwei.aistumanage.entity.Student;
import com.mikeliwei.aistumanage.service.StudentService;
import com.mikeliwei.aistumanage.vo.StudentVO;
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

/**
 * 学生信息管理模块接口。
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public Result<PageResult<StudentVO>> page(StudentQuery query) {
        return Result.ok(studentService.page(query));
    }

    @GetMapping("/{id}")
    public Result<StudentVO> detail(@PathVariable Long id) {
        return Result.ok(studentService.getVoById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody Student entity) {
        studentService.create(entity);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Student entity) {
        studentService.update(id, entity);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.ok();
    }
}
