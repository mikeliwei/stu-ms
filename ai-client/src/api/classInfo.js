import request from '../utils/request'

export function pageClasses(params) {
  return request.get('/classes', { params })
}

/** 全量班级列表，用于学生表单的班级下拉框 */
export function listAllClasses() {
  return request.get('/classes/all')
}

export function createClass(data) {
  return request.post('/classes', data)
}

export function updateClass(id, data) {
  return request.put(`/classes/${id}`, data)
}

export function deleteClass(id) {
  return request.delete(`/classes/${id}`)
}
