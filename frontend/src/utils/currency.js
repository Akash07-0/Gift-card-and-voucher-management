export const CURRENCIES = [
  { code: 'INR', symbol: '₹', label: 'INR (₹)' },
  { code: 'USD', symbol: '$', label: 'USD ($)' },
  { code: 'EUR', symbol: '€', label: 'EUR (€)' },
  { code: 'GBP', symbol: '£', label: 'GBP (£)' },
  { code: 'AED', symbol: 'د.إ', label: 'AED (د.إ)' },
  { code: 'SGD', symbol: 'S$', label: 'SGD (S$)' },
  { code: 'AUD', symbol: 'A$', label: 'AUD (A$)' }
];

export function formatCurrency(amount, currencyCode = 'INR') {
  if (amount == null || amount === '') return '-';
  try {
    return new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: currencyCode || 'INR',
    }).format(Number(amount));
  } catch (e) {
    return `${currencyCode || 'INR'} ${amount}`;
  }
}

export function formatDiscount(amount, type, currencyCode = 'INR') {
  if (amount == null || amount === '') return '-';
  if (type === 'PERCENTAGE') {
    return `${amount}%`;
  }
  return formatCurrency(amount, currencyCode);
}
