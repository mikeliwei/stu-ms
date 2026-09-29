package com.mikeliwei.aistumanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mikeliwei.aistumanage.common.BusinessException;
import com.mikeliwei.aistumanage.common.PageResult;
import com.mikeliwei.aistumanage.dto.StudentQuery;
import com.mikeliwei.aistumanage.entity.ClassInfo;
import com.mikeliwei.aistumanage.entity.Student;
import com.mikeliwei.aistumanage.mapper.ClassInfoMapper;
import com.mikeliwei.aistumanage.mapper.StudentMapper;
import com.mikeliwei.aistumanage.vo.StudentVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * StudentService 单元测试，Mapper 全部 mock，不需要数据库。
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private ClassInfoMapper classInfoMapper;

    @InjectMocks
    private StudentService studentService;

    private static Student student(Long id, String studentNo, String name, Long classId) {
        Student student = new Student();
        student.setId(id);
        student.setStudentNo(studentNo);
        student.setName(name);
        student.setGender("男");
        student.setClassId(classId);
        return student;
    }

    private static ClassInfo classInfo(Long id, String className) {
        ClassInfo classInfo = new ClassInfo();
        classInfo.setId(id);
        classInfo.setClassName(className);
        return classInfo;
    }

    private static Page<Student> pageOf(List<Student> records) {
        Page<Student> page = new Page<>(1, 10);
        page.setRecords(records);
        page.setTotal(records.size());
        return page;
    }

    @Test
    @DisplayName("分页查询会回填班级名称")
    void pageShouldFillClassName() {
        doReturn(pageOf(List.of(student(1L, "20240101", "李明", 1L))))
                .when(studentMapper).selectPage(any(Page.class), any());
        when(classInfoMapper.selectByIds(anyCollection())).thenReturn(List.of(classInfo(1L, "高一(1)班")));

        PageResult<StudentVO> result = studentService.page(new StudentQuery());

        assertEquals(1, result.getRecords().size());
        assertEquals(1, result.getTotal());
        StudentVO vo = result.getRecords().get(0);
        assertEquals("高一(1)班", vo.getClassName());
        assertEquals("20240101", vo.getStudentNo());
        assertEquals("李明", vo.getName());
    }

    @Test
    @DisplayName("学生未分班时班级名称为空，且不查班级表")
    void pageShouldHandleNullClassId() {
        doReturn(pageOf(List.of(student(1L, "20240101", "李明", null))))
                .when(studentMapper).selectPage(any(Page.class), any());

        PageResult<StudentVO> result = studentService.page(new StudentQuery());

        assertNull(result.getRecords().get(0).getClassName());
        verifyNoInteractions(classInfoMapper);
    }

    @Test
    @DisplayName("没有数据时返回空列表")
    void pageShouldReturnEmptyWhenNoRecords() {
        doReturn(pageOf(List.of())).when(studentMapper).selectPage(any(Page.class), any());

        PageResult<StudentVO> result = studentService.page(new StudentQuery());

        assertTrue(result.getRecords().isEmpty());
        assertEquals(0, result.getTotal());
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
    }

    @Test
    @DisplayName("学号重复时不允许新增")
    void createShouldRejectDuplicateStudentNo() {
        Student entity = student(null, "20240101", "李明", null);
        when(studentMapper.selectCount(any())).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> studentService.create(entity));

        assertTrue(ex.getMessage().contains("学号已存在"));
        verify(studentMapper, never()).insert(any(Student.class));
    }

    @Test
    @DisplayName("所属班级不存在时不允许新增")
    void createShouldRejectUnknownClass() {
        Student entity = student(null, "20240101", "李明", 99L);
        when(classInfoMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> studentService.create(entity));

        assertTrue(ex.getMessage().contains("班级不存在"));
        verify(studentMapper, never()).insert(any(Student.class));
    }

    @Test
    @DisplayName("合法数据可以新增")
    void createShouldInsertWhenValid() {
        Student entity = student(null, "20240101", "李明", 1L);
        when(classInfoMapper.selectById(1L)).thenReturn(classInfo(1L, "高一(1)班"));
        when(studentMapper.selectCount(any())).thenReturn(0L);

        studentService.create(entity);

        verify(studentMapper).insert(entity);
    }

    @Test
    @DisplayName("修改不存在的学生返回 404")
    void updateShouldThrowWhenMissing() {
        when(studentMapper.selectById(5L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> studentService.update(5L, student(null, "20240101", "李明", null)));

        assertEquals(404, ex.getCode());
        verify(studentMapper, never()).updateById(any(Student.class));
    }

    @Test
    @DisplayName("查询不存在的学生返回 404")
    void getVoByIdShouldThrowWhenMissing() {
        when(studentMapper.selectById(5L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> studentService.getVoById(5L));

        assertEquals(404, ex.getCode());
    }

    @Test
    @DisplayName("删除不存在的学生返回 404")
    void deleteShouldThrowWhenMissing() {
        when(studentMapper.selectById(5L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> studentService.delete(5L));

        assertEquals(404, ex.getCode());
        verify(studentMapper, never()).deleteById(any(Long.class));
    }
}
