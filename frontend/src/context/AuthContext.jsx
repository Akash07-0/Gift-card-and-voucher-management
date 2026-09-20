import { createContext, useContext, useMemo, useState } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => ({
    token: localStorage.getItem('voucher_token'),
    role: localStorage.getItem('voucher_role')
  }));

  async function login(email, password) {
    const { data } = await api.post('/auth/login', { email, password });
    localStorage.setItem('voucher_token', data.token);
    localStorage.setItem('voucher_role', data.role);
    setSession({ token: data.token, role: data.role });
  }

  async function register(name, email, password) {
    const { data } = await api.post('/auth/register', { name, email, password });
    localStorage.setItem('voucher_token', data.token);
    localStorage.setItem('voucher_role', data.role);
    setSession({ token: data.token, role: data.role });
  }

  function logout() {
    localStorage.removeItem('voucher_token');
    localStorage.removeItem('voucher_role');
    setSession({ token: null, role: null });
  }

  const value = useMemo(() => ({ ...session, login, register, logout }), [session]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
