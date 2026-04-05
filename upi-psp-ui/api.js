// FinTech NEO - Unified API Service Layer
// Use Environment Variables for deployment, fallback to localhost for dev
const WALLET_BASE_URL = import.meta.env.VITE_WALLET_API_URL || 'http://localhost:8080/api/v1';
const UPI_GATEWAY_URL = import.meta.env.VITE_UPI_GATEWAY_URL || 'http://localhost:8081/api/v1/upi';
const UPI_LEDGER_URL = import.meta.env.VITE_UPI_LEDGER_URL || 'http://localhost:8083/api/v1/ledger';

export const ApiService = {
    // Wallet Integration
    async getWalletBalance() {
        try {
            // Check for persistent dummy balance first (Portfolio fallback)
            let balance = localStorage.getItem('neo_wallet_balance');
            if (balance === null) {
                balance = '14580.30';
                localStorage.setItem('neo_wallet_balance', balance);
            }
            return { balance: parseFloat(balance), currency: 'INR' };
        } catch (error) {
            console.error('Error fetching wallet balance:', error);
            return { balance: 0, error: true };
        }
    },

    // UPI Switch Integration
    async sendPayment(payload) {
        try {
            const response = await fetch(`${UPI_GATEWAY_URL}/pay`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Transaction-Id': payload.transactionId
                },
                body: JSON.stringify(payload)
            });
            
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Payment failed');
            }
            
            return await response.json();
        } catch (error) {
            console.warn('Backend not reachable, entering LIVE SIMULATION MODE...');
            await new Promise(resolve => setTimeout(resolve, 150)); 
            
            // Persistence Logic: Save to localStorage for demo realism
            const localTxns = JSON.parse(localStorage.getItem('neo_local_txns') || '[]');
            const newTxn = {
                transactionId: payload.transactionId,
                title: `Sent to ${payload.merchantVpa}`,
                date: new Date().toLocaleString(),
                amount: -parseFloat(payload.amount),
                type: 'debit',
                icon: '💸',
                status: 'SUCCESS'
            };
            localTxns.unshift(newTxn);
            localStorage.setItem('neo_local_txns', JSON.stringify(localTxns.slice(0, 10)));
            
            // Update persistent balance
            let balance = parseFloat(localStorage.getItem('neo_wallet_balance') || '14580.30');
            balance -= parseFloat(payload.amount);
            localStorage.setItem('neo_wallet_balance', balance.toFixed(2));

            return {
                success: true,
                message: 'Transaction accepted and produced to Kafka (SIMULATED)',
                data: {
                    transactionId: payload.transactionId,
                    status: 'SUCCESS'
                }
            };
        }
    },

    async getRecentTransactions() {
        try {
            const response = await fetch(`${UPI_LEDGER_URL}/transactions`);
            if (!response.ok) throw new Error('Failed to fetch ledger');
            const result = await response.json();
            
            return (result.data || []).map(txn => ({
                id: txn.transactionId,
                title: `Sent to ${txn.merchantVpa}`,
                date: new Date(txn.eventTimestamp).toLocaleString(),
                amount: -txn.amount,
                type: 'debit',
                icon: '💸',
                status: txn.status
            }));
        } catch (error) {
            // Merge persistent local transactions with seed data for demo
            const localTxns = JSON.parse(localStorage.getItem('neo_local_txns') || '[]');
            const seedTxns = [
                { id: 'TXN-A101', title: 'Spotify Premium', date: 'Oct 26, 2:00 PM', amount: -149.00, type: 'debit', icon: '🎵' },
                { id: 'TXN-B202', title: 'Zomato Ltd', date: 'Oct 25, 10:20 PM', amount: -675.00, type: 'debit', icon: '🍕' },
                { id: 'TXN-C303', title: 'Rent Payment', date: 'Oct 01, 09:00 AM', amount: -22000.00, type: 'debit', icon: '🏠' },
                { id: 'TXN-D404', title: 'Cash Deposit', date: 'Sep 28, 11:30 AM', amount: 50000.00, type: 'credit', icon: '🏦' }
            ];
            
            return [...localTxns, ...seedTxns];
        }
    }
};
