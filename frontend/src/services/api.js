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
    // If validation details exist, show detailed field errors
    if (data.details && typeof data.details === 'object' && Object.keys(data.details).length > 0) {
      const messages = Object.values(data.details).filter(Boolean);
      if (messages.length > 0) {
        return messages.join('. ');
      }
    }

    if (data.message && typeof data.message === 'string' && data.message !== 'No message available' && data.message !== 'Validation failed') {
      return data.message;
    }
    if (data.error && typeof data.error === 'string' && data.error !== 'Bad Request' && data.error !== 'Internal Server Error') {
      return data.error;
    }
  }

  if (error.response.status === 401) {
    return 'Invalid email or password.';
  }
  if (error.response.status === 403) {
    return 'Access denied. You do not have permission to perform this action.';
  }
  if (error.response.status === 404) {
    return data?.message || 'Requested resource not found.';
  }
  if (error.response.status === 409) {
    return data?.message || 'Conflict: Record already exists or state is invalid.';
  }
  if (error.response.status >= 500) {
    return data?.message || 'Internal server error. Please try again later.';
  }

  return 'Operation failed. Please check input values.';
}


export default api;
