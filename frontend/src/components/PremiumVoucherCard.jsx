import React from 'react';

const PremiumVoucherCard = ({ voucher, onAction, actionLabel }) => {
    if (!voucher) return null;

    const isActive = voucher.active !== false;
    const isExpired = new Date(voucher.expiryDate) < new Date();
    
    let statusLabel = 'ACTIVE';
    let statusColor = 'bg-green-500';
    if (!isActive) {
        statusLabel = 'DEACTIVATED';
        statusColor = 'bg-gray-500';
    } else if (isExpired) {
        statusLabel = 'EXPIRED';
        statusColor = 'bg-red-500';
    }

    const usagePercent = voucher.maximumUsage ? (voucher.currentUsage / voucher.maximumUsage) * 100 : 0;

    return (
        <div className="relative overflow-hidden rounded-xl bg-gradient-to-br from-indigo-900 to-purple-800 p-6 text-white shadow-xl transition-transform hover:-translate-y-1 hover:shadow-2xl sm:p-8">
            {/* Ticket Cutouts */}
            <div className="absolute -left-4 top-1/2 h-8 w-8 -translate-y-1/2 rounded-full bg-gray-50 shadow-inner dark:bg-gray-900"></div>
            <div className="absolute -right-4 top-1/2 h-8 w-8 -translate-y-1/2 rounded-full bg-gray-50 shadow-inner dark:bg-gray-900"></div>

            <div className="flex items-center justify-between border-b border-white/20 pb-4">
                <div className="flex items-center space-x-2">
                    <span className="text-2xl">🎟️</span>
                    <h3 className="text-lg font-bold uppercase tracking-wider">{voucher.promotionName || 'SPECIAL OFFER'}</h3>
                </div>
                <div className={`flex items-center rounded-full px-3 py-1 text-xs font-semibold ${statusColor}`}>
                    <span className="mr-1 h-2 w-2 rounded-full bg-white"></span>
                    {statusLabel}
                </div>
            </div>

            <div className="py-6 text-center">
                <div className="text-4xl font-extrabold sm:text-5xl">
                    ₹{voucher.discount} OFF
                </div>
                <div className="mt-2 text-xl font-mono text-indigo-200">
                    {voucher.voucherCode || voucher.code}
                </div>
            </div>

            <div className="grid grid-cols-2 gap-4 text-sm text-indigo-100">
                <div>
                    <span className="block text-indigo-300">Shop</span>
                    <span className="font-semibold">{voucher.shopName || 'Unknown Shop'}</span>
                </div>
                <div>
                    <span className="block text-indigo-300">Scope</span>
                    <span className="font-semibold">{voucher.scope || 'SHOP_ONLY'}</span>
                </div>
                <div>
                    <span className="block text-indigo-300">Min Purchase</span>
                    <span className="font-semibold">₹{voucher.minimumPurchaseAmount || 0}</span>
                </div>
                <div>
                    <span className="block text-indigo-300">Expires</span>
                    <span className="font-semibold">{new Date(voucher.expiryDate).toLocaleDateString()}</span>
                </div>
            </div>

            <div className="mt-6 border-t border-white/20 pt-4">
                <div className="mb-1 flex justify-between text-xs text-indigo-200">
                    <span>Usage: {voucher.currentUsage || 0} / {voucher.maximumUsage || '∞'}</span>
                    <span>{Math.round(usagePercent)}% USED</span>
                </div>
                <div className="h-2 w-full overflow-hidden rounded-full bg-white/10">
                    <div 
                        className="h-full rounded-full bg-gradient-to-r from-pink-500 to-yellow-500"
                        style={{ width: `${Math.min(usagePercent, 100)}%` }}
                    ></div>
                </div>
            </div>

            {onAction && (
                <div className="mt-6 flex justify-end">
                    <button 
                        onClick={() => onAction(voucher)}
                        className="rounded-lg bg-white px-6 py-2 font-bold text-indigo-900 transition-colors hover:bg-indigo-50"
                    >
                        {actionLabel || 'View Details'}
                    </button>
                </div>
            )}
        </div>
    );
};

export default PremiumVoucherCard;
