let baseURL = ''

if (process.env.NODE_ENV === 'development') {
    baseURL = 'http://localhost:8080'
} else {
    baseURL = 'https://your-domain.com'  // 生产环境地址
}

export default {
    baseURL
} 