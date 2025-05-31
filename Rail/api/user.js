import request from '@/utils/request'

// 获取用户信息
export function getUserInfo() {
    return request.get('/user/info')
}

// 用户登录
export function login(data) {
    return request.post('/user/login', data)
}

// 更新用户信息
export function updateUserInfo(data) {
    return request.post('/user/update', data)
}

// 获取用户消息列表
export function getUserMessages(params) {
    return request.get('/user/messages', params)
} 