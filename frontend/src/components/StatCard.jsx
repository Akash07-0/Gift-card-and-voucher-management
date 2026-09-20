export default function StatCard({ label, value, tone = 'green' }) {
  return <div className={`stat-card ${tone}`}><span>{label}</span><strong>{value}</strong></div>;
}
