import request from '../../utils/request'

export function getRoleTree() {
  return request.get('/system/role/tree')
}

export function getRolePage(params) {
  return request.get('/system/role/page', { params })
}

export function getRole(id) {
  return request.get(`/system/role/${id}`)
}

export function createRole(data) {
  return request.post('/system/role', data)
}

export function updateRole(id, data) {
  return request.put(`/system/role/${id}`, data)
}

export function deleteRole(id) {
  return request.delete(`/system/role/${id}`)
}

export function updateRoleStatus(id, status) {
  return request.put(`/system/role/${id}/status`, status, {
    headers: { 'Content-Type': 'application/json' }
  })
}

export function getRolePermissions(id) {
  return request.get(`/system/role/${id}/permissions`)
}

export function assignRolePermissions(id, permissionIds) {
  return request.put(`/system/role/${id}/permissions`, permissionIds)
}