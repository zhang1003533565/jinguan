import request from '@/utils/request'

// 获取用户列表
export function getUserList(params) {
    return request({
        url: '/user/list',
        method: 'get',
        params
    })
}

// 获取用户详情
export function getUserDetail(id) {
    return request({
        url: `/user/${id}`,
        method: 'get'
    })
}

// 创建用户
export function createUser(data) {
    return request({
        url: '/user/create',
        method: 'post',
        data
    })
}

// 更新用户
export function updateUser(id, data) {
    return request({
        url: `/user/${id}`,
        method: 'put',
        data
    })
}

// 删除用户
export function deleteUser(id) {
    return request({
        url: `/user/${id}`,
        method: 'delete'
    })
} 