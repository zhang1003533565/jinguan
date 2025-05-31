import request from '@/utils/request'

// 预警管理API
export default {
    // 创建预警
    create(data) {
        return request.post('/admin/warning', data)
    },

    // 更新预警
    update(id, data) {
        return request.put(`/admin/warning/${id}`, data)
    },

    // 删除预警
    delete(id) {
        return request.delete(`/admin/warning/${id}`)
    },

    // 获取预警详情
    getById(id) {
        return request.get(`/admin/warning/${id}`)
    },

    // 获取预警列表
    getList(params) {
        return request.get('/admin/warning/list', { params })
    },

    // 根据状态获取预警列表
    getListByStatus(params) {
        return request.get('/admin/warning/status', { params })
    },

    // 获取未处理预警列表
    getUnhandledWarnings() {
        return request.get('/admin/warning/unhandled')
    },

    // 获取已处理预警列表
    getHandledWarnings() {
        return request.get('/admin/warning/handled')
    },

    // 更新预警状态
    updateStatus(id, status) {
        return request.put(`/admin/warning/${id}/status`, null, { params: { status } })
    },

    // 处理预警
    handle(id, data) {
        return request.put(`/admin/warning/${id}/handle`, data)
    },

    // 获取预警统计数据
    getStatistics(params) {
        return request.get('/admin/warning/statistics', { params })
    },

    // 根据类型和级别获取预警列表
    getListByTypeAndLevel(params) {
        return request.get('/warning/type-level', { params })
    },

    // 获取预警趋势
    getWarningTrend(params) {
        return request.get('/warning/trend', { params })
    },

    // 导出预警记录
    exportWarnings(params) {
        return request.get('/warning/export', {
            params,
            responseType: 'blob'
        })
    },

    // 发送预警通知
    sendWarningNotification(id) {
        return request.post(`/warning/${id}/notify`)
    }
} 