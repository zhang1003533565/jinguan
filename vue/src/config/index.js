const config = {
    development: {
        baseURL: 'http://localhost:8080'
    },
    production: {
        baseURL: 'https://your-domain.com'  // 生产环境地址
    }
}

export default {
    baseURL: config[process.env.NODE_ENV || 'development'].baseURL
} 