package com.mikeliwei.aistumanage.service;

import com.mikeliwei.aistumanage.common.BusinessException;
import com.mikeliwei.aistumanage.entity.ClassInfo;
import com.mikeliwei.aistumanage.mapper.ClassInfoMapper;
import com.mikeliwei.aistumanage.mapper.StudentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ClassInfoService 单元测试，重点覆盖班级编号唯一性与「有学生不允许删除」规则。
 */
@ExtendWith(MockitoExtension.class)
class ClassInfoServiceTest {

    @Mock
    private ClassInfoMapper classInfoMapper;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private ClassInfoService classInfoService;

    private static ClassInfo classInfo(Long id, String classCode, String className) {
        ClassInfo classInfo = new ClassInfo();
        classInfo.setId(id);
        classInfo.setClassCode(classCode);
        classInfo.setClassName(className);
        return classInfo;
    }

    @Test
    @DisplayName("班级编号重复时不允许新增")
    void createShouldRejectDuplicateCode() {
        when(classInfoMapper.selectCount(any())).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> classInfoService.create(classInfo(null, "C2024001", "高一(1)班")));

        assertTrue(ex.getMessage().contains("班级编号已存在"));
        verify(classInfoMapper, never()).insert(any(ClassInfo.class));
    }

    @Test
    @DisplayName("合法数据可以新增")
    void createShouldInsertWhenValid() {
        when(classInfoMapper.selectCount(any())).thenReturn(0L);
        ClassInfo entity = classInfo(null, "C2024001", "高一(1)班");

        classInfoService.create(entity);

        verify(classInfoMapper).insert(entity);
    }

    @Test
    @DisplayName("班级下还有学生时不允许删除")
    void deleteShouldRejectWhenClassHasStudents() {
        when(classInfoMapper.selectById(1L)).thenReturn(classInfo(1L, "C2024001", "高一(1)班"));
        when(studentMapper.selectCount(any())).thenReturn(3L);

        BusinessException ex = assertThrows(BusinessException.class, () -> classInfoService.delete(1L));

        assertTrue(ex.getMessage().contains("还有 3 名学生"));
        verify(classInfoMapper, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("班级下没有学生时可以删除")
    void deleteShouldRemoveWhenNoStudents() {
        when(classInfoMapper.selectById(1L)).thenReturn(classInfo(1L, "C2024001", "高一(1)班"));
        when(studentMapper.selectCount(any())).thenReturn(0L);

        classInfoService.delete(1L);

        verify(classInfoMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除不存在的班级返回 404")
    void deleteShouldThrowWhenMissing() {
        when(classInfoMapper.selectById(9L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> classInfoService.delete(9L));

        assertEquals(404, ex.getCode());
        verify(studentMapper, never()).selectCount(any());
    }

    @Test
    @DisplayName("修改不存在的班级返回 404")
    void updateShouldThrowWhenMissing() {
        when(classInfoMapper.selectById(9L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> classInfoService.update(9L, classInfo(null, "C2024001", "高一(1)班")));

        assertEquals(404, ex.getCode());
        verify(classInfoMapper, never()).updateById(any(ClassInfo.class));
    }

    @Test
    @DisplayName("编号被其它班级占用时不允许修改")
    void updateShouldRejectDuplicateCodeOfOtherClass() {
        when(classInfoMapper.selectById(1L)).thenReturn(classInfo(1L, "C2024001", "高一(1)班"));
        when(classInfoMapper.selectCount(any())).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> classInfoService.update(1L, classInfo(null, "C2024002", "高一(1)班")));

        assertTrue(ex.getMessage().contains("班级编号已存在"));
        verify(classInfoMapper, never()).updateById(any(ClassInfo.class));
    }
}
