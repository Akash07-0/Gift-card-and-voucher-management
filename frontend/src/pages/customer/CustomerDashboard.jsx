import { useEffect, useState, useMemo } from 'react';
import { useAuth } from '../../context/AuthContext';
import api, { messageFromError } from '../../services/api';
import StatCard from '../../components/StatCard';
import { QRCodeSVG } from 'qrcode.react';
import { formatCurrency, formatDiscount } from '../../utils/currency';

export default function CustomerDashboard() {
  const auth = useAuth();
  const [activeTab, setActiveTab] = useState('vouchers');

  const [vouchers, setVouchers] = useState([]);
  const [cards, setCards] = useState([]);
  const [voucherHistory, setVoucherHistory] = useState([]);
  const [giftHistory, setGiftHistory] = useState([]);
  const [rewards, setRewards] = useState([]);
  const [purchases, setPurchases] = useState([]);

  // Search & Filter States
  const [voucherSearch, setVoucherSearch] = useState('');
  const [cardSearch, setCardSearch] = useState('');
  const [historySearch, setHistorySearch] = useState('');
  const [historyTypeFilter, setHistoryTypeFilter] = useState('ALL');

  // Gift Card Redeem Modal
  const [selectedCard, setSelectedCard] = useState(null);
  const [redeemAmount, setRedeemAmount] = useState('');
  const [redeeming, setRedeeming] = useState(false);

  // QR Modal
  const [qrToken, setQrToken] = useState(null);
  const [qrModalOpen, setQrModalOpen] = useState(false);
  const [qrVoucher, setQrVoucher] = useState(null);
  const [generatingQr, setGeneratingQr] = useState(false);

  // Status and Alerts
  const [loading, setLoading] = useState(true);
  const [notice, setNotice] = useState({ text: '', type: 'success' });

  async function loadData() {
    setLoading(true);
    try {
      const [vRes, cRes, vhRes, ghRes, rRes, pRes] = await Promise.allSettled([
        api.get('/vouchers/available'),
        api.get('/gift-cards/available'),
        api.get('/redemptions/my-history'),
        api.get('/gift-cards/my-history'),
        api.get('/customer/rewards'),
        api.get('/customer/purchases')
      ]);

      if (vRes.status === 'fulfilled') setVouchers(vRes.value.data || []);
      if (cRes.status === 'fulfilled') setCards(cRes.value.data || []);
      if (vhRes.status === 'fulfilled') setVoucherHistory(vhRes.value.data || []);
      if (ghRes.status === 'fulfilled') setGiftHistory(ghRes.value.data || []);
      if (rRes.status === 'fulfilled') setRewards(rRes.value.data || []);
      if (pRes.status === 'fulfilled') setPurchases(pRes.value.data || []);
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, []);

  async function handleRedeemVoucher(code) {
    if (!window.confirm(`Redeem voucher code "${code}" now?`)) return;
    try {
      const { data } = await api.post('/redemptions', { code });
      setNotice({
        text: `Success! Voucher ${code} redeemed for ${formatCurrency(data.discount, data.currency)} discount!`,
        type: 'success'
      });
      loadData();
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    }
  }

  async function handleShowQr(voucher) {
    setGeneratingQr(true);
    try {
      const { data } = await api.get(`/vouchers/${voucher.code}/qr`);
      setQrToken(data.qrToken);
      setQrVoucher(voucher);
      setQrModalOpen(true);
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    } finally {
      setGeneratingQr(false);
    }
  }

  async function handleRedeemReward(id) {
    setGeneratingQr(true); // Reusing loader
    try {
      const { data } = await api.post(`/customer/rewards/${id}/redeem`);
      setNotice({
        text: `Demo Partner Redemption Successful! Ref: ${data.reference}`,
        type: 'success'
      });
      loadData();
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    } finally {
      setGeneratingQr(false);
    }
  }

  function openCardRedeemModal(card) {
    setSelectedCard(card);
    setRedeemAmount(card.balance > 10 ? '10' : String(card.balance));
  }

  async function handleRedeemCardSubmit(e) {
    e.preventDefault();
    if (!selectedCard) return;

    const amountNum = Number(redeemAmount);
    if (isNaN(amountNum) || amountNum <= 0) {
      setNotice({ text: 'Please enter a valid amount greater than 0', type: 'error' });
      return;
    }
    if (amountNum > selectedCard.balance) {
      setNotice({
        text: `Redeem amount (${formatCurrency(amountNum, selectedCard.currency)}) exceeds remaining balance (${formatCurrency(selectedCard.balance, selectedCard.currency)})`,
        type: 'error'
      });
      return;
    }

    setRedeeming(true);
    try {
      const { data } = await api.post('/gift-cards/redeem', {
        code: selectedCard.code,
        amount: amountNum
      });
      setSelectedCard(null);
      setNotice({
        text: `Success! Redeemed ${formatCurrency(amountNum, selectedCard.currency)} from gift card ${selectedCard.code}. Remaining balance: ${formatCurrency(data.balance, data.currency)}`,
        type: 'success'
      });
      loadData();
    } catch (err) {
      setNotice({ text: messageFromError(err), type: 'error' });
    } finally {
      setRedeeming(false);
    }
  }

  // Filtered lists
  const filteredVouchers = useMemo(() => {
    return vouchers.filter(
      (v) =>
        v.code.toLowerCase().includes(voucherSearch.toLowerCase()) ||
        v.description.toLowerCase().includes(voucherSearch.toLowerCase())
    );
  }, [vouchers, voucherSearch]);

  const filteredCards = useMemo(() => {
    return cards.filter((c) =>
      c.code.toLowerCase().includes(cardSearch.toLowerCase())
    );
  }, [cards, cardSearch]);

  const combinedHistory = useMemo(() => {
    const list = [
      ...voucherHistory.map((h) => ({
        id: `v-${h.id}`,
        type: 'VOUCHER',
        code: h.voucherCode,
        amount: formatDiscount(h.discount, h.discountType, h.currency),
        balance: '-',
        date: h.redeemedAt,
        status: h.status || 'SUCCESS'
      })),
      ...giftHistory.map((h) => ({
        id: `g-${h.id}`,
        type: 'GIFT_CARD',
        code: h.giftCardCode,
        amount: formatCurrency(h.amount, h.currency),
        balance: formatCurrency(h.remainingBalance, h.currency),
        date: h.redeemedAt,
        status: 'SUCCESS'
      }))
    ];

    list.sort((a, b) => new Date(b.date) - new Date(a.date));

    return list.filter((item) => {
      const matchSearch = item.code.toLowerCase().includes(historySearch.toLowerCase());
      const matchType =
        historyTypeFilter === 'ALL' ? true : item.type === historyTypeFilter;
      return matchSearch && matchType;
    });
  }, [voucherHistory, giftHistory, historySearch, historyTypeFilter]);

  const totalRedemptionsCount = voucherHistory.length + giftHistory.length;

  return (
    <div className="dashboard">
      {/* Hero Welcome */}
      <div className="hero-row">
        <div className="hero-text">
          <span className="eyebrow">CUSTOMER WALLET</span>
          <h1>Welcome to Your Benefits Portal</h1>
          <p>Discover available promotional vouchers, tap into gift card balances, and track your redemption history.</p>
        </div>

        <div>
          <button className="btn btn-outline btn-sm" onClick={loadData} disabled={loading}>
            🔄 {loading ? 'Refreshing...' : 'Refresh Offers'}
          </button>
        </div>
      </div>

      {/* Stats row */}
      <div className="stats-grid">
        <StatCard
          label="Available Vouchers"
          value={vouchers.length}
          subtext="Active discounts ready to use"
          tone="green"
        />
        <StatCard
          label="Available Gift Cards"
          value={cards.length}
          subtext="Stored value cards with balance"
          tone="gold"
        />
        <StatCard
          label="My Redemptions"
          value={totalRedemptionsCount}
          subtext="Total offers redeemed by you"
          tone="blue"
        />
      </div>

      {/* Notification banner */}
      {notice.text && (
        <div className={`notice ${notice.type}`}>
          <span>{notice.text}</span>
          <button
            type="button"
            className="notice-close"
            onClick={() => setNotice({ text: '', type: 'success' })}
          >
            ×
          </button>
        </div>
      )}

      {/* Navigation tabs */}
      <nav className="tab-bar">
        <button
          type="button"
          className={`tab-btn ${activeTab === 'vouchers' ? 'active' : ''}`}
          onClick={() => setActiveTab('vouchers')}
        >
          🎟️ Available Vouchers ({vouchers.length})
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'giftcards' ? 'active' : ''}`}
          onClick={() => setActiveTab('giftcards')}
        >
          💳 Available Gift Cards ({cards.length})
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'history' ? 'active' : ''}`}
          onClick={() => setActiveTab('history')}
        >
          📜 My Redemption History ({totalRedemptionsCount})
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'rewards' ? 'active' : ''}`}
          onClick={() => setActiveTab('rewards')}
        >
          🎁 Partner Rewards ({rewards.length})
        </button>
      </nav>

      {/* TAB 1: AVAILABLE VOUCHERS */}
      {activeTab === 'vouchers' && (
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2 className="panel-title">Active Promotional Vouchers</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
                Each voucher can be redeemed once per customer account until usage limits are reached.
              </p>
            </div>
            <div className="filter-bar">
              <input
                type="text"
                className="search-input"
                placeholder="Search vouchers..."
                value={voucherSearch}
                onChange={(e) => setVoucherSearch(e.target.value)}
              />
            </div>
          </div>

          <div className="offer-grid">
            {filteredVouchers.map((v) => (
              <article className="offer-card" key={v.id}>
                <div>
                  <div className="offer-header">
                    <span className="code-cell">{v.code}</span>
                    <span className="badge badge-active">Active</span>
                  </div>
                  <div className="offer-amount">{formatDiscount(v.discount, v.discountType, v.currency)} OFF</div>
                  <p className="offer-desc">{v.description}</p>
                </div>

                <div>
                  <div className="offer-meta">
                    <span>Expires: <strong>{v.expiryDate}</strong></span>
                    <span>Used: <strong>{v.currentUsage}/{v.maxUsage}</strong></span>
                  </div>
                  <div className="offer-action" style={{ display: 'flex', gap: '8px' }}>
                    <button
                      type="button"
                      className="btn btn-outline"
                      style={{ flex: 1 }}
                      onClick={() => handleShowQr(v)}
                      disabled={generatingQr}
                    >
                      Show QR
                    </button>
                    <button
                      type="button"
                      className="btn btn-primary"
                      style={{ flex: 1 }}
                      onClick={() => handleRedeemVoucher(v.code)}
                    >
                      Redeem Online
                    </button>
                  </div>
                </div>
              </article>
            ))}

            {filteredVouchers.length === 0 && (
              <div className="empty-state" style={{ gridColumn: '1 / -1' }}>
                <div className="empty-state-icon">🎟️</div>
                <h3>No active vouchers available</h3>
                <p>Check back later for new promotional offers.</p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 2: AVAILABLE GIFT CARDS */}
      {activeTab === 'giftcards' && (
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2 className="panel-title">Active Gift Cards</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
                Stored value cards. You can redeem partial or full amounts against available balances.
              </p>
            </div>
            <div className="filter-bar">
              <input
                type="text"
                className="search-input"
                placeholder="Search gift cards..."
                value={cardSearch}
                onChange={(e) => setCardSearch(e.target.value)}
              />
            </div>
          </div>

          <div className="offer-grid">
            {filteredCards.map((c) => (
              <article className="offer-card gold" key={c.id}>
                <div>
                  <div className="offer-header">
                    <span className="code-cell">{c.code}</span>
                    <span className="badge badge-giftcard">Stored Value</span>
                  </div>
                  <div className="offer-amount">{formatCurrency(c.balance, c.currency)}</div>
                  <p className="offer-desc">Available balance out of original {formatCurrency(c.amount, c.currency)}</p>
                </div>

                <div>
                  <div className="offer-meta">
                    <span>Expires: <strong>{c.expiryDate}</strong></span>
                    <span className="badge badge-active">Funded</span>
                  </div>
                  <div className="offer-action">
                    <button
                      type="button"
                      className="btn btn-gold"
                      style={{ width: '100%' }}
                      onClick={() => openCardRedeemModal(c)}
                    >
                      Redeem Amount →
                    </button>
                  </div>
                </div>
              </article>
            ))}

            {filteredCards.length === 0 && (
              <div className="empty-state" style={{ gridColumn: '1 / -1' }}>
                <div className="empty-state-icon">💳</div>
                <h3>No active gift cards available</h3>
                <p>All gift cards are currently fully redeemed or inactive.</p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 3: MY REDEMPTIONS */}
      {activeTab === 'history' && (
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2 className="panel-title">My Personal Redemption History</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
                Detailed record of all vouchers and gift card amounts you have redeemed.
              </p>
            </div>
            <div className="filter-bar">
              <input
                type="text"
                className="search-input"
                placeholder="Search by code..."
                value={historySearch}
                onChange={(e) => setHistorySearch(e.target.value)}
              />
              <select
                className="filter-select"
                value={historyTypeFilter}
                onChange={(e) => setHistoryTypeFilter(e.target.value)}
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
                  <th>Redeemed Value</th>
                  <th>Remaining Balance</th>
                  <th>Redemption Timestamp</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {combinedHistory.map((h) => (
                  <tr key={h.id}>
                    <td>
                      <span className={`badge ${h.type === 'VOUCHER' ? 'badge-voucher' : 'badge-giftcard'}`}>
                        {h.type === 'VOUCHER' ? '🎟️ Voucher' : '💳 Gift Card'}
                      </span>
                    </td>
                    <td><span className="code-cell">{h.code}</span></td>
                    <td><strong>{h.amount}</strong></td>
                    <td>{h.balance}</td>
                    <td style={{ color: 'var(--text-muted)' }}>
                      {h.date ? new Date(h.date).toLocaleString() : '-'}
                    </td>
                    <td>
                      <span className="badge badge-active">{h.status}</span>
                    </td>
                  </tr>
                ))}

                {combinedHistory.length === 0 && (
                  <tr>
                    <td colSpan="6" className="empty-state">
                      <div className="empty-state-icon">📜</div>
                      <h3>No redemptions found</h3>
                      <p>You haven't redeemed any vouchers or gift cards yet.</p>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 4: PARTNER REWARDS */}
      {activeTab === 'rewards' && (
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2 className="panel-title">My Partner Rewards</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
                Gift cards from external partner brands automatically issued to you.
              </p>
            </div>
          </div>
          <div className="offer-grid">
            {rewards.map((r) => (
              <article className="offer-card" key={r.id}>
                <div>
                  <div className="offer-header">
                    <span className="code-cell">{r.partnerBrand?.brandName}</span>
                    <span className={`badge ${r.active ? 'badge-active' : 'badge-inactive'}`}>
                      {r.active ? 'AVAILABLE' : 'REDEEMED'}
                    </span>
                  </div>
                  <div className="offer-amount">{r.currency} {r.amount}</div>
                  <p className="offer-desc">Integration: {r.partnerBrand?.redemptionMode}</p>
                </div>
                <div>
                  <div className="offer-meta">
                    <span>Expires: <strong>{r.expiryDate}</strong></span>
                  </div>
                  {r.active && (
                    <div className="offer-action" style={{ display: 'flex', gap: '8px' }}>
                      <button
                        type="button"
                        className="btn btn-primary"
                        style={{ flex: 1 }}
                        onClick={() => handleRedeemReward(r.id)}
                        disabled={generatingQr}
                      >
                        Redeem Reward
                      </button>
                    </div>
                  )}
                </div>
              </article>
            ))}
            {rewards.length === 0 && (
              <div className="empty-state" style={{ gridColumn: '1 / -1' }}>
                <div className="empty-state-icon">🎁</div>
                <h3>No rewards yet</h3>
                <p>Complete qualifying purchases to earn external brand rewards.</p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 5: MY PURCHASES */}
      {activeTab === 'purchases' && (
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2 className="panel-title">My Purchases</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
                A record of your in-store purchases and associated rewards.
              </p>
            </div>
          </div>
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Date</th>
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
                    <td colSpan="5" className="empty-state">
                      <div className="empty-state-icon">💰</div>
                      <h3>No purchases yet</h3>
                      <p>Your in-store purchases will appear here.</p>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* REDEEM GIFT CARD MODAL */}
      {selectedCard && (
        <div className="modal-overlay" onClick={() => setSelectedCard(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">Redeem Gift Card: {selectedCard.code}</h3>
              <button
                type="button"
                className="notice-close"
                onClick={() => setSelectedCard(null)}
              >
                ×
              </button>
            </div>

            <form onSubmit={handleRedeemCardSubmit}>
              <div style={{ background: 'var(--gold-light)', padding: '14px', borderRadius: 'var(--radius-sm)', marginBottom: '16px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                  <span style={{ fontSize: '0.85rem', color: 'var(--gold)' }}>Current Balance:</span>
                  <strong style={{ fontSize: '1.2rem', color: 'var(--gold)' }}>{formatCurrency(selectedCard.balance, selectedCard.currency)}</strong>
                </div>
                <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                  Expires on: {selectedCard.expiryDate}
                </div>
              </div>

              <div className="form-group" style={{ marginBottom: '20px' }}>
                <label>Amount to Redeem</label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  max={selectedCard.balance}
                  value={redeemAmount}
                  onChange={(e) => setRedeemAmount(e.target.value)}
                  placeholder="e.g. 25.00"
                  required
                />
                <span className="form-help">
                  Enter any amount up to {formatCurrency(selectedCard.balance, selectedCard.currency)}
                </span>
              </div>

              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-outline"
                  onClick={() => setSelectedCard(null)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-gold"
                  disabled={redeeming}
                >
                  {redeeming ? 'Processing...' : 'Confirm Redemption'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* QR MODAL */}
      {qrModalOpen && (
        <div className="modal-overlay" onClick={() => setQrModalOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ textAlign: 'center', maxWidth: '400px' }}>
            <div className="modal-header">
              <h3 className="modal-title">In-Store QR Redemption</h3>
              <button
                type="button"
                className="notice-close"
                onClick={() => setQrModalOpen(false)}
              >
                ×
              </button>
            </div>
            <div style={{ padding: '24px' }}>
              <p style={{ marginBottom: '16px', color: 'var(--text-muted)' }}>Show this QR code to the merchant to redeem your voucher.</p>
              
              {qrVoucher && (
                <div style={{ background: '#f5f5f5', padding: '16px', borderRadius: '8px', marginBottom: '20px', textAlign: 'left', fontSize: '14px' }}>
                  <h4 style={{ margin: '0 0 8px 0', color: '#1f6b52' }}>{qrVoucher.description || 'Promotional Offer'}</h4>
                  <p style={{ margin: '4px 0' }}><strong>Shop:</strong> {qrVoucher.shopName || 'Platform Wide'}</p>
                  <p style={{ margin: '4px 0' }}><strong>Discount:</strong> {qrVoucher.discountType === 'PERCENTAGE' ? `${qrVoucher.discount}% OFF` : `${qrVoucher.currency || '₹'} ${qrVoucher.discount} OFF`}</p>
                  <p style={{ margin: '4px 0' }}><strong>Expires:</strong> {qrVoucher.expiryDate}</p>
                  <p style={{ margin: '4px 0' }}>
                    <strong>Status:</strong> <span className="badge badge-active">Available</span>
                  </p>
                </div>
              )}

              {qrToken ? (
                <div style={{ background: '#fff', padding: '16px', display: 'inline-block', borderRadius: '12px', border: '1px solid #eaeaea' }}>
                  <QRCodeSVG value={qrToken} size={200} />
                </div>
              ) : (
                <p>Failed to generate QR.</p>
              )}
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-primary"
                onClick={() => setQrModalOpen(false)}
              >
                Done
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
