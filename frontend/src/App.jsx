import { useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import AdminDashboard from './pages/admin/AdminDashboard';
import CustomerDashboard from './pages/customer/CustomerDashboard';

export default function App() {
  const auth = useAuth();
  if (!auth.token || !auth.role) return <LoginPage />;
  const Dashboard = auth.role === 'ADMIN' ? AdminDashboard : CustomerDashboard;
  return <><header className="topbar"><div className="brand"><span className="brand-mark">SV</span><span>Secure Voucher</span></div><div className="topbar-right"><span className="role-pill">{auth.role}</span><button className="logout" onClick={auth.logout}>Log out</button></div></header><Dashboard /></>;
}
