import React from 'react';
import { useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import AdminDashboard from './pages/admin/AdminDashboard';
import CustomerDashboard from './pages/customer/CustomerDashboard';
import MerchantDashboard from './pages/merchant/MerchantDashboard';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Unhandled UI Error:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{ display: 'grid', placeItems: 'center', minHeight: '100vh', background: '#f5f2ea', color: '#19352e', fontFamily: 'DM Sans, sans-serif', padding: '24px' }}>
          <div style={{ background: '#fff', padding: '32px', borderRadius: '8px', maxWidth: '500px', width: '100%', boxShadow: '0 8px 30px rgba(0,0,0,0.08)', textAlign: 'center' }}>
            <h2 style={{ margin: '0 0 12px', color: '#a83e34' }}>Something went wrong</h2>
            <p style={{ color: '#68766f', margin: '0 0 20px', fontSize: '0.95rem' }}>
              {this.state.error?.message || 'An unexpected error occurred while rendering the dashboard.'}
            </p>
            <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
              <button
                className="button primary"
                onClick={() => {
                  this.setState({ hasError: false, error: null });
                  window.location.reload();
                }}
              >
                Reload page
              </button>
              <button
                className="button"
                style={{ background: '#e5efdf', color: '#1f6b52' }}
                onClick={() => {
                  localStorage.removeItem('voucher_token');
                  localStorage.removeItem('voucher_role');
                  window.location.href = '/';
                }}
              >
                Return to sign in
              </button>
            </div>
          </div>
        </div>
      );
    }
    return this.props.children;
  }
}

function MainLayout() {
  const auth = useAuth();

  if (auth.loading) {
    return (
      <div style={{ display: 'grid', placeItems: 'center', minHeight: '100vh', background: '#f5f2ea', color: '#19352e', fontFamily: 'DM Sans, sans-serif' }}>
        <div style={{ textAlign: 'center' }}>
          <div className="brand-mark" style={{ margin: '0 auto 16px' }}>GC</div>
          <p style={{ margin: 0, fontWeight: 500 }}>Loading application...</p>
        </div>
      </div>
    );
  }

  if (!auth.token || !auth.role) {
    return <LoginPage />;
  }

  const role = String(auth.role).toUpperCase();
  let Dashboard = CustomerDashboard;
  if (role === 'ADMIN') Dashboard = AdminDashboard;
  if (role === 'MERCHANT') Dashboard = MerchantDashboard;

  return (
    <>
      <header className="topbar">

        <div className="brand">
          <span className="brand-mark">GC</span>
          <span>Gift Card and Voucher Management System</span>
        </div>
        <div className="topbar-right">
          <div className="user-tag">
            <span className={`role-pill ${role === 'ADMIN' ? 'admin' : ''}`}>{auth.role}</span>
          </div>
          <button type="button" className="logout-btn" onClick={auth.logout}>
            Log out
          </button>
        </div>
      </header>
      <Dashboard />
    </>
  );
}

export default function App() {
  return (
    <ErrorBoundary>
      <MainLayout />
    </ErrorBoundary>
  );
}

