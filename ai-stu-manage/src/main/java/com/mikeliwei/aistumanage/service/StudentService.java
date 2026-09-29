package com.mikeliwei.aistumanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mikeliwei.aistumanage.common.BusinessException;
import com.mikeliwei.aistumanage.common.PageResult;
import com.mikeliwei.aistumanage.dto.StudentQuery;
import com.mikeliwei.aistumanage.entity.ClassInfo;
import com.mikeliwei.aistumanage.entity.Student;
import com.mikeliwei.aistumanage.mapper.ClassInfoMapper;
import com.mikeliwei.aistumanage.mapper.StudentMapper;
import com.mikeliwei.aistumanage.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 学生信息管理业务。
 */
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentMapper studentMapper;

    private final ClassInfoMapper classInfoMapper;

    public PageResult<StudentVO> page(StudentQuery query) {
        Page<Student> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<Student>()
                .like(StringUtils.hasText(query.getStudentNo()), Student::getStudentNo, query.getStudentNo())
                .like(StringUtils.hasText(query.getName()), Student::getName, query.getName())
                .eq(query.getClassId() != null, Student::getClassId, query.getClassId())
                .eq(StringUtils.hasText(query.getGender()), Student::getGender, query.getGender())
                .orderByAsc(Student::getStudentNo);
        Page<Student> result = studentMapper.selectPage(page, wrapper);
        return PageResult.of(toVoList(result.getRecords()), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public StudentVO getVoById(Long id) {
        Student student = getExisting(id);
        List<StudentVO> list = toVoList(Collections.singletonList(student));
        return list.get(0);
    }

    public void create(Student entity) {
        validate(entity, null);
        entity.setId(null);
        studentMapper.insert(entity);
    }

    public void update(Long id, Student entity) {
        getExisting(id);
        validate(entity, id);
        entity.setId(id);
        studentMapper.updateById(entity);
    }

    public void delete(Long id) {
        getExisting(id);
        studentMapper.deleteById(id);
    }

    private Student getExisting(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException(404, "学生不存在");
        }
        return student;
    }

    private void validate(Student entity, Long excludeId) {
        if (entity.getClassId() != null && classInfoMapper.selectById(entity.getClassId()) == null) {
            throw new BusinessException("所选班级不存在");
        }
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<Student>()
                .eq(Student::getStudentNo, entity.getStudentNo());
        if (excludeId != null) {
            wrapper.ne(Student::getId, excludeId);
        }
        Long count = studentMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException("学号已存在：" + entity.getStudentNo());
        }
    }

    /**
     * 批量回填班级名称。
     * 先收集涉及到的 classId 一次性查出班级，避免手写 JOIN SQL 和 N+1 查询。
     */
    private List<StudentVO> toVoList(List<Student> records) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> classIds = records.stream()
                .map(Student::getClassId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> classNameMap = classIds.isEmpty()
                ? Collections.emptyMap()
                : classInfoMapper.selectByIds(classIds).stream()
                        .collect(Collectors.toMap(ClassInfo::getId, ClassInfo::getClassName, (a, b) -> a));

        List<StudentVO> list = new ArrayList<>(records.size());
        for (Student student : records) {
            StudentVO vo = new StudentVO();
            BeanUtils.copyProperties(student, vo);
            if (student.getClassId() != null) {
                vo.setClassName(classNameMap.get(student.getClassId()));
            }
            list.add(vo);
        }
        return list;
    }
}
