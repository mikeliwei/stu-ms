package com.mikeliwei.aistumanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mikeliwei.aistumanage.common.BusinessException;
import com.mikeliwei.aistumanage.common.PageResult;
import com.mikeliwei.aistumanage.dto.ClassQuery;
import com.mikeliwei.aistumanage.entity.ClassInfo;
import com.mikeliwei.aistumanage.entity.Student;
import com.mikeliwei.aistumanage.mapper.ClassInfoMapper;
import com.mikeliwei.aistumanage.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 班级信息管理业务。
 */
@Service
@RequiredArgsConstructor
public class ClassInfoService {

    private final ClassInfoMapper classInfoMapper;

    private final StudentMapper studentMapper;

    public PageResult<ClassInfo> page(ClassQuery query) {
        Page<ClassInfo> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<ClassInfo> wrapper = new LambdaQueryWrapper<ClassInfo>()
                .like(StringUtils.hasText(query.getClassName()), ClassInfo::getClassName, query.getClassName())
                .eq(StringUtils.hasText(query.getGrade()), ClassInfo::getGrade, query.getGrade())
                .orderByAsc(ClassInfo::getClassCode);
        return PageResult.of(classInfoMapper.selectPage(page, wrapper));
    }

    /** 全量班级列表，给学生表单的下拉框使用。 */
    public List<ClassInfo> listAll() {
        return classInfoMapper.selectList(new LambdaQueryWrapper<ClassInfo>()
                .orderByAsc(ClassInfo::getClassCode));
    }

    public ClassInfo getExisting(Long id) {
        ClassInfo entity = classInfoMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "班级不存在");
        }
        return entity;
    }

    public void create(ClassInfo entity) {
        if (isCodeTaken(entity.getClassCode(), null)) {
            throw new BusinessException("班级编号已存在：" + entity.getClassCode());
        }
        entity.setId(null);
        classInfoMapper.insert(entity);
    }

    public void update(Long id, ClassInfo entity) {
        getExisting(id);
        if (isCodeTaken(entity.getClassCode(), id)) {
            throw new BusinessException("班级编号已存在：" + entity.getClassCode());
        }
        entity.setId(id);
        classInfoMapper.updateById(entity);
    }

    public void delete(Long id) {
        getExisting(id);
        Long studentCount = studentMapper.selectCount(new LambdaQueryWrapper<Student>()
                .eq(Student::getClassId, id));
        if (studentCount != null && studentCount > 0) {
            throw new BusinessException("该班级下还有 " + studentCount + " 名学生，无法删除");
        }
        classInfoMapper.deleteById(id);
    }

    private boolean isCodeTaken(String classCode, Long excludeId) {
        LambdaQueryWrapper<ClassInfo> wrapper = new LambdaQueryWrapper<ClassInfo>()
                .eq(ClassInfo::getClassCode, classCode);
        if (excludeId != null) {
            wrapper.ne(ClassInfo::getId, excludeId);
        }
        Long count = classInfoMapper.selectCount(wrapper);
        return count != null && count > 0;
    }
}
