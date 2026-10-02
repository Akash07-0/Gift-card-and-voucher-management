import { useEffect, useState, useMemo, useCallback } from 'react';
import api, { messageFromError } from '../../services/api';
import StatCard from '../../components/StatCard';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

import { CURRENCIES, formatCurrency, formatDiscount } from '../../utils/currency';

export function formatDateTime(timestamp) {
  if (!timestamp) return '-';
  const d = new Date(timestamp);
  return d.toLocaleString('en-US', {
    month: 'numeric',
    day: 'numeric',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
    second: '2-digit',
    hour12: true
  });
}

const emptyVoucher = { code: '', description: '', discount: '', expiryDate: '', maxUsage: '', discountType: 'FIXED_AMOUNT', currency: 'INR' };
const emptyCard = { code: '', amount: '', expiryDate: '', currency: 'INR' };

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState('overview');
  const [dateRange, setDateRange] = useState('ALL');
  const [stats, setStats] = useState({
    totalVouchers: 0,
    activeVouchers: 0,
    totalGiftCards: 0,
    activeGiftCards: 0,
    totalRedemptions: 0,
    totalCustomers: 0,
    totalPurchases: 0,
    totalRewardsIssued: 0,
    totalRewardsRedeemed: 0,
    totalDiscountValue: 0,
    activePartnerBrands: 0,
    voucherActivityOverTime: [],
    redemptionActivityOverTime: [],
    purchaseActivityOverTime: [],
    rewardIssuanceOverTime: [],
    rewardRedemptionOverTime: [],
    voucherStatusDistribution: {},
    giftCardStatusDistribution: {}
  });

  const [vouchers, setVouchers] = useState([]);
  const [cards, setCards] = useState([]);
  const [voucherRedemptions, setVoucherRedemptions] = useState([]);
  const [giftCardRedemptions, setGiftCardRedemptions] = useState([]);
  const [users, setUsers] = useState([]);
  const [partnerBrands, setPartnerBrands] = useState([]);
  const [purchases, setPurchases] = useState([]);

  const [voucherForm, setVoucherForm] = useState(emptyVoucher);
  const [cardForm, setCardForm] = useState(emptyCard);
  const [brandForm, setBrandForm] = useState({ brandName: '', logo: '', currency: 'INR', redemptionMode: 'DEMO' });

  // Edit Modal State
  const [editingVoucher, setEditingVoucher] = useState(null);

  // Search and Filter States
  const [voucherSearch, setVoucherSearch] = useState('');
  const [voucherStatusFilter, setVoucherStatusFilter] = useState('ALL');

  const [cardSearch, setCardSearch] = useState('');
  const [cardStatusFilter, setCardStatusFilter] = useState('ALL');

  const [redemptionSearch, setRedemptionSearch] = useState('');
  const [redemptionTypeFilter, setRedemptionTypeFilter] = useState('ALL');

  const [userSearch, setUserSearch] = useState('');

  // Status and Loading
  const [loading, setLoading] = useState(true);
  const [notice, setNotice] = useState({ text: '', type: 'success' });
  const [submittingVoucher, setSubmittingVoucher] = useState(false);
  const [submittingCard, setSubmittingCard] = useState(false);

  // Minimum date for datepickers (tomorrow)
  const tomorrowStr = useMemo(() => {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().split('T')[0];
  }, []);

  const getDateParams = useCallback(() => {
    if (dateRange === 'ALL') return '';
    const end = new Date();
    const start = new Date();
    if (dateRange === 'TODAY') { start.setHours(0,0,0,0); }
    else if (dateRange === 'LAST_7') { start.setDate(end.getDate() - 7); }
    else if (dateRange === 'LAST_30') { start.setDate(end.getDate() - 30); }
    else if (dateRange === 'LAST_90') { start.setDate(end.getDate() - 90); }
    else if (dateRange === 'THIS_YEAR') { start.setMonth(0); start.setDate(1); }
    return `?startDate=${start.toISOString()}&endDate=${end.toISOString()}`;
  }, [dateRange]);

  const loadStatsOnly = useCallback(async () => {
    try {
      const statsRes = await api.get('/admin/stats' + getDateParams());
      if (statsRes.data) {
        setStats(statsRes.data);
      }
    } catch (err) {
      console.error('Failed to load stats', err);
    }
  }, [getDateParams]);

  useEffect(() => {
    loadStatsOnly();
  }, [loadStatsOnly]);

  async function loadData() {
    setLoading(true);
    try {
      const [vouchersRes, cardsRes, vRedeemRes, gRedeemRes, usersRes, brandsRes, purchasesRes] = await Promise.allSettled([
        api.get('/vouchers'),
        api.get('/gift-cards'),
        api.get('/redemptions'),
        api.get('/gift-cards/redemptions'),
        api.get('/admin/users'),
        api.get('/admin/partner-brands'),
        api.get('/admin/purchases')
      ]);

      if (vouchersRes.status === 'fulfilled') setVouchers(vouchersRes.value.data || []);
      if (cardsRes.status === 'fulfilled') setCards(cardsRes.value.data || []);
      if (vRedeemRes.status === 'fulfilled') setVoucherRedemptions(vRedeemRes.value.data || []);
      if (gRedeemRes.status === 'fulfilled') setGiftCardRedemptions(gRedeemRes.value.data || []);
      if (usersRes.status === 'fulfilled') setUsers(usersRes.value.data || []);
      if (brandsRes.status === 'fulfilled') setPartnerBrands(brandsRes.value.data || []);
      if (purchasesRes.status === 'fulfilled') setPurchases(purchasesRes.value.data || []);

      await loadStatsOnly();
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, []);

  async function handleCreateVoucher(e) {
    e.preventDefault();
    setSubmittingVoucher(true);
    setNotice({ text: '', type: 'success' });
    try {
      await api.post('/vouchers', {
        code: voucherForm.code.trim().toUpperCase(),
        description: voucherForm.description.trim(),
        discount: Number(voucherForm.discount),
        expiryDate: voucherForm.expiryDate,
        maxUsage: Number(voucherForm.maxUsage),
        discountType: voucherForm.discountType,
        currency: voucherForm.currency
      });
      setVoucherForm(emptyVoucher);
      setNotice({ text: `Voucher ${voucherForm.code.toUpperCase()} created successfully!`, type: 'success' });
      loadData();
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    } finally {
      setSubmittingVoucher(false);
    }
  }

  async function handleCreateCard(e) {
    e.preventDefault();
    setSubmittingCard(true);
    setNotice({ text: '', type: 'success' });
    try {
      await api.post('/gift-cards', {
        code: cardForm.code.trim().toUpperCase(),
        amount: Number(cardForm.amount),
        expiryDate: cardForm.expiryDate,
        currency: cardForm.currency
      });
      setCardForm(emptyCard);
      setNotice({ text: `Gift card ${cardForm.code.toUpperCase()} created successfully!`, type: 'success' });
      loadData();
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    } finally {
      setSubmittingCard(false);
    }
  }

  async function handleUpdateVoucher(e) {
    e.preventDefault();
    if (!editingVoucher) return;
    try {
      await api.put(`/vouchers/${editingVoucher.id}`, {
        code: editingVoucher.code.trim().toUpperCase(),
        description: editingVoucher.description.trim(),
        discount: Number(editingVoucher.discount),
        expiryDate: editingVoucher.expiryDate,
        maxUsage: Number(editingVoucher.maxUsage)
      });
      setEditingVoucher(null);
      setNotice({ text: `Voucher updated successfully!`, type: 'success' });
      loadData();
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    }
  }

  async function toggleVoucherStatus(v) {
    try {
      if (v.active) {
        if (!window.confirm(`Are you sure you want to deactivate voucher ${v.code}?`)) return;
        await api.put(`/admin/vouchers/${v.id}/deactivate`);
        setNotice({ text: `Voucher ${v.code} deactivated.`, type: 'success' });
      } else {
        await api.put(`/admin/vouchers/${v.id}/activate`);
        setNotice({ text: `Voucher ${v.code} activated.`, type: 'success' });
      }
      loadData();
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    }
  }

  async function toggleCardStatus(c) {
    try {
      if (c.active) {
        if (!window.confirm(`Are you sure you want to deactivate gift card ${c.code}?`)) return;
        await api.delete(`/gift-cards/${c.id}`);
        setNotice({ text: `Gift card ${c.code} deactivated.`, type: 'success' });
      } else {
        await api.put(`/gift-cards/${c.id}/activate`);
        setNotice({ text: `Gift card ${c.code} activated.`, type: 'success' });
      }
      loadData();
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    }
  }

  async function handleCreateBrand(e) {
    e.preventDefault();
    try {
      await api.post('/admin/partner-brands', brandForm);
      setNotice({ text: 'Partner brand added successfully.', type: 'success' });
      setBrandForm({ brandName: '', logo: '', currency: 'INR', redemptionMode: 'DEMO' });
      loadData();
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    }
  }

  async function toggleBrandStatus(b) {
    try {
      if (b.active) {
        await api.put(`/admin/partner-brands/${b.id}/deactivate`);
        setNotice({ text: `Brand ${b.brandName} deactivated.`, type: 'success' });
      } else {
        await api.put(`/admin/partner-brands/${b.id}/activate`);
        setNotice({ text: `Brand ${b.brandName} activated.`, type: 'success' });
      }
      loadData();
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    }
  }

  // Filtered lists
  const filteredVouchers = useMemo(() => {
    return vouchers.filter((v) => {
      const matchSearch =
        v.code.toLowerCase().includes(voucherSearch.toLowerCase()) ||
        v.description.toLowerCase().includes(voucherSearch.toLowerCase());
      const matchStatus =
        voucherStatusFilter === 'ALL'
          ? true
          : voucherStatusFilter === 'ACTIVE'
          ? v.active
          : !v.active;
      return matchSearch && matchStatus;
    });
  }, [vouchers, voucherSearch, voucherStatusFilter]);

  const filteredCards = useMemo(() => {
    return cards.filter((c) => {
      const matchSearch = c.code.toLowerCase().includes(cardSearch.toLowerCase());
      const matchStatus =
        cardStatusFilter === 'ALL'
          ? true
          : cardStatusFilter === 'ACTIVE'
          ? c.active
          : !c.active;
      return matchSearch && matchStatus;
    });
  }, [cards, cardSearch, cardStatusFilter]);

  const combinedRedemptions = useMemo(() => {
    const list = [
      ...voucherRedemptions.map((r) => ({
        id: `v-${r.id}`,
        type: 'VOUCHER',
        code: r.voucherCode,
        customer: r.userEmail,
        amount: r.discountType === 'PERCENTAGE' ? `${r.discount}%` : formatCurrency(r.discount, r.currency),
        balance: '-',
        date: r.redeemedAt,
        status: r.status || 'SUCCESS'
      })),
      ...giftCardRedemptions.map((r) => ({
        id: `g-${r.id}`,
        type: 'GIFT_CARD',
        code: r.giftCardCode,
        customer: r.userEmail,
        amount: formatCurrency(r.amount, r.currency),
        balance: formatCurrency(r.remainingBalance, r.currency),
        date: r.redeemedAt,
        status: 'SUCCESS'
      }))
    ];

    // Sort newest first
    list.sort((a, b) => new Date(b.date) - new Date(a.date));

    return list.filter((item) => {
      const matchSearch =
        item.code.toLowerCase().includes(redemptionSearch.toLowerCase()) ||
        item.customer.toLowerCase().includes(redemptionSearch.toLowerCase());
      const matchType =
        redemptionTypeFilter === 'ALL' ? true : item.type === redemptionTypeFilter;
      return matchSearch && matchType;
    });
  }, [voucherRedemptions, giftCardRedemptions, redemptionSearch, redemptionTypeFilter]);

  const filteredUsers = useMemo(() => {
    return users.filter(
      (u) =>
        u.name.toLowerCase().includes(userSearch.toLowerCase()) ||
        u.email.toLowerCase().includes(userSearch.toLowerCase())
    );
  }, [users, userSearch]);

  return (
    <div className="dashboard">
      {/* Hero Header */}
      <div className="hero-row">
        <div className="hero-text">
          <span className="eyebrow">ADMINISTRATOR CONSOLE</span>
          <h1>System Control & Value Management</h1>
          <p>Configure discount vouchers, issue stored-value gift cards, and inspect live customer redemptions.</p>
        </div>

        <div>
          <button className="btn btn-outline btn-sm" onClick={loadData} disabled={loading}>
            🔄 {loading ? 'Refreshing...' : 'Refresh Data'}
          </button>
        </div>
      </div>

      {/* 6 Real Database Stat Cards */}
      <div className="stats-grid">
        <StatCard
          label="Total Vouchers"
          value={stats.totalVouchers}
          subtext={`${stats.activeVouchers} active in inventory`}
          tone="green"
        />
        <StatCard
          label="Active Vouchers"
          value={stats.activeVouchers}
          subtext="Available for redemption"
          tone="green"
        />
        <StatCard
          label="Total Gift Cards"
          value={stats.totalGiftCards}
          subtext={`${stats.activeGiftCards} active in inventory`}
          tone="gold"
        />
        <StatCard
          label="Active Gift Cards"
          value={stats.activeGiftCards}
          subtext="Funded & unexpired"
          tone="gold"
        />
        <StatCard
          label="Total Redemptions"
          value={stats.totalRedemptions}
          subtext="Vouchers + Gift Cards"
          tone="blue"
        />
        <StatCard
          label="Total Customers"
          value={stats.totalCustomers}
          subtext="Registered customer accounts"
          tone="purple"
        />
      </div>

      {/* Notification banner */}
      {notice.text && (
        <div className={`notice ${notice.type}`}>
          <span>{notice.text}</span>
          <button type="button" className="notice-close" onClick={() => setNotice({ text: '', type: 'success' })}>
            ×
          </button>
        </div>
      )}

      {/* Tabs Navigation */}
      <nav className="tab-bar">
        <button
          type="button"
          className={`tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          📊 Operations
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'analytics' ? 'active' : ''}`}
          onClick={() => setActiveTab('analytics')}
        >
          📈 Advanced Analytics
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'vouchers' ? 'active' : ''}`}
          onClick={() => setActiveTab('vouchers')}
        >
          🎟️ Voucher Management ({vouchers.length})
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'giftcards' ? 'active' : ''}`}
          onClick={() => setActiveTab('giftcards')}
        >
          💳 Gift Card Management ({cards.length})
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'redemptions' ? 'active' : ''}`}
          onClick={() => setActiveTab('redemptions')}
        >
          📜 Redemption Audit Log ({combinedRedemptions.length})
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'customers' ? 'active' : ''}`}
          onClick={() => setActiveTab('customers')}
        >
          👥 Registered Users ({users.length})
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'partnerbrands' ? 'active' : ''}`}
          onClick={() => setActiveTab('partnerbrands')}
        >
          🤝 Partner Brands ({partnerBrands.length})
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'purchases' ? 'active' : ''}`}
          onClick={() => setActiveTab('purchases')}
        >
          💰 All Purchases ({purchases.length})
        </button>
      </nav>

      {/* TAB 0: ADVANCED ANALYTICS */}
      {activeTab === 'analytics' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div className="panel" style={{ padding: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h2 style={{ margin: 0 }}>Analytics Dashboard</h2>
            <select value={dateRange} onChange={(e) => setDateRange(e.target.value)} className="filter-select">
              <option value="ALL">All Time</option>
              <option value="TODAY">Today</option>
              <option value="LAST_7">Last 7 Days</option>
              <option value="LAST_30">Last 30 Days</option>
              <option value="LAST_90">Last 3 Months</option>
              <option value="THIS_YEAR">This Year</option>
            </select>
          </div>
          
          <div className="stats-grid">
            <StatCard label="Total Purchases" value={stats.totalPurchases} subtext="Total transactions" tone="green" />
            <StatCard label="Total Redemptions" value={stats.totalRedemptions} subtext="Vouchers + Gift Cards" tone="blue" />
            <StatCard label="Total Discount Value" value={formatCurrency(stats.totalDiscountValue)} subtext="Value given" tone="gold" />
            <StatCard label="Rewards Issued" value={stats.totalRewardsIssued} subtext="Gift Cards Generated" tone="purple" />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', gap: '24px' }}>
            <div className="card" style={{ padding: '20px', background: '#fff', borderRadius: '8px' }}>
              <h3>Activity Over Time</h3>
              <div style={{ height: '300px' }}>
                {stats.purchaseActivityOverTime?.length > 0 ? (
                  <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={stats.purchaseActivityOverTime}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="date" />
                      <YAxis />
                      <Tooltip />
                      <Legend />
                      <Line type="monotone" dataKey="value" stroke="#8884d8" name="Purchases" />
                    </LineChart>
                  </ResponsiveContainer>
                ) : (
                  <div style={{ textAlign: 'center', color: '#999', paddingTop: '100px' }}>No data available</div>
                )}
              </div>
            </div>

            <div className="card" style={{ padding: '20px', background: '#fff', borderRadius: '8px' }}>
              <h3>Voucher Activity</h3>
              <div style={{ height: '300px' }}>
                {stats.voucherActivityOverTime?.length > 0 ? (
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={stats.voucherActivityOverTime}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="date" />
                      <YAxis />
                      <Tooltip />
                      <Legend />
                      <Bar dataKey="value" fill="#82ca9d" name="Vouchers Created" />
                    </BarChart>
                  </ResponsiveContainer>
                ) : (
                  <div style={{ textAlign: 'center', color: '#999', paddingTop: '100px' }}>No data available</div>
                )}
              </div>
            </div>

            <div className="card" style={{ padding: '20px', background: '#fff', borderRadius: '8px' }}>
              <h3>Reward Issuance Over Time</h3>
              <div style={{ height: '300px' }}>
                {stats.rewardIssuanceOverTime?.length > 0 ? (
                  <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={stats.rewardIssuanceOverTime}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="date" />
                      <YAxis />
                      <Tooltip />
                      <Legend />
                      <Line type="monotone" dataKey="value" stroke="#ffc658" name="Rewards Issued" />
                    </LineChart>
                  </ResponsiveContainer>
                ) : (
                  <div style={{ textAlign: 'center', color: '#999', paddingTop: '100px' }}>No data available</div>
                )}
              </div>
            </div>
            
            <div className="card" style={{ padding: '20px', background: '#fff', borderRadius: '8px' }}>
              <h3>Status Distribution (Active vs Inactive)</h3>
              <div style={{ height: '300px', display: 'flex', justifyContent: 'space-around' }}>
                <ResponsiveContainer width="45%" height="100%">
                  <PieChart>
                    <Pie data={[
                      { name: 'Active', value: stats.activeVouchers },
                      { name: 'Inactive', value: stats.inactiveVouchers }
                    ]} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={80}>
                      <Cell fill="#4caf50" />
                      <Cell fill="#f44336" />
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
                <ResponsiveContainer width="45%" height="100%">
                  <PieChart>
                    <Pie data={[
                      { name: 'Active', value: stats.activeGiftCards },
                      { name: 'Inactive', value: stats.inactiveGiftCards }
                    ]} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={80}>
                      <Cell fill="#ffb300" />
                      <Cell fill="#f44336" />
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </div>

          </div>
        </div>
      )}

      {/* TAB FOR PURCHASES */}
      {activeTab === 'purchases' && (
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2 className="panel-title">All Purchases</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
                System-wide record of in-store purchases across all merchants.
              </p>
            </div>
          </div>
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Customer</th>
                  <th>Shop</th>
                  <th>Order Ref</th>
                  <th>Amount</th>
                  <th>Reward Generated</th>
                </tr>
              </thead>
              <tbody>
                {purchases.map((p) => (
                  <tr key={p.id}>
                    <td>{new Date(p.purchaseDate).toLocaleDateString()}</td>
                    <td>{p.customerName || 'Customer'}</td>
                    <td>{p.shopName || 'Shop'}</td>
                    <td><span className="code-cell">{p.orderId}</span></td>
                    <td style={{ fontWeight: '600' }}>{formatCurrency(p.amount, p.currency)}</td>
                    <td>
                      {p.rewardGenerated ? (
                        <span className="badge badge-active">{p.rewardGenerated}</span>
                      ) : (
                        <span style={{ color: '#999', fontSize: '0.9em' }}>None</span>
                      )}
                    </td>
                  </tr>
                ))}
                {purchases.length === 0 && (
                  <tr>
                    <td colSpan="6" className="empty-state">
                      <div className="empty-state-icon">💰</div>
                      <h3>No purchases yet</h3>
                      <p>In-store purchases will appear here.</p>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 1: OVERVIEW (OPERATIONS) */}
      {activeTab === 'overview' && (
        <div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '24px' }}>
            {/* Quick Create Voucher */}
            <div className="panel">
              <div className="panel-header">
                <h2 className="panel-title">🎟️ Quick Issue Voucher</h2>
                <span className="badge badge-voucher">PROMOTION</span>
              </div>
              <form onSubmit={handleCreateVoucher} className="form-grid">
                <div className="form-group">
                  <label>Voucher Code</label>
                  <input
                    type="text"
                    placeholder="e.g. SUMMER50"
                    value={voucherForm.code}
                    onChange={(e) => setVoucherForm({ ...voucherForm, code: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group" style={{ display: 'flex', gap: '8px' }}>
                  <div style={{ flex: 1 }}>
                    <label>Discount Type</label>
                    <select
                      value={voucherForm.discountType}
                      onChange={(e) => setVoucherForm({ ...voucherForm, discountType: e.target.value })}
                      style={{ width: '100%', padding: '8px' }}
                    >
                      <option value="FIXED_AMOUNT">Fixed Amount</option>
                      <option value="PERCENTAGE">Percentage</option>
                    </select>
                  </div>
                  {voucherForm.discountType === 'FIXED_AMOUNT' && (
                    <div style={{ flex: 1 }}>
                      <label>Currency</label>
                      <select
                        value={voucherForm.currency}
                        onChange={(e) => setVoucherForm({ ...voucherForm, currency: e.target.value })}
                        style={{ width: '100%', padding: '8px' }}
                      >
                        {CURRENCIES.map(c => (
                          <option key={c.code} value={c.code}>{c.label}</option>
                        ))}
                      </select>
                    </div>
                  )}
                </div>
                <div className="form-group">
                  <label>Discount</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0.01"
                    placeholder="50"
                    value={voucherForm.discount}
                    onChange={(e) => setVoucherForm({ ...voucherForm, discount: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group full-width">
                  <label>Description</label>
                  <input
                    type="text"
                    placeholder="e.g. 50% discount on summer collection"
                    value={voucherForm.description}
                    onChange={(e) => setVoucherForm({ ...voucherForm, description: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Expiry Date</label>
                  <input
                    type="date"
                    min={tomorrowStr}
                    value={voucherForm.expiryDate}
                    onChange={(e) => setVoucherForm({ ...voucherForm, expiryDate: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Max Usage Limit</label>
                  <input
                    type="number"
                    min="1"
                    placeholder="100"
                    value={voucherForm.maxUsage}
                    onChange={(e) => setVoucherForm({ ...voucherForm, maxUsage: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group full-width" style={{ marginTop: '6px' }}>
                  <button type="submit" className="btn btn-primary" disabled={submittingVoucher}>
                    {submittingVoucher ? 'Creating...' : '+ Create Voucher'}
                  </button>
                </div>
              </form>
            </div>

            {/* Quick Create Gift Card */}
            <div className="panel">
              <div className="panel-header">
                <h2 className="panel-title">💳 Quick Issue Gift Card</h2>
                <span className="badge badge-giftcard">STORED VALUE</span>
              </div>
              <form onSubmit={handleCreateCard} className="form-grid">
                <div className="form-group">
                  <label>Gift Card Code</label>
                  <input
                    type="text"
                    placeholder="e.g. GIFT-100-XYZ"
                    value={cardForm.code}
                    onChange={(e) => setCardForm({ ...cardForm, code: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group" style={{ display: 'flex', gap: '8px' }}>
                  <div style={{ flex: 1 }}>
                    <label>Currency</label>
                    <select
                      value={cardForm.currency}
                      onChange={(e) => setCardForm({ ...cardForm, currency: e.target.value })}
                      style={{ width: '100%', padding: '8px' }}
                    >
                      {CURRENCIES.map(c => (
                        <option key={c.code} value={c.code}>{c.label}</option>
                      ))}
                    </select>
                  </div>
                  <div style={{ flex: 2 }}>
                    <label>Stored Amount</label>
                    <input
                      type="number"
                      step="0.01"
                      min="0.01"
                      placeholder="100"
                      value={cardForm.amount}
                      onChange={(e) => setCardForm({ ...cardForm, amount: e.target.value })}
                      required
                    />
                  </div>
                </div>
                <div className="form-group full-width">
                  <label>Expiry Date</label>
                  <input
                    type="date"
                    min={tomorrowStr}
                    value={cardForm.expiryDate}
                    onChange={(e) => setCardForm({ ...cardForm, expiryDate: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group full-width" style={{ marginTop: '24px' }}>
                  <button type="submit" className="btn btn-gold" disabled={submittingCard}>
                    {submittingCard ? 'Creating...' : '+ Create Gift Card'}
                  </button>
                </div>
              </form>
            </div>
          </div>

          {/* Recent Redemptions in Overview */}
          <div className="panel">
            <div className="panel-header">
              <div>
                <h2 className="panel-title">Recent Redemptions Audit</h2>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>Latest value consumed across all customers.</p>
              </div>
              <button className="btn btn-outline btn-sm" onClick={() => setActiveTab('redemptions')}>
                View All Redemptions →
              </button>
            </div>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Type</th>
                    <th>Code</th>
                    <th>Customer</th>
                    <th>Redeemed Value</th>
                    <th>Remaining Balance</th>
                    <th>Date & Time</th>
                  </tr>
                </thead>
                <tbody>
                  {combinedRedemptions.slice(0, 5).map((r) => (
                    <tr key={r.id}>
                      <td>
                        <span className={`badge ${r.type === 'VOUCHER' ? 'badge-voucher' : 'badge-giftcard'}`}>
                          {r.type === 'VOUCHER' ? '🎟️ Voucher' : '💳 Gift Card'}
                        </span>
                      </td>
                      <td><span className="code-cell">{r.code}</span></td>
                      <td>{r.customer}</td>
                      <td><strong>{r.amount}</strong></td>
                      <td>{r.balance}</td>
                      <td style={{ color: 'var(--text-muted)' }}>{formatDateTime(r.date)}</td>
                    </tr>
                  ))}
                  {combinedRedemptions.length === 0 && (
                    <tr>
                      <td colSpan="6" className="empty-state">No redemptions recorded yet.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: VOUCHER MANAGEMENT */}
      {activeTab === 'vouchers' && (
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2 className="panel-title">Voucher Inventory & Rules</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>Search, edit, activate, or deactivate promotional vouchers.</p>
            </div>
            <div className="filter-bar">
              <input
                type="text"
                className="search-input"
                placeholder="Search by code or description..."
                value={voucherSearch}
                onChange={(e) => setVoucherSearch(e.target.value)}
              />
              <select
                className="filter-select"
                value={voucherStatusFilter}
                onChange={(e) => setVoucherStatusFilter(e.target.value)}
              >
                <option value="ALL">All Statuses</option>
                <option value="ACTIVE">Active Only</option>
                <option value="INACTIVE">Inactive Only</option>
              </select>
            </div>
          </div>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Voucher Code</th>
                  <th>Description</th>
                  <th>Discount</th>
                  <th>Usage (Current / Max)</th>
                  <th>Expiry Date</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredVouchers.map((v) => (
                  <tr key={v.id}>
                    <td>#{v.id}</td>
                    <td><span className="code-cell">{v.code}</span></td>
                    <td>{v.description}</td>
                    <td><strong>{v.discountType === 'PERCENTAGE' ? `${v.discount}%` : formatCurrency(v.discount, v.currency)}</strong></td>
                    <td>
                      <span style={{ fontWeight: 600 }}>{v.currentUsage}</span> / {v.maxUsage}
                    </td>
                    <td>{v.expiryDate}</td>
                    <td>
                      <span className={`badge ${v.active ? 'badge-active' : 'badge-inactive'}`}>
                        {v.active ? '● Active' : '○ Inactive'}
                      </span>
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <div style={{ display: 'inline-flex', gap: '8px' }}>
                        <button
                          type="button"
                          className="btn btn-outline btn-sm"
                          onClick={() => setEditingVoucher(v)}
                        >
                          ✏️ Edit
                        </button>
                        {v.active ? (
                          <button
                            type="button"
                            className="btn btn-danger btn-sm"
                            onClick={() => toggleVoucherStatus(v)}
                          >
                            Deactivate
                          </button>
                        ) : (
                          <button
                            type="button"
                            className="btn btn-primary btn-sm"
                            onClick={() => toggleVoucherStatus(v)}
                          >
                            Activate
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
                {filteredVouchers.length === 0 && (
                  <tr>
                    <td colSpan="8" className="empty-state">
                      <div className="empty-state-icon">🎟️</div>
                      <h3>No vouchers found</h3>
                      <p>Try adjusting your search query or filter criteria.</p>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 3: GIFT CARD MANAGEMENT */}
      {activeTab === 'giftcards' && (
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2 className="panel-title">Gift Card Inventory</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>Monitor stored-value gift cards, remaining balances, and active statuses.</p>
            </div>
            <div className="filter-bar">
              <input
                type="text"
                className="search-input"
                placeholder="Search by card code..."
                value={cardSearch}
                onChange={(e) => setCardSearch(e.target.value)}
              />
              <select
                className="filter-select"
                value={cardStatusFilter}
                onChange={(e) => setCardStatusFilter(e.target.value)}
              >
                <option value="ALL">All Statuses</option>
                <option value="ACTIVE">Active Only</option>
                <option value="INACTIVE">Inactive Only</option>
              </select>
            </div>
          </div>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Gift Card Code</th>
                  <th>Original Amount</th>
                  <th>Remaining Balance</th>
                  <th>Expiry Date</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredCards.map((c) => (
                  <tr key={c.id}>
                    <td>#{c.id}</td>
                    <td><span className="code-cell">{c.code}</span></td>
                    <td>{formatCurrency(c.amount, c.currency)}</td>
                    <td>
                      <strong style={{ color: c.balance > 0 ? 'var(--gold)' : 'var(--text-muted)' }}>
                        {formatCurrency(c.balance, c.currency)}
                      </strong>
                    </td>
                    <td>{c.expiryDate}</td>
                    <td>
                      <span className={`badge ${c.active ? 'badge-active' : 'badge-inactive'}`}>
                        {c.active ? '● Active' : '○ Inactive'}
                      </span>
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <div style={{ display: 'inline-flex', gap: '8px' }}>
                        {c.active ? (
                          <button
                            type="button"
                            className="btn btn-danger btn-sm"
                            onClick={() => toggleCardStatus(c)}
                          >
                            Deactivate
                          </button>
                        ) : (
                          <button
                            type="button"
                            className="btn btn-primary btn-sm"
                            onClick={() => toggleCardStatus(c)}
                          >
                            Activate
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
                {filteredCards.length === 0 && (
                  <tr>
                    <td colSpan="7" className="empty-state">
                      <div className="empty-state-icon">💳</div>
                      <h3>No gift cards found</h3>
                      <p>Try adjusting your search query or filter criteria.</p>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 4: REDEMPTIONS AUDIT LOG */}
      {activeTab === 'redemptions' && (
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2 className="panel-title">Comprehensive Redemption Audit Log</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>Complete immutable log of all voucher and gift card redemptions.</p>
            </div>
            <div className="filter-bar">
              <input
                type="text"
                className="search-input"
                placeholder="Search by code or customer email..."
                value={redemptionSearch}
                onChange={(e) => setRedemptionSearch(e.target.value)}
              />
              <select
                className="filter-select"
                value={redemptionTypeFilter}
                onChange={(e) => setRedemptionTypeFilter(e.target.value)}
              >
                <option value="ALL">All Types</option>
                <option value="VOUCHER">Vouchers Only</option>
                <option value="GIFT_CARD">Gift Cards Only</option>
              </select>
            </div>
          </div>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Type</th>
                  <th>Offer Code</th>
                  <th>Customer Email</th>
                  <th>Redeemed Amount</th>
                  <th>Remaining Balance</th>
                  <th>Timestamp</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {combinedRedemptions.map((r) => (
                  <tr key={r.id}>
                    <td>
                      <span className={`badge ${r.type === 'VOUCHER' ? 'badge-voucher' : 'badge-giftcard'}`}>
                        {r.type === 'VOUCHER' ? '🎟️ Voucher' : '💳 Gift Card'}
                      </span>
                    </td>
                    <td><span className="code-cell">{r.code}</span></td>
                    <td>{r.customer}</td>
                    <td><strong>{r.amount}</strong></td>
                    <td>{r.balance}</td>
                    <td style={{ color: 'var(--text-muted)' }}>{formatDateTime(r.date)}</td>
                    <td>
                      <span className="badge badge-active">{r.status}</span>
                    </td>
                  </tr>
                ))}
                {combinedRedemptions.length === 0 && (
                  <tr>
                    <td colSpan="7" className="empty-state">
                      <div className="empty-state-icon">📜</div>
                      <h3>No redemptions found</h3>
                      <p>No redemption history matching your filter criteria.</p>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 5: REGISTERED CUSTOMERS */}
      {activeTab === 'customers' && (
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2 className="panel-title">Registered Accounts & Roles</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>Overview of all registered platform users and administrators.</p>
            </div>
            <div className="filter-bar">
              <input
                type="text"
                className="search-input"
                placeholder="Search user name or email..."
                value={userSearch}
                onChange={(e) => setUserSearch(e.target.value)}
              />
            </div>
          </div>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>User Name</th>
                  <th>Email Address</th>
                  <th>Role</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map((u) => (
                  <tr key={u.id}>
                    <td>#{u.id}</td>
                    <td style={{ fontWeight: 600 }}>{u.name}</td>
                    <td>{u.email}</td>
                    <td>
                      <span className={`role-pill ${u.role === 'ADMIN' ? 'admin' : ''}`}>
                        {u.role}
                      </span>
                    </td>
                  </tr>
                ))}
                {filteredUsers.length === 0 && (
                  <tr>
                    <td colSpan="4" className="empty-state">
                      <div className="empty-state-icon">👥</div>
                      <h3>No users found</h3>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* EDIT VOUCHER MODAL */}
      {editingVoucher && (
        <div className="modal-overlay" onClick={() => setEditingVoucher(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">Edit Voucher #{editingVoucher.id}</h3>
              <button
                type="button"
                className="notice-close"
                onClick={() => setEditingVoucher(null)}
              >
                ×
              </button>
            </div>
            <form onSubmit={handleUpdateVoucher}>
              <div className="form-group" style={{ marginBottom: '14px' }}>
                <label>Voucher Code</label>
                <input
                  type="text"
                  value={editingVoucher.code}
                  onChange={(e) => setEditingVoucher({ ...editingVoucher, code: e.target.value })}
                  required
                />
              </div>
              <div className="form-group" style={{ marginBottom: '14px' }}>
                <label>Description</label>
                <input
                  type="text"
                  value={editingVoucher.description}
                  onChange={(e) => setEditingVoucher({ ...editingVoucher, description: e.target.value })}
                  required
                />
              </div>
              <div className="form-group" style={{ marginBottom: '14px' }}>
                <label>Discount Amount</label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  value={editingVoucher.discount}
                  onChange={(e) => setEditingVoucher({ ...editingVoucher, discount: e.target.value })}
                  required
                />
              </div>
              <div className="form-group" style={{ marginBottom: '14px' }}>
                <label>Expiry Date</label>
                <input
                  type="date"
                  min={tomorrowStr}
                  value={editingVoucher.expiryDate}
                  onChange={(e) => setEditingVoucher({ ...editingVoucher, expiryDate: e.target.value })}
                  required
                />
              </div>
              <div className="form-group" style={{ marginBottom: '20px' }}>
                <label>Max Usage Limit</label>
                <input
                  type="number"
                  min="1"
                  value={editingVoucher.maxUsage}
                  onChange={(e) => setEditingVoucher({ ...editingVoucher, maxUsage: e.target.value })}
                  required
                />
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-outline"
                  onClick={() => setEditingVoucher(null)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Save Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* TAB 6: PARTNER BRANDS */}
      {activeTab === 'partnerbrands' && (
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2 className="panel-title">🤝 Partner Brands</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>Manage integrations for partner reward brands.</p>
            </div>
          </div>
          
          <div style={{ marginBottom: '20px', padding: '16px', background: '#f8f9fa', borderRadius: '8px' }}>
            <h3 style={{ margin: '0 0 16px 0', fontSize: '1rem' }}>+ Add Partner Brand</h3>
            <form onSubmit={handleCreateBrand} style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', alignItems: 'flex-end' }}>
              <div className="form-group" style={{ flex: 1, minWidth: '200px' }}>
                <label>Brand Name (e.g. Amazon)</label>
                <input type="text" value={brandForm.brandName} onChange={e => setBrandForm({...brandForm, brandName: e.target.value})} required />
              </div>
              <div className="form-group" style={{ flex: 1, minWidth: '150px' }}>
                <label>Currency</label>
                <select value={brandForm.currency} onChange={e => setBrandForm({...brandForm, currency: e.target.value})}>
                  {CURRENCIES.map(c => <option key={c.code} value={c.code}>{c.code}</option>)}
                </select>
              </div>
              <div className="form-group" style={{ flex: 1, minWidth: '150px' }}>
                <label>Integration Mode</label>
                <select value={brandForm.redemptionMode} onChange={e => setBrandForm({...brandForm, redemptionMode: e.target.value})}>
                  <option value="DEMO">DEMO</option>
                  <option value="MANUAL">MANUAL</option>
                  <option value="REDIRECT">REDIRECT</option>
                  <option value="EXTERNAL_API">EXTERNAL_API</option>
                </select>
              </div>
              <button type="submit" className="btn btn-primary" style={{ height: '38px', padding: '0 24px' }}>Add Brand</button>
            </form>
          </div>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Brand Name</th>
                  <th>Currency</th>
                  <th>Mode</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {partnerBrands.map(b => (
                  <tr key={b.id}>
                    <td><strong>{b.brandName}</strong></td>
                    <td>{b.currency}</td>
                    <td>{b.redemptionMode}</td>
                    <td>
                      <span className={`badge ${b.active ? 'badge-active' : 'badge-inactive'}`}>
                        {b.active ? '● Active' : '○ Inactive'}
                      </span>
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      {b.active ? (
                        <button type="button" className="btn btn-danger btn-sm" onClick={() => toggleBrandStatus(b)}>Deactivate</button>
                      ) : (
                        <button type="button" className="btn btn-primary btn-sm" onClick={() => toggleBrandStatus(b)}>Activate</button>
                      )}
                    </td>
                  </tr>
                ))}
                {partnerBrands.length === 0 && (
                  <tr>
                    <td colSpan="5" className="empty-state">No partner brands configured yet.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}