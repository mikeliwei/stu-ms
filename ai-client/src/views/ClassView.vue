<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createClass, deleteClass, pageClasses, updateClass } from '../api/classInfo'

const loading = ref(false)
const submitting = ref(false)
const rows = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增班级')
const formRef = ref()

const query = reactive({
  page: 1,
  size: 10,
  className: '',
  grade: '',
})

const form = reactive({
  id: null,
  classCode: '',
  className: '',
  grade: '',
  headTeacher: '',
  remark: '',
})

const rules = {
  classCode: [{ required: true, message: '请输入班级编号', trigger: 'blur' }],
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const data = await pageClasses({ ...query })
    rows.value = data?.records ?? []
    total.value = data?.total ?? 0
  } catch {
    rows.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

function handleReset() {
  Object.assign(query, { page: 1, size: 10, className: '', grade: '' })
  loadData()
}

function handleSizeChange() {
  query.page = 1
  loadData()
}

function resetForm() {
  Object.assign(form, {
    id: null,
    classCode: '',
    className: '',
    grade: '',
    headTeacher: '',
    remark: '',
  })
  formRef.value?.clearValidate()
}

function openCreate() {
  resetForm()
  dialogTitle.value = '新增班级'
  dialogVisible.value = true
}

function openEdit(row) {
  resetForm()
  Object.assign(form, {
    id: row.id,
    classCode: row.classCode,
    className: row.className,
    grade: row.grade || '',
    headTeacher: row.headTeacher || '',
    remark: row.remark || '',
  })
  dialogTitle.value = '编辑班级'
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  // 可选字段空值转 null，保证后端能把字段清空
  const payload = {
    ...form,
    grade: form.grade || null,
    headTeacher: form.headTeacher || null,
    remark: form.remark || null,
  }
  submitting.value = true
  try {
    if (form.id) {
      await updateClass(form.id, payload)
      ElMessage.success('修改成功')
    } else {
      await createClass(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {
    // 错误提示由 axios 拦截器统一处理
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除班级「${row.className}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await deleteClass(row.id)
    ElMessage.success('删除成功')
    if (rows.value.length === 1 && query.page > 1) {
      query.page -= 1
    }
    loadData()
  } catch {
    // 班级下还有学生时后端会返回提示，由拦截器展示
  }
}

onMounted(loadData)
</script>

<template>
  <div>
    <el-card class="page-card" shadow="never">
      <el-form :model="query" inline @submit.prevent>
        <el-form-item label="班级名称">
          <el-input v-model="query.className" placeholder="模糊查询" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="年级">
          <el-input v-model="query.grade" placeholder="精确查询" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            <span>查询</span>
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            <span>重置</span>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div class="page-toolbar">
        <span>共 {{ total }} 个班级</span>
        <el-button type="primary" @click="openCreate">
          <el-icon><Plus /></el-icon>
          <span>新增班级</span>
        </el-button>
      </div>

      <el-table v-loading="loading" :data="rows" border stripe style="width: 100%">
        <el-table-column prop="classCode" label="班级编号" width="120" />
        <el-table-column prop="className" label="班级名称" width="140" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="headTeacher" label="班主任" width="110" />
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ (row.createTime || '').replace('T', ' ') }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="page-pagination">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadData"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="班级编号" prop="classCode">
          <el-input v-model="form.classCode" placeholder="例如 C2024001" />
        </el-form-item>
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="例如 高一(1)班" />
        </el-form-item>
        <el-form-item label="年级">
          <el-input v-model="form.grade" placeholder="例如 2024级" />
        </el-form-item>
        <el-form-item label="班主任">
          <el-input v-model="form.headTeacher" placeholder="请输入班主任姓名" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
