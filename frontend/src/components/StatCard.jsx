export default function StatCard({ label, value, subtext, tone = 'green' }) {
  return (
    <div className={`stat-card ${tone}`}>
      <span className="stat-card-label">{label}</span>
      <div className="stat-card-val">{value}</div>
      {subtext && <span className="stat-card-sub">{subtext}</span>}
    </div>
  );
}
