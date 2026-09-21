import { useEffect, useState } from 'react';
import api, { messageFromError } from '../../services/api';
import Section from '../../components/Section';
import StatCard from '../../components/StatCard';

const emptyVoucher = { code: '', description: '', discount: '', expiryDate: '', maxUsage: '' };
const emptyCard = { code: '', amount: '', expiryDate: '' };

export default function AdminDashboard() {
  const [vouchers, setVouchers] = useState([]);
  const [cards, setCards] = useState([]);
  const [history, setHistory] = useState([]);
  const [giftHistory, setGiftHistory] = useState([]);
  const [voucher, setVoucher] = useState(emptyVoucher);
  const [card, setCard] = useState(emptyCard);
  const [notice, setNotice] = useState('');

  async function load() {
    try {
      const [vouchersResponse, cardsResponse, historyResponse, giftHistoryResponse] = await Promise.all([
        api.get('/vouchers'), api.get('/gift-cards'), api.get('/redemptions'), api.get('/gift-cards/redemptions')
      ]);
      setVouchers(vouchersResponse.data);
      setCards(cardsResponse.data);
      setHistory(historyResponse.data);
      setGiftHistory(giftHistoryResponse.data);
    } catch (error) { setNotice(messageFromError(error)); }
  }

  useEffect(() => { load(); }, []);

  async function createVoucher(event) {
    event.preventDefault();
    try {
      await api.post('/vouchers', { ...voucher, discount: Number(voucher.discount), maxUsage: Number(voucher.maxUsage) });
      setVoucher(emptyVoucher); setNotice('Voucher created.'); load();
    } catch (error) { setNotice(messageFromError(error)); }
  }

  async function createCard(event) {
    event.preventDefault();
    try {
      await api.post('/gift-cards', { ...card, amount: Number(card.amount) });
      setCard(emptyCard); setNotice('Gift card created.'); load();
    } catch (error) { setNotice(messageFromError(error)); }
  }

  async function deactivate(path, label) {
    try { await api.delete(path); setNotice(`${label} deactivated.`); load(); }
    catch (error) { setNotice(messageFromError(error)); }
  }

  async function editVoucher(item) {
    const next = {
      code: window.prompt('Voucher code', item.code),
      description: window.prompt('Description', item.description),
      discount: Number(window.prompt('Discount', item.discount)),
      expiryDate: window.prompt('Expiry date', item.expiryDate),
      maxUsage: Number(window.prompt('Maximum usage', item.maxUsage))
    };
    if (!next.code || !next.description || !next.expiryDate) return;
    try { await api.put(`/vouchers/${item.id}`, next); setNotice('Voucher updated.'); load(); }
    catch (error) { setNotice(messageFromError(error)); }
  }

  return <div className="dashboard">
    <div className="hero-row"><div><span className="eyebrow">ADMIN CONSOLE</span><h1>Manage the value layer.</h1><p>Issue, monitor, and retire customer benefits from one place.</p></div><div className="stats"><StatCard label="Vouchers" value={vouchers.length} /><StatCard label="Gift cards" value={cards.length} tone="gold" /><StatCard label="Redemptions" value={history.length + giftHistory.length} tone="blue" /></div></div>
    {notice && <div className="notice">{notice}</div>}
    <div className="grid-two">
      <Section title="Create voucher" eyebrow="NEW OFFER"><form className="form-grid" onSubmit={createVoucher}>{Object.entries(voucher).map(([key, value]) => <label key={key}>{key.replace(/([A-Z])/g, ' $1')}<input type={key === 'discount' || key === 'maxUsage' ? 'number' : key === 'expiryDate' ? 'date' : 'text'} value={value} onChange={(event) => setVoucher({ ...voucher, [key]: event.target.value })} required /></label>)}<button className="button primary">Create voucher</button></form></Section>
      <Section title="Create gift card" eyebrow="STORED VALUE"><form className="form-grid" onSubmit={createCard}>{Object.entries(card).map(([key, value]) => <label key={key}>{key.replace(/([A-Z])/g, ' $1')}<input type={key === 'amount' ? 'number' : key === 'expiryDate' ? 'date' : 'text'} value={value} onChange={(event) => setCard({ ...card, [key]: event.target.value })} required /></label>)}<button className="button primary">Create gift card</button></form></Section>
    </div>
    <Section title="Voucher inventory" eyebrow="LIVE INVENTORY"><DataTable columns={['Code', 'Discount', 'Usage', 'Expiry', 'Status', '']} rows={vouchers.map((item) => [item.code, item.discount, `${item.currentUsage}/${item.maxUsage}`, item.expiryDate, item.active ? 'Active' : 'Inactive', <><button className="link-button" onClick={() => editVoucher(item)}>Edit</button><button className="link-button danger" onClick={() => deactivate(`/vouchers/${item.id}`, 'Voucher')}>Deactivate</button></>])} /></Section>
    <Section title="Gift card inventory" eyebrow="LIVE INVENTORY"><DataTable columns={['Code', 'Balance', 'Expiry', 'Status', '']} rows={cards.map((item) => [item.code, item.balance, item.expiryDate, item.active ? 'Active' : 'Inactive', <button className="link-button danger" onClick={() => deactivate(`/gift-cards/${item.id}`, 'Gift card')}>Deactivate</button>])} /></Section>
    <Section title="Gift Card Redemption History" eyebrow="AUDIT TRAIL"><DataTable columns={['Type', 'Code', 'Customer', 'Redeemed amount', 'Remaining balance', 'Time']} rows={[...history.map((item) => ['Voucher', item.voucherCode, item.userEmail, item.discount, '-', item.redeemedAt]), ...giftHistory.map((item) => ['Gift card', item.giftCardCode, item.userEmail, item.amount, item.remainingBalance, item.redeemedAt])]} /></Section>
  </div>;
}

function DataTable({ columns, rows }) {
  return <div className="table-wrap"><table><thead><tr>{columns.map((column) => <th key={column}>{column}</th>)}</tr></thead><tbody>{rows.length ? rows.map((row, index) => <tr key={index}>{row.map((cell, cellIndex) => <td key={cellIndex}>{cell}</td>)}</tr>) : <tr><td colSpan={columns.length} className="empty">Nothing to show yet.</td></tr>}</tbody></table></div>;
}
