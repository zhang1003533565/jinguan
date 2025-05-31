import config from '@/config'

const baseURL = `${config.baseURL}/api/mini`

// 请求封装
const request = (options = {}) => {
    return new Promise((resolve, reject) => {
        const token = uni.getStorageSync('token')
        
        uni.request({
            url: baseURL + options.url,
            method: options.method || 'GET',
            data: options.data,
            header: {
                'content-type': 'application/json',
                'Authorization': token ? `Bearer ${token}` : '',
                ...options.header
            },
            success: (res) => {
                if (res.statusCode === 200) {
                    if (res.data.code === 200) {
                        resolve(res.data)
                    } else {
                        // 业务错误处理
                        uni.showToast({
                            title: res.data.message || '请求失败',
                            icon: 'none'
                        })
                        reject(res.data)
                    }
                } else if (res.statusCode === 401) {
                    // token过期处理
                    uni.removeStorageSync('token')
                    uni.navigateTo({
                        url: '/pages/login/login'
                    })
                    reject(res)
                } else {
                    uni.showToast({
                        title: '网络错误',
                        icon: 'none'
                    })
                    reject(res)
                }
            },
            fail: (err) => {
                uni.showToast({
                    title: '请求失败',
                    icon: 'none'
                })
                reject(err)
            }
        })
    })
}

// 封装GET请求
const get = (url, data = {}) => {
    return request({
        url,
        data
    })
}

// 封装POST请求
const post = (url, data = {}) => {
    return request({
        url,
        method: 'POST',
        data
    })
}

export default {
    get,
    post
} 