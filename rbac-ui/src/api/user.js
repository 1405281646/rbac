import request from '../../utils/request'

export function getUserPage(params) {
  return request.get('/system/user/page', { params })
}

export function getUser(id) {
  return request.get(`/system/user/${id}`)
}

export function createUser(data) {
  return request.post('/system/user', data)
}

export function updateUser(id, data) {
  return request.put(`/system/user/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/system/user/${id}`)
}

export function updateUserStatus(id, status) {
  return request.put(`/system/user/${id}/status`, status, {
    headers: { 'Content-Type': 'application/json' }
  })
}

export function getUserRoles(id) {
  return request.get(`/system/user/${id}/roles`)
}

export function assignUserRoles(id, roleIds) {
  return request.put(`/system/user/${id}/roles`, roleIds)
}