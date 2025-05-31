import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
    baseURL: '/api', // API基础路径
    timeout: 15000 // 请求超时时间
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        console.log('发送请求:', {
            url: config.url,
            method: config.method,
            headers: config.headers,
            data: config.data,
            params: config.params
        });
        
        // 从localStorage获取token
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
            console.log('添加认证token:', token);
        } else {
            console.log('未找到认证token');
        }
        return config
    },
    error => {
        console.error('请求拦截器错误:', {
            message: error.message,
            config: error.config
        });
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        console.log('收到响应:', {
            status: response.status,
            statusText: response.statusText,
            headers: response.headers,
            data: response.data
        });
        
        // 获取响应数据
        let res = response.data;
        
        // 如果响应数据是字符串，尝试处理和解析
        if (typeof res === 'string') {
            try {
                // 尝试直接解析
                res = JSON.parse(res);
            } catch (e) {
                console.error('第一次解析失败，尝试清理数据');
                try {
                    // 提取有效的 JSON 部分
                    const match = res.match(/{"code":200,"message":"success","data":{"content":\[.*?\]}/);
                    if (match) {
                        res = JSON.parse(match[0]);
                        console.log('提取后的数据:', res);
                    } else {
                        throw new Error('无法提取有效数据');
                    }
                } catch (e2) {
                    console.error('数据处理失败:', e2);
                    return Promise.reject(new Error('响应数据格式错误'));
                }
            }
        }
        
        // 判断响应状态
        if (res.code === 200) {
            return res;
        } else {
            console.error('业务错误:', {
                code: res.code,
                message: res.message
            });
            ElMessage.error(res.message || '请求失败');
            return Promise.reject(new Error(res.message || '请求失败'));
        }
    },
    error => {
        console.error('响应错误详情:', {
            message: error.message,
            response: {
                status: error.response?.status,
                statusText: error.response?.statusText,
                data: error.response?.data
            },
            request: {
                url: error.config?.url,
                method: error.config?.method,
                headers: error.config?.headers
            }
        });
        const message = error.response?.data?.message || '服务器错误';
        ElMessage.error(message);
        return Promise.reject(error);
    }
);

export default request 