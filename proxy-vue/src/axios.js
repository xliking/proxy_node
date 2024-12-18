import axios from 'axios';

// 创建一个Axios实例
const instance = axios.create({
    baseURL: 'http://127.0.0.1:9988', // 替换为你的固定IP和端口号
    timeout: 300000, // 请求超时时间（可选） 5分钟
    headers: {
        'Content-Type': 'application/json',
    },
});

// 请求拦截器（可选）
instance.interceptors.request.use(
    config => {
        // 在发送请求之前做些什么
        // 例如，添加认证Token
        // config.headers.Authorization = `Bearer ${token}`;
        return config;
    },
    error => {
        // 对请求错误做些什么
        return Promise.reject(error);
    }
);

// 响应拦截器（可选）
instance.interceptors.response.use(
    response => {
        // 对响应数据做些什么
        return response.data;
    },
    error => {
        // 对响应错误做些什么
        return Promise.reject(error);
    }
);

export default instance;
