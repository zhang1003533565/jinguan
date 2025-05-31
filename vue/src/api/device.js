import request from '@/utils/request'

// 设备管理API
export default {
    // 创建设备
    create(data) {
        return request.post('/admin/device', data)
    },

    // 更新设备
    update(id, data) {
        return request.put(`/admin/device/${id}`, data)
    },

    // 删除设备
    delete(id) {
        return request.delete(`/admin/device/${id}`)
    },

    // 获取设备详情
    getById(id) {
        return request.get(`/admin/device/${id}`)
    },

    // 获取设备列表
    getList(params) {
        return request.get('/admin/device/list', { params })
    },

    // 根据状态获取设备列表
    getListByStatus(params) {
        return request.get('/admin/device/status', { params })
    },

    // 获取在线设备列表
    getOnlineDevices() {
        return request.get('/admin/device/online')
    },

    // 获取离线设备列表
    getOfflineDevices() {
        return request.get('/admin/device/offline')
    },

    // 更新设备状态
    updateStatus(id, status) {
        return request.put(`/admin/device/${id}/status`, null, { params: { status } })
    },

    // 更新设备在线状态
    updateOnlineStatus(id, isOnline) {
        return request.put(`/admin/device/${id}/online-status`, null, { params: { isOnline } })
    },

    // 获取设备最新数据
    getLatestData(id) {
        return request.get(`/admin/device/${id}/data/latest`)
    },

    // 获取设备历史数据
    getHistoryData(id, params) {
        return request.get(`/admin/device/${id}/data/history`, { params })
    },

    // 获取设备告警记录
    getWarnings(id, params) {
        return request.get(`/admin/device/${id}/warnings`, { params })
    }
} 