import request from '@/utils/request'

// 环境监测API
export default {
    // 创建环境数据
    create(data) {
        return request.post('/admin/environment', data)
    },

    // 更新环境数据
    update(id, data) {
        return request.put(`/admin/environment/${id}`, data)
    },

    // 删除环境数据
    delete(id) {
        return request.delete(`/admin/environment/${id}`)
    },

    // 获取环境数据详情
    getById(id) {
        return request.get(`/admin/environment/${id}`)
    },

    // 获取环境数据列表
    getList(params) {
        return request.get('/admin/environment/list', { params })
    },

    // 获取隧道列表
    getTunnels() {
        return request({
            url: '/admin/tunnel/list',
            method: 'get'
        }).then(res => {
            // 处理响应数据，只保留需要的字段
            if (res.data && res.data.content) {
                // 提取第一层数据，避免处理嵌套的设备信息
                res.data.content = res.data.content.map(tunnel => {
                    const { id, name, length, width, height, location, status, type, description, constructionTime, openTime, manager, contact } = tunnel;
                    return {
                        id, name, length, width, height, location, status, type, description, constructionTime, openTime, manager, contact
                    };
                });
            }
            return res;
        });
    },

    // 获取最新环境数据
    getLatestData(tunnelId) {
        return request({
            url: `/admin/tunnel/${tunnelId}/environment/latest`,
            method: 'get'
        })
    },

    // 获取历史环境数据
    getHistoryData({ tunnelId, startTime, endTime }) {
        return request({
            url: `/admin/tunnel/${tunnelId}/environment/history`,
            method: 'get',
            params: { startTime, endTime }
        })
    },

    // 获取环境数据统计
    getStatistics({ tunnelId, type, date }) {
        return request({
            url: '/admin/environment/statistics',
            method: 'get',
            params: { location: tunnelId, type, date }
        })
    },

    // 获取报警阈值
    getAlarmThreshold() {
        return request({
            url: '/admin/environment/threshold',
            method: 'get'
        })
    },

    // 更新报警阈值
    updateAlarmThreshold(thresholds) {
        return request.put('/admin/environment/threshold', thresholds)
    },

    // 检查环境数据是否超过阈值
    checkAlarmThreshold(data) {
        return request({
            url: '/admin/environment/check-alarm',
            method: 'post',
            data
        })
    },

    // 导出环境数据
    exportData(params) {
        return request.get('/admin/environment/export', {
            params,
            responseType: 'blob'
        })
    }
} 