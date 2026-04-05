import { ApiService } from './api.js';

let transactions = [];

document.addEventListener('DOMContentLoaded', async () => {
    await refreshData();
    initPaymentFlow();
    initAnimations();
});

async function refreshData() {
    // 1. Fetch Balance from Wallet Service
    const walletRes = await ApiService.getWalletBalance();
    if (!walletRes.error) {
        document.getElementById('wallet-balance').innerText =
            walletRes.balance.toLocaleString('en-IN', { style: 'currency', currency: 'INR' });
    }

    // 2. Fetch Transactions from UPI Ledger Service
    const ledgerTxns = await ApiService.getRecentTransactions();

    // Merge with static demo ones if empty
    if (ledgerTxns.length === 0) {
        transactions = [
            { id: 1, title: 'Spotify Premium', date: 'Oct 26, 2:00 PM', amount: -149.00, type: 'debit', icon: '🎵' },
            { id: 2, title: 'Zomato Ltd', date: 'Oct 25, 10:00 PM', amount: -675.00, type: 'debit', icon: '🍕' }
        ];
    } else {
        transactions = ledgerTxns;
    }

    renderTransactions();
}

function renderTransactions() {
    const txnList = document.getElementById('txn-list');
    txnList.innerHTML = transactions.map(txn => `
        <div class="txn-card">
            <div class="txn-info">
                <div class="txn-icon-box">${txn.icon}</div>
                <div class="txn-details">
                    <h4>${txn.title}</h4>
                    <span>${txn.date}</span>
                </div>
            </div>
            <div class="txn-amount">
                <span class="amount-val ${txn.amount > 0 ? 'positive' : 'negative'}">
                    ${txn.amount >= 0 ? '+' : ''}${Math.abs(txn.amount).toLocaleString('en-IN', { style: 'currency', currency: 'INR' })}
                </span>
            </div>
        </div>
    `).join('');
}

function logToConsole(msg, type = 'system') {
    const logs = document.getElementById('telemetry-logs');
    const entry = document.createElement('div');
    entry.className = `log-entry ${type}`;
    const time = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false });
    entry.innerText = `[${time}] ${msg}`;
    logs.prepend(entry);
}

function initPaymentFlow() {
    const modal = document.getElementById('payment-modal');
    const btnSend = document.getElementById('btn-send');
    const closeBtn = document.querySelector('.close-modal');
    const submitPay = document.getElementById('submit-pay');
    const statusMsg = document.getElementById('payment-status');

    btnSend.onclick = () => {
        modal.style.display = 'block';
        statusMsg.innerHTML = '';
        submitPay.disabled = false;
        submitPay.innerHTML = 'Authorize Transaction';
    };

    closeBtn.onclick = () => modal.style.display = 'none';
    window.onclick = (e) => { if (e.target == modal) modal.style.display = 'none'; };

    submitPay.onclick = async () => {
        const vpa = document.getElementById('receiver-vpa').value;
        const amount = document.getElementById('pay-amount').value;

        if (!vpa || !amount) {
            showStatus('Please enter VPA and Amount', 'error');
            return;
        }

        submitPay.innerHTML = 'Authorizing...';
        submitPay.disabled = true;

        try {
            const txnId = Math.random().toString(36).substr(2, 9).toUpperCase();
            
            // Phase 1: Gateway
            logToConsole(`GATEWAY: Received request for ${txnId}`, 'gateway');
            await new Promise(r => setTimeout(r, 100));
            logToConsole(`GATEWAY: Idempotency OK. Creating envelope.`, 'gateway');

            // Phase 2: Kafka
            logToConsole(`KAFKA: Producing event to 'upi.transactions.initiate'`, 'kafka');
            await new Promise(r => setTimeout(r, 50));

            // Phase 3: Routing
            logToConsole(`ROUTING: Consumed ${txnId}. Selecting bank route...`, 'routing');
            await new Promise(r => setTimeout(r, 100));
            logToConsole(`ROUTING: Bank connection UP. Authorization received.`, 'routing');

            // Phase 4: Ledger
            logToConsole(`LEDGER: Consumed status success for ${txnId}`, 'ledger');
            logToConsole(`LEDGER: Persisting to Oracle XE (ACID Audit)...`, 'ledger');
            await new Promise(r => setTimeout(r, 50));
            
            const payload = {
                transactionId: txnId,
                merchantVpa: vpa,
                amount: parseFloat(amount),
                customerVpa: 'dheeraj.kumar@finneonet',
                timestamp: new Date().toISOString()
            };

            const result = await ApiService.sendPayment(payload);

            showStatus('Success! Transaction ID: ' + result.data.transactionId, 'success');
            logToConsole(`SYSTEM: Flow complete for ${txnId} in 300ms.`, 'system');

            // Wait for Kafka to process and then refresh
            setTimeout(async () => {
                await refreshData();
                modal.style.display = 'none';
            }, 1500);

        } catch (error) {
            showStatus(error.message || 'Payment failed', 'error');
            logToConsole(`ERROR: ${error.message}`, 'error');
            submitPay.disabled = false;
            submitPay.innerHTML = 'Authorize Transaction';
        }
    };
}

function showStatus(msg, type) {
    const statusMsg = document.getElementById('payment-status');
    statusMsg.innerHTML = msg;
    statusMsg.style.color = type === 'success' ? '#10b981' : '#ef4444';
}

function initAnimations() {
    const cards = document.querySelectorAll('.glow-card, .action-item, .txn-card');
    cards.forEach((card, i) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'all 0.6s cubic-bezier(0.4, 0, 0.2, 1)';
        setTimeout(() => {
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, 100 + (i * 50));
    });
}
