import request from '@/utils/request'

// 隧道管理API
export default {
    // 获取所有隧道列表
    getTunnelList() {
        return request({
            url: '/admin/tunnel/list',
            method: 'get'
        })
    },

    // 获取单个隧道详情
    getTunnelById(id) {
        return request({
            url: `/admin/tunnel/${id}`,
            method: 'get'
        })
    },

    // 按状态获取隧道
    getTunnelsByStatus(status) {
        return request({
            url: `/admin/tunnel/status/${status}`,
            method: 'get'
        })
    },

    // 创建新隧道
    createTunnel(data) {
        return request({
            url: '/admin/tunnel',
            method: 'post',
            data
        })
    },

    // 更新隧道信息
    updateTunnel(id, data) {
        return request({
            url: `/admin/tunnel/${id}`,
            method: 'put',
            data
        })
    },

    // 更新隧道状态
    updateTunnelStatus(id, status) {
        return request({
            url: `/admin/tunnel/${id}/status`,
            method: 'patch',
            data: { status }
        })
    },

    // 删除隧道
    deleteTunnel(id) {
        return request({
            url: `/admin/tunnel/${id}`,
            method: 'delete'
        })
    },

    // 搜索隧道
    searchTunnels(keyword) {
        return request({
            url: '/admin/tunnel/search',
            method: 'get',
            params: { keyword }
        })
    },

    // 获取隧道的设备列表
    getDevices(id) {
        return request.get(`/admin/tunnel/${id}/devices`)
    },

    // 获取隧道的摄像头列表
    getCameras(id) {
        return request.get(`/admin/tunnel/${id}/cameras`)
    },

    // 获取最新环境数据
    getLatestEnvironment(id) {
        return request.get(`/admin/tunnel/${id}/environment/latest`)
    },

    // 获取历史环境数据
    getEnvironmentHistory(id, params) {
        return request.get(`/admin/tunnel/${id}/environment/history`, { params })
    },

    // 获取预警记录
    getWarnings(id, params) {
        return request.get(`/admin/tunnel/${id}/warnings`, { params })
    },

    // 添加设备到隧道
    addDevice(tunnelId, deviceId) {
        return request.post(`/admin/tunnel/${tunnelId}/device/${deviceId}`)
    },

    // 从隧道移除设备
    removeDevice(tunnelId, deviceId) {
        return request.delete(`/admin/tunnel/${tunnelId}/device/${deviceId}`)
    },

    // 添加摄像头到隧道
    addCamera(tunnelId, cameraId) {
        return request.post(`/admin/tunnel/${tunnelId}/camera/${cameraId}`)
    },

    // 从隧道移除摄像头
    removeCamera(tunnelId, cameraId) {
        return request.delete(`/admin/tunnel/${tunnelId}/camera/${cameraId}`)
    }
} 