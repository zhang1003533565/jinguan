import request from '@/utils/request'

// 摄像头管理API
export default {
    // 创建摄像头
    create(data) {
        return request.post('/admin/camera', data)
    },

    // 更新摄像头
    update(id, data) {
        return request.put(`/admin/camera/${id}`, data)
    },

    // 删除摄像头
    delete(id) {
        return request.delete(`/admin/camera/${id}`)
    },

    // 获取摄像头详情
    getById(id) {
        return request.get(`/admin/camera/${id}`)
    },

    // 获取摄像头列表
    getList(params) {
        return request.get('/admin/camera/list', { params })
    },

    // 根据状态获取摄像头列表
    getListByStatus(params) {
        return request.get('/admin/camera/status', { params })
    },

    // 获取在线摄像头列表
    getOnlineCameras() {
        return request.get('/admin/camera/online')
    },

    // 获取离线摄像头列表
    getOfflineCameras() {
        return request.get('/admin/camera/offline')
    },

    // 更新摄像头状态
    updateStatus(id, status) {
        return request.put(`/admin/camera/${id}/status`, null, { params: { status } })
    },

    // 更新摄像头在线状态
    updateOnlineStatus(id, isOnline) {
        return request.put(`/admin/camera/${id}/online-status`, null, { params: { isOnline } })
    },

    // 获取RTSP地址
    getRtspUrl(id) {
        return request.get(`/admin/camera/${id}/rtsp`)
    },

    // 控制云台
    controlPTZ(id, command, speed = 1) {
        return request.post(`/admin/camera/${id}/ptz`, null, { params: { command, speed } })
    },

    // 截图
    takeSnapshot(id) {
        return request.get(`/admin/camera/${id}/snapshot`)
    }
} 