import request from '@/utils/request'

// 查询微信用户列表
export function listUser(query) {
  return request({
    url: '/wx/user/list',
    method: 'get',
    params: query
  })
}

// 查询微信用户详细
export function getUser(uid) {
  return request({
    url: '/wx/user/' + uid,
    method: 'get'
  })
}

// 新增微信用户
export function addUser(data) {
  return request({
    url: '/wx/user',
    method: 'post',
    data: data
  })
}

// 修改微信用户
export function updateUser(data) {
  return request({
    url: '/wx/user',
    method: 'put',
    data: data
  })
}

// 删除微信用户
export function delUser(uid) {
  return request({
    url: '/wx/user/' + uid,
    method: 'delete'
  })
}

//更改用户状态
export function changeWxUserDisabled(data) {
  return request({
    url: '/wx/user/changeStatus',
    method: 'post',
    data: data
  })
}
