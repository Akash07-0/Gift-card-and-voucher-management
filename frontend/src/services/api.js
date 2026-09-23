import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api'
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('voucher_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config?.url?.includes('/auth/login') && !error.config?.url?.includes('/auth/register')) {
      localStorage.removeItem('voucher_token');
      localStorage.removeItem('voucher_role');
    }
    return Promise.reject(error);
  }
);

export function messageFromError(error) {
  if (!error) return 'An unknown error occurred.';
  if (!error.response) {
    if (error.code === 'ERR_NETWORK' || error.message?.includes('Network Error')) {
      return 'Backend server is not running or unreachable on port 8080. Start the backend and try again.';
    }
    return error.message || 'Network error occurred. Please check your connection.';
  }

  const data = error.response.data;
  if (typeof data === 'string' && data.trim().length > 0) {
    if (!data.startsWith('<!doctype') && !data.startsWith('<html')) {
      return data;
    }
  }

  if (data && typeof data === 'object') {
    if (data.message && typeof data.message === 'string' && data.message !== 'No message available') {
      return data.message;
    }
    if (data.error && typeof data.error === 'string') {
      return data.error;
    }
    if (data.details && typeof data.details === 'object') {
      const firstDetail = Object.values(data.details)[0];
      if (firstDetail) return String(firstDetail);
    }
  }

  if (error.response.status === 401) {
    return 'Invalid email or password.';
  }
  if (error.response.status === 403) {
    return 'Access denied. You do not have permission to perform this action.';
  }
  if (error.response.status === 404) {
    return 'Requested resource not found.';
  }
  if (error.response.status >= 500) {
    return 'Internal server error. Please try again later.';
  }

  return 'Operation failed. Please try again.';
}

export default api;
