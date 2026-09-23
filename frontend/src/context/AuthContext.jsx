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
        if (!data || !data.role) {
          throw new Error('Invalid user role');
        }
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
    try {
      const cleanEmail = email ? email.trim() : '';
      const { data } = await api.post('/auth/login', { email: cleanEmail, password });
      if (!data || !data.token || !data.role) {
        throw new Error('Invalid response from authentication server');
      }
      localStorage.setItem('voucher_token', data.token);
      localStorage.setItem('voucher_role', data.role);
      setSession({ token: data.token, role: data.role, loading: false });
      return data;
    } catch (error) {
      localStorage.removeItem('voucher_token');
      localStorage.removeItem('voucher_role');
      setSession({ token: null, role: null, loading: false });
      throw error;
    }
  }

  async function register(name, email, password) {
    try {
      const cleanEmail = email ? email.trim() : '';
      const { data } = await api.post('/auth/register', { name, email: cleanEmail, password });
      if (!data || !data.token || !data.role) {
        throw new Error('Invalid response from registration server');
      }
      localStorage.setItem('voucher_token', data.token);
      localStorage.setItem('voucher_role', data.role);
      setSession({ token: data.token, role: data.role, loading: false });
      return data;
    } catch (error) {
      localStorage.removeItem('voucher_token');
      localStorage.removeItem('voucher_role');
      setSession({ token: null, role: null, loading: false });
      throw error;
    }
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
