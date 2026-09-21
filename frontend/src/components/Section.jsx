export default function Section({ title, eyebrow, actions, children }) {
  return <section className="panel"><div className="section-heading"><div><span className="eyebrow">{eyebrow}</span><h2>{title}</h2></div><div className="section-actions">{actions}</div></div>{children}</section>;
}
