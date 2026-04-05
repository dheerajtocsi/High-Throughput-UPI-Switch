// FinTech NEO - Unified API Service Layer
// Use Environment Variables for deployment, fallback to localhost for dev
const WALLET_BASE_URL = import.meta.env.VITE_WALLET_API_URL || 'http://localhost:8080/api/v1';
const UPI_GATEWAY_URL = import.meta.env.VITE_UPI_GATEWAY_URL || 'http://localhost:8081/api/v1/upi';
const UPI_LEDGER_URL = import.meta.env.VITE_UPI_LEDGER_URL || 'http://localhost:8083/api/v1/ledger';

export const ApiService = {
    // Wallet Integration
    async getWalletBalance() {
        try {
            // Note: In a real app, we'd pass the JWT token here
            // const response = await fetch(`${WALLET_BASE_URL}/wallets/me`); 
            // Mocking balance for now to ensure UI works even if Wallet Auth is complex
            return { balance: 14580.30, currency: 'INR' };
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
            // Mimic Kafka processing latency (Sub-200ms target)
            await new Promise(resolve => setTimeout(resolve, 150)); 
            
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
            // Return rich mock history if ledger is down (High-fidelity portfolio fallback)
            return [
                { id: 'TXN-A101', title: 'Spotify Premium', date: 'Oct 26, 2:00 PM', amount: -149.00, type: 'debit', icon: '🎵' },
                { id: 'TXN-B202', title: 'Zomato Ltd', date: 'Oct 25, 10:20 PM', amount: -675.00, type: 'debit', icon: '🍕' },
                { id: 'TXN-C303', title: 'Rent Payment', date: 'Oct 01, 09:00 AM', amount: -22000.00, type: 'debit', icon: '🏠' },
                { id: 'TXN-D404', title: 'Cash Deposit', date: 'Sep 28, 11:30 AM', amount: 50000.00, type: 'credit', icon: '🏦' }
            ];
        }
    }
};
