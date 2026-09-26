import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import PremiumVoucherCard from '../../components/PremiumVoucherCard';
import { Html5QrcodeScanner } from 'html5-qrcode';

// -- INLINE SUB-COMPONENTS FOR MERCHANT TABS -- //

const ShopProfile = () => {
  const [shop, setShop] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    loadShop();
  }, []);

  const loadShop = async () => {
    try {
      const res = await api.get('/merchant/shop');
      setShop(res.data);
    } catch (err) {
      console.error(err);
      // Setup default empty state if not found
      setShop({ name: '', description: '', address: '', phone: '', email: '', website: '', category: '' });
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMessage('');
    try {
      const res = await api.put('/merchant/shop', shop);
      setShop(res.data);
      setMessage('Shop profile updated successfully!');
    } catch (err) {
      setMessage('Error updating profile: ' + (err.response?.data?.message || err.message));
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div>Loading shop profile...</div>;

  return (
    <div className="card" style={{ padding: '24px', background: '#fff', borderRadius: '8px', maxWidth: '600px' }}>
      <h2 style={{ marginTop: 0 }}>Shop Profile</h2>
      <div style={{ marginBottom: '20px' }}>
        <strong>Status: </strong> 
        <span style={{ padding: '4px 8px', borderRadius: '4px', background: shop?.status === 'VERIFIED' ? '#e5efdf' : '#f5e4e4', color: shop?.status === 'VERIFIED' ? '#19352e' : '#a83e34' }}>
          {shop?.status || 'PENDING'}
        </span>
      </div>
      {message && <div style={{ marginBottom: '16px', color: message.includes('Error') ? 'red' : 'green' }}>{message}</div>}
      
      <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <input className="input" placeholder="Shop Name" value={shop.name || ''} onChange={e => setShop({...shop, name: e.target.value})} required />
        <input className="input" placeholder="Category" value={shop.category || ''} onChange={e => setShop({...shop, category: e.target.value})} required />
        <textarea className="input" placeholder="Description" value={shop.description || ''} onChange={e => setShop({...shop, description: e.target.value})} rows="3" />
        <input className="input" placeholder="Address" value={shop.address || ''} onChange={e => setShop({...shop, address: e.target.value})} />
        <input className="input" placeholder="Phone" value={shop.phone || ''} onChange={e => setShop({...shop, phone: e.target.value})} />
        <input className="input" placeholder="Email" value={shop.email || ''} onChange={e => setShop({...shop, email: e.target.value})} />
        <input className="input" placeholder="Website" value={shop.website || ''} onChange={e => setShop({...shop, website: e.target.value})} />
        
        <button type="submit" className="button primary" disabled={saving}>
          {saving ? 'Saving...' : 'Save Profile'}
        </button>
      </form>
    </div>
  );
};


const MyPromotions = ({ onCreateNew }) => {
  const [vouchers, setVouchers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadVouchers();
  }, []);

  const loadVouchers = async () => {
    try {
      const res = await api.get('/merchant/vouchers');
      setVouchers(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const toggleStatus = async (id, currentStatus) => {
    try {
      const endpoint = currentStatus ? 'deactivate' : 'activate';
      await api.put(`/merchant/vouchers/${id}/${endpoint}`);
      loadVouchers();
    } catch (err) {
      alert('Error toggling status');
    }
  };

  if (loading) return <div>Loading promotions...</div>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2 style={{ margin: 0 }}>My Promotions</h2>
        <button className="button primary" onClick={onCreateNew}>+ Create Promotion</button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
        {vouchers.map(v => (
          <div key={v.id} style={{ position: 'relative' }}>
            <PremiumVoucherCard voucher={v} />
            <div style={{ marginTop: '10px', textAlign: 'center' }}>
              <button 
                onClick={() => toggleStatus(v.id, v.active)}
                className="button"
                style={{ background: v.active ? '#f5e4e4' : '#e5efdf', color: v.active ? '#a83e34' : '#1f6b52' }}
              >
                {v.active ? 'Deactivate' : 'Activate'}
              </button>
            </div>
          </div>
        ))}
        {vouchers.length === 0 && <p>No promotions created yet.</p>}
      </div>
    </div>
  );
};


const CreatePromotion = ({ onBack }) => {
  const [form, setForm] = useState({
    promotionName: '', code: '', description: '', discount: 0, minPurchaseAmount: 0, maxDiscount: 0,
    expiryDate: '', maxUsage: 100, scope: 'SHOP_ONLY', termsAndConditions: ''
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      await api.post('/merchant/vouchers', form);
      onBack(); // Return to list
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ display: 'flex', gap: '40px', flexWrap: 'wrap' }}>
      <div className="card" style={{ flex: '1 1 400px', padding: '24px', background: '#fff', borderRadius: '8px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
          <h2 style={{ margin: 0 }}>Create Promotion</h2>
          <button className="button" onClick={onBack}>Cancel</button>
        </div>
        
        {error && <div style={{ color: 'red', marginBottom: '16px' }}>{error}</div>}

        <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <input className="input" placeholder="Promotion Name (e.g. SPECIAL OFFER)" value={form.promotionName} onChange={e => setForm({...form, promotionName: e.target.value})} required />
          <input className="input" placeholder="Voucher Code (e.g. SAVE100)" value={form.code} onChange={e => setForm({...form, code: e.target.value.toUpperCase()})} required />
          <input className="input" placeholder="Description" value={form.description} onChange={e => setForm({...form, description: e.target.value})} required />
          
          <div style={{ display: 'flex', gap: '16px' }}>
            <input type="number" className="input" placeholder="Discount Amount (₹)" value={form.discount || ''} onChange={e => setForm({...form, discount: Number(e.target.value)})} required min="1" />
            <input type="number" className="input" placeholder="Min Purchase (₹)" value={form.minPurchaseAmount || ''} onChange={e => setForm({...form, minPurchaseAmount: Number(e.target.value)})} min="0" />
          </div>

          <div style={{ display: 'flex', gap: '16px' }}>
            <input type="date" className="input" value={form.expiryDate} onChange={e => setForm({...form, expiryDate: e.target.value})} required />
            <input type="number" className="input" placeholder="Max Usage Limit" value={form.maxUsage || ''} onChange={e => setForm({...form, maxUsage: Number(e.target.value)})} min="1" required />
          </div>

          <select className="input" value={form.scope} onChange={e => setForm({...form, scope: e.target.value})}>
            <option value="SHOP_ONLY">Shop Only (Default)</option>
            <option value="PARTNER_NETWORK">Partner Network</option>
            <option value="PLATFORM_WIDE">Platform Wide</option>
          </select>

          <textarea className="input" placeholder="Terms & Conditions" value={form.termsAndConditions} onChange={e => setForm({...form, termsAndConditions: e.target.value})} rows="2" />
          
          <button type="submit" className="button primary" disabled={saving}>
            {saving ? 'Creating...' : 'Create Voucher'}
          </button>
        </form>
      </div>
      
      <div style={{ flex: '1 1 350px' }}>
        <h3 style={{ marginTop: 0, marginBottom: '20px' }}>Live Preview</h3>
        <PremiumVoucherCard voucher={{ ...form, active: true, shopName: 'Your Shop' }} />
      </div>
    </div>
  );
};


const RewardRules = ({ onCreateNew }) => {
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadRules();
  }, []);

  const loadRules = async () => {
    try {
      const res = await api.get('/merchant/reward-rules');
      setRules(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const toggleStatus = async (id, currentStatus) => {
    try {
      const endpoint = currentStatus ? 'deactivate' : 'activate';
      await api.put(`/merchant/reward-rules/${id}/${endpoint}`);
      loadRules();
    } catch (err) {
      alert('Error toggling rule status');
    }
  };

  if (loading) return <div>Loading rules...</div>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2 style={{ margin: 0 }}>Reward Rules</h2>
        <button className="button primary" onClick={onCreateNew}>+ Create Rule</button>
      </div>
      <div style={{ background: '#fff', padding: '16px', borderRadius: '8px' }}>
        <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid #eee' }}>
              <th style={{ padding: '8px' }}>Min Purchase</th>
              <th>Brand</th>
              <th>Type</th>
              <th>Amount</th>
              <th>Status</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {rules.map(r => (
              <tr key={r.id} style={{ borderBottom: '1px solid #f9f9f9' }}>
                <td style={{ padding: '8px' }}>{r.currency} {r.minimumPurchaseAmount}</td>
                <td>{r.rewardBrand?.brandName}</td>
                <td>{r.rewardType}</td>
                <td>{r.currency} {r.rewardAmount}</td>
                <td>
                  <span style={{ color: r.active ? 'green' : 'red' }}>{r.active ? 'Active' : 'Inactive'}</span>
                </td>
                <td style={{ textAlign: 'right' }}>
                  <button className="button" style={{ fontSize: '12px', padding: '4px 8px' }} onClick={() => toggleStatus(r.id, r.active)}>
                    {r.active ? 'Deactivate' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
            {rules.length === 0 && <tr><td colSpan="6" style={{ padding: '16px', textAlign: 'center' }}>No reward rules found.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
};

const CreateRewardRule = ({ onBack }) => {
  const [brands, setBrands] = useState([]);
  const [form, setForm] = useState({ minimumPurchaseAmount: '', rewardBrand: { id: '' }, rewardType: 'GIFT_CARD', rewardAmount: '', currency: 'INR' });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/admin/partner-brands').then(res => {
      setBrands(res.data.filter(b => b.active));
      if (res.data.length > 0) setForm(f => ({ ...f, rewardBrand: { id: res.data[0].id }, currency: res.data[0].currency }));
    }).catch(() => setError('Failed to load brands'));
  }, []);

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await api.post('/merchant/reward-rules', {
        ...form,
        minimumPurchaseAmount: Number(form.minimumPurchaseAmount),
        rewardAmount: Number(form.rewardAmount)
      });
      onBack();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card" style={{ padding: '24px', background: '#fff', borderRadius: '8px', maxWidth: '500px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
        <h2 style={{ margin: 0 }}>Create Reward Rule</h2>
        <button className="button" onClick={onBack}>Cancel</button>
      </div>
      {error && <div style={{ color: 'red', marginBottom: '16px' }}>{error}</div>}
      <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <input type="number" className="input" placeholder="Min Purchase Amount" value={form.minimumPurchaseAmount} onChange={e => setForm({...form, minimumPurchaseAmount: e.target.value})} required min="1" />
        <select className="input" value={form.rewardBrand.id} onChange={e => {
          const selectedBrand = brands.find(b => b.id === Number(e.target.value));
          setForm({...form, rewardBrand: { id: Number(e.target.value) }, currency: selectedBrand?.currency || 'INR'});
        }} required>
          {brands.map(b => <option key={b.id} value={b.id}>{b.brandName}</option>)}
        </select>
        <select className="input" value={form.rewardType} onChange={e => setForm({...form, rewardType: e.target.value})} required>
          <option value="GIFT_CARD">Gift Card</option>
        </select>
        <input type="number" className="input" placeholder="Reward Amount" value={form.rewardAmount} onChange={e => setForm({...form, rewardAmount: e.target.value})} required min="1" />
        <button type="submit" className="button primary" disabled={saving}>{saving ? 'Saving...' : 'Save Rule'}</button>
      </form>
    </div>
  );
};


const VerifyAndRedeem = () => {
  const [step, setStep] = useState(1); // 1: Verify, 2: OTP, 3: Success
  const [code, setCode] = useState('');
  const [customerEmail, setCustomerEmail] = useState('');
  const [purchaseAmount, setPurchaseAmount] = useState('');
  
  const [verification, setVerification] = useState(null);
  const [otp, setOtp] = useState('');
  const [receipt, setReceipt] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [scanning, setScanning] = useState(false);

  useEffect(() => {
    if (scanning) {
      const scanner = new Html5QrcodeScanner('qr-reader', { fps: 10, qrbox: { width: 250, height: 250 } }, false);
      scanner.render(
        (decodedText) => {
          scanner.clear();
          setScanning(false);
          handleQrScanSuccess(decodedText);
        },
        () => {}
      );
      return () => {
        scanner.clear().catch(() => {});
      };
    }
  }, [scanning]);

  const handleQrScanSuccess = async (qrToken) => {
    setLoading(true);
    setError('');
    try {
      const res = await api.post('/merchant/redemptions/scan-qr', { qrToken });
      setVerification(res.data);
      setCode(res.data.voucherCode || '');
      setStep(2);
      // Backend automatically finds customerId internally during OTP request if needed, or we just pass a placeholder
      await api.post(`/merchant/redemptions/request-otp?voucherCode=${res.data.voucherCode}&customerId=1`);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  };

  // Step 1: Pre-Verify
  const handleVerify = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      // Assuming customerId is resolved on backend via email in real flow, or we pass ID. 
      // For this simplified UI, we send mock customerId 1, or prompt for exact ID if known.
      const res = await api.post('/merchant/redemptions/verify', {
        voucherCode: code,
        customerId: 1, // hardcoded for demo frontend to avoid looking up user IDs manually
        purchaseAmount: Number(purchaseAmount)
      });
      setVerification(res.data);
      setStep(2);
      
      // Auto-trigger OTP request
      await api.post(`/merchant/redemptions/request-otp?voucherCode=${code}&customerId=1`);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  };

  // Step 2: Final OTP Submit
  const handleOtpSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const res = await api.post('/merchant/redemptions/verify-otp', {
        voucherCode: code,
        customerId: 1,
        purchaseAmount: Number(purchaseAmount),
        otp: otp
      });
      setReceipt(res.data);
      setStep(3);
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid OTP');
    } finally {
      setLoading(false);
    }
  };

  const reset = () => {
    setStep(1); setCode(''); setPurchaseAmount(''); setVerification(null); setOtp(''); setReceipt(null); setError('');
  };

  return (
    <div className="card" style={{ padding: '32px', background: '#fff', borderRadius: '8px', maxWidth: '500px', margin: '0 auto' }}>
      
      {step === 1 && (
        <form onSubmit={handleVerify} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <h2 style={{ marginTop: 0, textAlign: 'center' }}>Verify Voucher</h2>
          
          <button type="button" className="button" style={{ background: '#1f6b52', color: '#fff' }} onClick={() => setScanning(!scanning)}>
            {scanning ? 'Cancel QR Scan' : '📷 Scan QR Code'}
          </button>
          
          {scanning && <div id="qr-reader" style={{ width: '100%', maxWidth: '400px', margin: '0 auto' }}></div>}

          {error && <div style={{ color: 'red', textAlign: 'center' }}>{error}</div>}
          <div style={{ textAlign: 'center', color: '#666' }}>OR Enter Manually</div>
          <input className="input" placeholder="Voucher Code" value={code} onChange={e => setCode(e.target.value.toUpperCase())} required={!scanning} disabled={scanning} />
          <input type="number" className="input" placeholder="Total Purchase Amount (₹)" value={purchaseAmount} onChange={e => setPurchaseAmount(e.target.value)} required min="1" disabled={scanning} />
          <button type="submit" className="button primary" disabled={loading || scanning}>
            {loading ? 'Verifying...' : 'Verify'}
          </button>
        </form>
      )}

      {step === 2 && verification && (
        <form onSubmit={handleOtpSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <h2 style={{ marginTop: 0, textAlign: 'center', color: '#1f6b52' }}>Pre-Verification Success</h2>
          <div style={{ background: '#f5f5f5', padding: '16px', borderRadius: '8px', fontSize: '14px' }}>
            <p><strong>Voucher:</strong> {verification.voucherCode}</p>
            <p><strong>Discount:</strong> ₹{verification.discount}</p>
            <p><strong>Min Purchase:</strong> ₹{verification.minimumPurchaseAmount}</p>
            <p style={{ color: 'green', marginTop: '10px' }}>✓ OTP sent securely to customer.</p>
          </div>
          {error && <div style={{ color: 'red', textAlign: 'center' }}>{error}</div>}
          <input className="input" placeholder="Enter 6-digit OTP" value={otp} onChange={e => setOtp(e.target.value)} required maxLength={6} style={{ textAlign: 'center', letterSpacing: '8px', fontSize: '20px' }} />
          <button type="submit" className="button primary" disabled={loading}>
            {loading ? 'Redeeming...' : 'Verify OTP & Complete Redemption'}
          </button>
          <button type="button" className="button" onClick={reset}>Cancel</button>
        </form>
      )}

      {step === 3 && receipt && (
        <div style={{ textAlign: 'center' }}>
          <div style={{ fontSize: '48px', marginBottom: '16px' }}>✅</div>
          <h2 style={{ marginTop: 0, color: '#1f6b52' }}>Redemption Successful!</h2>
          <div style={{ textAlign: 'left', background: '#f5f5f5', padding: '16px', borderRadius: '8px', fontSize: '14px', marginBottom: '20px' }}>
            <p><strong>TXN ID:</strong> {receipt.transactionId}</p>
            <p><strong>Voucher:</strong> {receipt.voucherCode}</p>
            <p><strong>Customer:</strong> {receipt.customerName}</p>
            <hr style={{ margin: '10px 0', border: 'none', borderTop: '1px dashed #ccc' }} />
            <p><strong>Original Amount:</strong> ₹{receipt.originalAmount}</p>
            <p style={{ color: 'red' }}><strong>Discount Applied:</strong> -₹{receipt.discountApplied}</p>
            <p style={{ fontSize: '18px', fontWeight: 'bold' }}><strong>Final Payable:</strong> ₹{receipt.finalAmount}</p>
          </div>
          <button onClick={reset} className="button primary">Verify Another Voucher</button>
        </div>
      )}
    </div>
  );
};


// -- MAIN DASHBOARD COMPONENT -- //

export default function MerchantDashboard() {
  const [activeTab, setActiveTab] = useState('analytics');
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (activeTab === 'analytics') loadAnalytics();
  }, [activeTab]);

  const loadAnalytics = async () => {
    try {
      setLoading(true);
      const res = await api.get('/merchant/analytics');
      setAnalytics(res.data);
    } catch (error) {
      console.error('Failed to load analytics', error);
    } finally {
      setLoading(false);
    }
  };

  const renderContent = () => {
    switch (activeTab) {
      case 'analytics':
        if (loading) return <div>Loading analytics...</div>;
        if (!analytics) return <div>Failed to load data</div>;
        return (
          <div className="dashboard-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
            <div className="card" style={{ padding: '20px', background: '#fff', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <h3 style={{ margin: '0 0 10px', fontSize: '14px', color: '#666' }}>Total Vouchers</h3>
              <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold' }}>{analytics.totalVouchers}</p>
            </div>
            <div className="card" style={{ padding: '20px', background: '#fff', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <h3 style={{ margin: '0 0 10px', fontSize: '14px', color: '#666' }}>Active Vouchers</h3>
              <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold' }}>{analytics.activeVouchers}</p>
            </div>
            <div className="card" style={{ padding: '20px', background: '#fff', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <h3 style={{ margin: '0 0 10px', fontSize: '14px', color: '#666' }}>Redeemed (Uses)</h3>
              <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold' }}>{analytics.redeemedVouchers}</p>
            </div>
            <div className="card" style={{ padding: '20px', background: '#fff', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <h3 style={{ margin: '0 0 10px', fontSize: '14px', color: '#666' }}>Expired</h3>
              <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold' }}>{analytics.expiredVouchers}</p>
            </div>
            <div className="card" style={{ padding: '20px', background: '#fff', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <h3 style={{ margin: '0 0 10px', fontSize: '14px', color: '#666' }}>Total Purchases</h3>
              <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold' }}>{analytics.totalPurchases}</p>
            </div>
            <div className="card" style={{ padding: '20px', background: '#fff', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <h3 style={{ margin: '0 0 10px', fontSize: '14px', color: '#666' }}>Discount Given</h3>
              <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold' }}>₹{analytics.totalDiscountGiven}</p>
            </div>
          </div>
        );
      case 'shop':
        return <ShopProfile />;
      case 'promotions':
        return <MyPromotions onCreateNew={() => setActiveTab('create_promotion')} />;
      case 'create_promotion':
        return <CreatePromotion onBack={() => setActiveTab('promotions')} />;
      case 'rules':
        return <RewardRules onCreateNew={() => setActiveTab('create_rule')} />;
      case 'create_rule':
        return <CreateRewardRule onBack={() => setActiveTab('rules')} />;
      case 'verify':
        return <VerifyAndRedeem />;
      default:
        return (
          <div style={{ padding: '40px', textAlign: 'center', color: '#666', background: '#fff', borderRadius: '8px' }}>
            <h2 style={{ margin: '0 0 10px' }}>{activeTab.toUpperCase()}</h2>
            <p>This module is under construction.</p>
          </div>
        );
    }
  };

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px' }}>
      <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap' }}>
        
        {/* Sidebar */}
        <div style={{ flex: '1 1 250px', background: '#fff', borderRadius: '8px', padding: '20px', boxShadow: '0 2px 8px rgba(0,0,0,0.05)', alignSelf: 'flex-start' }}>
          <h2 style={{ fontSize: '18px', marginBottom: '20px', borderBottom: '1px solid #eee', paddingBottom: '10px' }}>Merchant Menu</h2>
          <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {['analytics', 'shop', 'promotions', 'rules', 'verify'].map(tab => (
              <li key={tab}>
                <button 
                  onClick={() => setActiveTab(tab)}
                  style={{ width: '100%', padding: '10px', textAlign: 'left', background: activeTab === tab || (activeTab === 'create_promotion' && tab === 'promotions') || (activeTab === 'create_rule' && tab === 'rules') ? '#1f6b52' : 'transparent', color: activeTab === tab || (activeTab === 'create_promotion' && tab === 'promotions') || (activeTab === 'create_rule' && tab === 'rules') ? '#fff' : '#333', border: 'none', borderRadius: '4px', cursor: 'pointer', textTransform: 'capitalize' }}
                >
                  {tab === 'verify' ? 'Verify & Redeem Voucher' : tab.replace('_', ' ')}
                </button>
              </li>
            ))}
          </ul>
        </div>

        {/* Main Content Area */}
        <div style={{ flex: '3 1 700px' }}>
          {renderContent()}
        </div>
        
      </div>
    </div>
  );
}
