import request from '../../utils/request'

export function getPermissionTree() {
  return request.get('/system/permission/tree')
}

export function getPermission(id) {
  return request.get(`/system/permission/${id}`)
}

export function createPermission(data) {
  return request.post('/system/permission', data)
}

export function updatePermission(id, data) {
  return request.put(`/system/permission/${id}`, data)
}

export function deletePermission(id) {
  return request.delete(`/system/permission/${id}`)
}