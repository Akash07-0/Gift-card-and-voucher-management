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
  return error.response?.data?.message || error.response?.data?.error || 'Request failed. Please try again.';
}

export default api;
