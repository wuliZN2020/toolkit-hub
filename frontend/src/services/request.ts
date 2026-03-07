import axios, { type AxiosInstance, type AxiosResponse } from 'axios';
import { message } from 'antd';

// Response data structure
export interface ResponseData<T = any> {
  code: number;
  message: string;
  data: T;
}

// Create axios instance
const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
request.interceptors.request.use(
  (config) => {
    // Add token if needed
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor
request.interceptors.response.use(
  (response: AxiosResponse<ResponseData>) => {
    const { code, message: msg, data } = response.data;

    // Success
    if (code === 200) {
      return data;
    }

    // Business error
    message.error(msg || 'Request failed');
    return Promise.reject(new Error(msg || 'Request failed'));
  },
  (error) => {
    console.error('Response error:', error);

    if (error.response) {
      const { status, data } = error.response;

      switch (status) {
        case 400:
          message.error(data?.message || 'Bad request');
          break;
        case 401:
          message.error('Unauthorized, please login');
          // Redirect to login page
          break;
        case 403:
          message.error('Access forbidden');
          break;
        case 404:
          message.error('Resource not found');
          break;
        case 500:
          message.error(data?.message || 'Server error');
          break;
        default:
          message.error('Request failed');
      }
    } else if (error.request) {
      message.error('Network error, please check your connection');
    } else {
      message.error('Request error');
    }

    return Promise.reject(error);
  }
);

export default request;
