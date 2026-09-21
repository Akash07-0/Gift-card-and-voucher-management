import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api'
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('voucher_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export function messageFromError(error) {
  if (!error.response) {
    return 'Backend server is not running. Start the backend and try again.';
  }
  return error.response.data?.message || error.response.data?.error || 'Login failed. Please check your details and try again.';
}

export default api;
