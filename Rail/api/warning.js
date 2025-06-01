import request from '@/utils/request'

// 提交告警
export function submitWarning(data) {
    return request.post('/warning', data)
}

// 获取告警列表
export function getWarningList(params) {
    return request.get('/warning/list', params)
}

// 获取告警详情
export function getWarningDetail(id) {
    return request.get(`/warning/${id}`)
}

// 获取告警统计数据
export function getWarningStats() {
    return request.get('/warning/stats')
}

// 获取隧道列表
export function getTunnelList() {
    return request.get('/tunnel/list')
}

// 标记告警为已处理
export function markWarningProcessed(id, data) {
    return request.post(`/warning/process/${id}`, data)
}

// 获取所有隧道
export function getAllTunnels() {
    return request.get('/warning/tunnels')
}

// 使用示例：
/*
// 提交告警
submitWarning({
    type: '环境',  // 环境/设备/安全
    level: '严重', // 严重/中等/轻微
    description: '隧道内CO2浓度超标',
    tunnelId: 1
})

// 获取告警列表
getWarningList({
    page: 1,
    size: 10,
    type: '环境',    // 可选
    level: '严重',   // 可选
    status: '未处理' // 可选
})

// 获取告警详情
getWarningDetail(1)

// 获取告警统计
getWarningStats()

// 标记告警已处理
markWarningProcessed(1, {
    handleMethod: '已调整通风系统',
    handler: '张工'
})
*/ 