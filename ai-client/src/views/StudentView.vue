<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listAllClasses } from '../api/classInfo'
import { createStudent, deleteStudent, pageStudents, updateStudent } from '../api/student'

const loading = ref(false)
const submitting = ref(false)
const classOptions = ref([])
const rows = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增学生')
const formRef = ref()

const query = reactive({
  page: 1,
  size: 10,
  studentNo: '',
  name: '',
  classId: null,
  gender: '',
})

const form = reactive({
  id: null,
  studentNo: '',
  name: '',
  gender: '男',
  age: null,
  birthDate: '',
  phone: '',
  email: '',
  classId: null,
  address: '',
})

const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const data = await pageStudents({ ...query })
    rows.value = data?.records ?? []
    total.value = data?.total ?? 0
  } catch {
    rows.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function loadClassOptions() {
  try {
    classOptions.value = (await listAllClasses()) ?? []
  } catch {
    classOptions.value = []
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

function handleReset() {
  Object.assign(query, { page: 1, size: 10, studentNo: '', name: '', classId: null, gender: '' })
  loadData()
}

function handleSizeChange() {
  query.page = 1
  loadData()
}

function resetForm() {
  Object.assign(form, {
    id: null,
    studentNo: '',
    name: '',
    gender: '男',
    age: null,
    birthDate: '',
    phone: '',
    email: '',
    classId: null,
    address: '',
  })
  formRef.value?.clearValidate()
}

function openCreate() {
  resetForm()
  dialogTitle.value = '新增学生'
  dialogVisible.value = true
}

function openEdit(row) {
  resetForm()
  Object.assign(form, {
    id: row.id,
    studentNo: row.studentNo,
    name: row.name,
    gender: row.gender || '男',
    age: row.age,
    birthDate: row.birthDate || '',
    phone: row.phone || '',
    email: row.email || '',
    classId: row.classId,
    address: row.address || '',
  })
  dialogTitle.value = '编辑学生'
  dialogVisible.value = true
}

/** 选择出生日期后自动推算年龄，仍可手动修改 */
function handleBirthDateChange(value) {
  if (!value) return
  const birth = new Date(value)
  if (Number.isNaN(birth.getTime())) return
  const now = new Date()
  let age = now.getFullYear() - birth.getFullYear()
  const monthDiff = now.getMonth() - birth.getMonth()
  if (monthDiff < 0 || (monthDiff === 0 && now.getDate() < birth.getDate())) {
    age -= 1
  }
  form.age = age < 0 ? null : age
}

async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  // 日期/可选字段的空值统一转成 null，保证后端能正确解析并清空字段
  const payload = {
    ...form,
    birthDate: form.birthDate || null,
    phone: form.phone || null,
    email: form.email || null,
    address: form.address || null,
    age: form.age === '' ? null : form.age,
  }
  submitting.value = true
  try {
    if (form.id) {
      await updateStudent(form.id, payload)
      ElMessage.success('修改成功')
    } else {
      await createStudent(payload)
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
    await ElMessageBox.confirm(`确定要删除学生「${row.name}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await deleteStudent(row.id)
    ElMessage.success('删除成功')
    // 删掉当前页最后一条时回退一页
    if (rows.value.length === 1 && query.page > 1) {
      query.page -= 1
    }
    loadData()
  } catch {
    // 错误提示由 axios 拦截器统一处理
  }
}

onMounted(() => {
  loadClassOptions()
  loadData()
})
</script>

<template>
  <div>
    <el-card class="page-card" shadow="never">
      <el-form :model="query" inline @submit.prevent>
        <el-form-item label="学号">
          <el-input v-model="query.studentNo" placeholder="模糊查询" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="query.name" placeholder="模糊查询" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="query.classId" placeholder="全部班级" clearable style="width: 180px">
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="query.gender" placeholder="全部" clearable style="width: 120px">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
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
        <span>共 {{ total }} 条学生记录</span>
        <el-button type="primary" @click="openCreate">
          <el-icon><Plus /></el-icon>
          <span>新增学生</span>
        </el-button>
      </div>

      <el-table v-loading="loading" :data="rows" border stripe style="width: 100%">
        <el-table-column prop="studentNo" label="学号" width="110" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70" align="center" />
        <el-table-column prop="age" label="年龄" width="70" align="center" />
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="address" label="家庭住址" min-width="200" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender">
                <el-radio value="男">男</el-radio>
                <el-radio value="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期">
              <el-date-picker
                v-model="form.birthDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                @change="handleBirthDateChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input-number v-model="form.age" :min="0" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级">
              <el-select v-model="form.classId" placeholder="请选择班级" clearable style="width: 100%">
                <el-option
                  v-for="item in classOptions"
                  :key="item.id"
                  :label="item.className"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="家庭住址">
              <el-input v-model="form.address" type="textarea" :rows="2" placeholder="请输入家庭住址" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
