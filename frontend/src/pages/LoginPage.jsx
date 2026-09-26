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
    setBusy(true);
    setMessage('');
    try {
      const email = form.email ? form.email.trim() : '';
      if (mode === 'login') {
        await login(email, form.password);
      } else {
        await register(form.name.trim(), email, form.password);
      }
    } catch (error) {
      setMessage(messageFromError(error));
    } finally {
      setBusy(false);
    }
  }

  function fillCredentials(email, password) {
    setMode('login');
    setForm({ name: '', email, password });
    setMessage('');
  }

  return (
    <main className="auth-container">
      <div className="auth-hero">
        <div>
          <span className="eyebrow" style={{ color: '#b9dcce' }}>CAPSTONE PROJECT</span>
          <div className="brand" style={{ color: '#ffffff', margin: '20px 0 0' }}>
            <span className="brand-mark" style={{ background: '#ffffff', color: '#124e3f' }}>GC</span>
            <span>Gift Card and Voucher Management System</span>
          </div>
        </div>

        <div className="auth-hero-content">
          <h1>Manage and redeem value seamlessly.</h1>
          <p>
            A full-stack, role-based platform for issuing vouchers, provisioning stored-value gift cards,
            and executing secure, auditable redemptions.
          </p>
        </div>

        <div style={{ fontSize: '0.85rem', color: '#b9dcce' }}>
          Spring Boot 3.5 · Spring Security · JWT · MySQL · React
        </div>
      </div>

      <div className="auth-card-panel">
        <div className="auth-card-inner">
          <div className="auth-tabs">
            <button
              type="button"
              className={`auth-tab-btn ${mode === 'login' ? 'active' : ''}`}
              onClick={() => { setMode('login'); setMessage(''); }}
            >
              Sign In
            </button>
            <button
              type="button"
              className={`auth-tab-btn ${mode === 'register' ? 'active' : ''}`}
              onClick={() => { setMode('register'); setMessage(''); }}
            >
              Register Customer
            </button>
          </div>

          <div style={{ marginBottom: '20px' }}>
            <h2 style={{ fontFamily: 'var(--font-heading)', color: 'var(--primary)', fontSize: '1.6rem', marginBottom: '4px' }}>
              {mode === 'login' ? 'Welcome back' : 'Create Customer Account'}
            </h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              {mode === 'login' ? 'Enter your credentials to access your dashboard.' : 'Sign up to view and redeem available offers.'}
            </p>
          </div>

          {/* Quick Demo Credentials Box */}
          <div className="credentials-box">
            <strong>Demo Credentials (Click to auto-fill):</strong>
            <div style={{ display: 'flex', gap: '16px', marginTop: '6px', flexWrap: 'wrap' }}>
              <span
                className="cred-link"
                onClick={() => fillCredentials('admin@voucher.com', 'Admin@123')}
              >
                🛡️ Admin Account
              </span>
              <span
                className="cred-link"
                onClick={() => fillCredentials('customer@voucher.com', 'Customer@123')}
              >
                👤 Customer Account
              </span>
            </div>
          </div>

          <form onSubmit={submit}>
            {mode === 'register' && (
              <div className="form-group" style={{ marginBottom: '16px' }}>
                <label>Full Name</label>
                <input
                  type="text"
                  placeholder="e.g. John Doe"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  required
                />
              </div>
            )}

            <div className="form-group" style={{ marginBottom: '16px' }}>
              <label>Email Address</label>
              <input
                type="email"
                placeholder="name@example.com"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                required
              />
            </div>

            <div className="form-group" style={{ marginBottom: '20px' }}>
              <label>Password</label>
              <input
                type="password"
                placeholder="••••••••"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                required
              />
            </div>

            {message && (
              <div className="notice error" style={{ marginBottom: '16px' }}>
                <span>{message}</span>
                <button type="button" className="notice-close" onClick={() => setMessage('')}>×</button>
              </div>
            )}

            <button type="submit" className="btn btn-primary" style={{ width: '100%', padding: '12px' }} disabled={busy}>
              {busy ? 'Authenticating...' : mode === 'login' ? 'Sign In to Dashboard' : 'Complete Registration'}
            </button>
          </form>
        </div>
      </div>
    </main>
  );
}
