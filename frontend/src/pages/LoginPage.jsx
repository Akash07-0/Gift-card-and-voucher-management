import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { messageFromError } from '../services/api';

export default function LoginPage() {
  const { login, register } = useAuth();
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [message, setMessage] = useState('');
  const [busy, setBusy] = useState(false);

  async function submit(event) {
    event.preventDefault();
    setBusy(true); setMessage('');
    try {
      if (mode === 'login') await login(form.email, form.password);
      else await register(form.name, form.email, form.password);
    } catch (error) { setMessage(messageFromError(error)); }
    finally { setBusy(false); }
  }

  return <main className="auth-shell"><div className="auth-art"><span className="eyebrow">SECURE VOUCHER SYSTEM</span><h1>Benefits that move at the speed of trust.</h1><p>A calm, controlled workspace for issuing offers and redeeming value.</p><div className="art-mark">SV<span>•</span></div></div><form className="auth-card" onSubmit={submit}><span className="eyebrow">{mode === 'login' ? 'WELCOME BACK' : 'NEW CUSTOMER'}</span><h2>{mode === 'login' ? 'Sign in' : 'Create an account'}</h2>{mode === 'register' && <label>Name<input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required /></label>}<label>Email<input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required /></label><label>Password<input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required /></label>{message && <p className="error">{message}</p>}<button className="button primary" disabled={busy}>{busy ? 'Working...' : mode === 'login' ? 'Sign in' : 'Register'}</button><button type="button" className="text-button" onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setMessage(''); }}>{mode === 'login' ? 'Create a customer account' : 'Return to sign in'}</button></form></main>;
}
