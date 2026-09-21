import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [session, setSession] = useState({ token: null, role: null, loading: true });

  useEffect(() => {
    const token = localStorage.getItem('voucher_token');
    if (!token) {
      setSession({ token: null, role: null, loading: false });
      return;
    }

    api.get('/auth/me')
      .then(({ data }) => {
        localStorage.setItem('voucher_role', data.role);
        setSession({ token, role: data.role, loading: false });
      })
      .catch(() => {
        localStorage.removeItem('voucher_token');
        localStorage.removeItem('voucher_role');
        setSession({ token: null, role: null, loading: false });
      });
  }, []);

  async function login(email, password) {
    localStorage.removeItem('voucher_token');
    localStorage.removeItem('voucher_role');
    setSession({ token: null, role: null, loading: true });
    const { data } = await api.post('/auth/login', { email, password });
    localStorage.setItem('voucher_token', data.token);
    localStorage.setItem('voucher_role', data.role);
    setSession({ token: data.token, role: data.role, loading: false });
  }

  async function register(name, email, password) {
    const { data } = await api.post('/auth/register', { name, email, password });
    localStorage.setItem('voucher_token', data.token);
    localStorage.setItem('voucher_role', data.role);
    setSession({ token: data.token, role: data.role, loading: false });
  }

  function logout() {
    localStorage.removeItem('voucher_token');
    localStorage.removeItem('voucher_role');
    setSession({ token: null, role: null, loading: false });
  }

  const value = useMemo(() => ({ ...session, login, register, logout }), [session]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
