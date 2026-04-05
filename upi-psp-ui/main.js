import { ApiService } from './api.js';

let transactions = [];

// --- System Engine ---
document.addEventListener('DOMContentLoaded', async () => {
    console.log('UPI Switch Booting...');
    await refreshData();
    initGlobalListeners();
    initAnimations();
    initBackgroundEngine();
    
    logToConsole('HEALTH: System identity partitions verified.', 'system');
    logToConsole('DEMO: High-Fidelity Simulation Mode is ACTIVE.', 'system');
});

// --- Robust Global Event Delegation ---
function initGlobalListeners() {
    document.body.addEventListener('click', async (e) => {
        // 1. Navigation Switching
        const navItem = e.target.closest('.nav-item');
        if (navItem) {
            e.preventDefault();
            handleViewSwitch(navItem);
            return;
        }

        // 2. Open Modal Triggers
        if (e.target.closest('#btn-send')) {
            openPaymentModal();
            return;
        }

        // 3. Close Modals/Overlays
        if (e.target.closest('.close-modal') || (e.target.classList.contains('modal'))) {
            document.getElementById('payment-modal').style.display = 'none';
            return;
        }
        if (e.target.closest('.close-success')) {
            document.getElementById('success-screen').style.display = 'none';
            await refreshData();
            return;
        }

        // 4. Payment Submission
        if (e.target.id === 'submit-pay') {
            handleTransaction();
            return;
        }

        // 5. Quick Actions
        handleQuickActions(e);
    });
}

function handleViewSwitch(item) {
    const span = item.querySelector('span');
    if (!span) return;
    
    const viewName = span.innerText.trim().toUpperCase();
    const views = {
        'HOME': document.getElementById('home-view'),
        'PAYMENTS': document.getElementById('home-view'),
        'WEALTH': document.getElementById('wealth-view'),
        'PROFILE': document.getElementById('profile-view')
    };

    if (views[viewName]) {
        // Toggle Active UI
        document.querySelectorAll('.nav-item').forEach(ni => ni.classList.remove('active'));
        item.classList.add('active');

        // Toggle Visibility
        Object.keys(views).forEach(v => {
            if (views[v]) views[v].style.display = 'none';
        });
        views[viewName].style.display = 'block';
        
        logToConsole(`UI: Route changed to /${viewName.toLowerCase()}`, 'system');
    }
}

async function handleTransaction() {
    const vpa = document.getElementById('receiver-vpa').value.trim();
    const amountVal = document.getElementById('pay-amount').value;
    const amount = parseFloat(amountVal);

    if (!vpa || isNaN(amount) || amount <= 0) return alert('Please enter valid UPI VPA and Amount');

    const modal = document.getElementById('payment-modal');
    const processingOverlay = document.getElementById('processing-overlay');
    const successScreen = document.getElementById('success-screen');

    modal.style.display = 'none';
    processingOverlay.style.display = 'flex';

    try {
        const txnId = 'T24' + Math.floor(Math.random() * 899999 + 100000);
        
        logToConsole(`GATEWAY: Authorizing Merchant VPA [${vpa}]...`, 'gateway');
        await new Promise(r => setTimeout(r, 1200));
        
        logToConsole(`KAFKA: Transaction event produced. TXID: ${txnId}`, 'kafka');
        await new Promise(r => setTimeout(r, 800));
        
        logToConsole(`LEDGER: Ledger consistency check. Idempotency Key verified.`, 'ledger');
        await new Promise(r => setTimeout(r, 600));
        
        await ApiService.sendPayment({ transactionId: txnId, merchantVpa: vpa, amount: amount });

        // Update Success UI
        document.getElementById('success-txn-id').innerText = `TXID: ${txnId}`;
        document.getElementById('success-amount').innerText = amount.toLocaleString('en-IN', { style: 'currency', currency: 'INR' });

        processingOverlay.style.display = 'none';
        successScreen.style.display = 'flex';
        
        logToConsole(`SYSTEM: Full-stack lifecycle success: ${txnId}`, 'system');
    } catch (err) {
        processingOverlay.style.display = 'none';
        logToConsole(`FATAL: System re-routed call due to exception. See logs.`, 'error');
        alert('Simulation Error: ' + err.message);
    }
}

function handleQuickActions(e) {
    // Scan & Pay
    if (e.target.closest('#btn-scan')) {
        logToConsole('CAMERA: Initializing scanner...', 'system');
        setTimeout(() => {
            document.getElementById('receiver-vpa').value = 'starbucks@upi';
            openPaymentModal();
        }, 500);
    }

    // Add Money
    if (e.target.closest('.add-money-btn')) {
        simulateAddMoney();
    }

    // Reset Simulation
    if (e.target.closest('.reset-app-btn')) {
        if (confirm('Clear simulation storage? This will reset all dummy data.')) {
            localStorage.clear();
            location.reload();
        }
    }
}

function openPaymentModal() {
    const modal = document.getElementById('payment-modal');
    modal.style.display = 'block';
    
    const submitBtn = document.getElementById('submit-pay');
    submitBtn.disabled = false;
    submitBtn.innerHTML = 'Authorize Transaction';
}

async function simulateAddMoney() {
    const amountStr = prompt('Portfolio Demo: Enter amount to top up (INR)', '2000');
    const amount = parseFloat(amountStr);
    if (!isNaN(amount) && amount > 0) {
        logToConsole(`BANK: Secured 3DS handshake for ₹${amount}...`, 'gateway');
        
        let balance = parseFloat(localStorage.getItem('neo_wallet_balance') || '14580.30');
        balance += amount;
        localStorage.setItem('neo_wallet_balance', balance.toFixed(2));
        
        // Save to local transactions
        const localTxns = JSON.parse(localStorage.getItem('neo_local_txns') || '[]');
        localTxns.unshift({ id: Date.now(), title: 'Self Transfer', date: 'Just now', amount: amount, type: 'credit', icon: '🏦', status: 'SUCCESS' });
        localStorage.setItem('neo_local_txns', JSON.stringify(localTxns));
        
        await refreshData();
        logToConsole(`LEDGER: Funds credited to wallet partition.`, 'ledger');
    }
}

// --- Live Simulation Engine ---
function initBackgroundEngine() {
    setInterval(async () => {
        if (Math.random() > 0.95) { // Occasional cashback pulse
            const amount = (Math.random() * 2 + 1).toFixed(2);
            logToConsole(`CASHBACK: Distributed automated reward: ₹${amount}`, 'ledger');
            
            let balance = parseFloat(localStorage.getItem('neo_wallet_balance') || '14580.30');
            balance += parseFloat(amount);
            localStorage.setItem('neo_wallet_balance', balance.toFixed(2));
            
            await refreshData();
        }
    }, 45000);

    setInterval(() => {
        const load = Math.floor(Math.random() * 8000 + 42000);
        logToConsole(`METRIC: Kafka Aggregated Throughput: ${load.toLocaleString()} TPS`, 'system');
    }, 30000);
}

// --- Data & Rendering ---
async function refreshData() {
    try {
        const walletRes = await ApiService.getWalletBalance();
        const balanceEl = document.getElementById('wallet-balance');
        if (balanceEl) {
            balanceEl.innerText = walletRes.balance.toLocaleString('en-IN', { style: 'currency', currency: 'INR' });
        }

        const ledgerTxns = await ApiService.getRecentTransactions();
        transactions = (ledgerTxns && ledgerTxns.length > 0) ? ledgerTxns : [
            { id: 1, title: 'Spotify Premium', date: 'Oct 26, 2:00 PM', amount: -149.00, type: 'debit', icon: '🎵' },
            { id: 2, title: 'Zomato Ltd', date: 'Oct 25, 10:00 PM', amount: -675.00, type: 'debit', icon: '🍕' }
        ];

        renderTransactions();
    } catch (err) {
        console.warn('Refresh state failed:', err);
    }
}

function renderTransactions() {
    const txnList = document.getElementById('txn-list');
    if (!txnList) return;
    txnList.innerHTML = transactions.map(txn => `
        <div class="txn-card">
            <div class="txn-info">
                <div class="txn-icon-box">${txn.icon || '💸'}</div>
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
    if (!logs) return;
    const entry = document.createElement('div');
    entry.className = `log-entry ${type}`;
    const time = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false });
    entry.innerText = `[${time}] ${msg}`;
    logs.prepend(entry);
}

function initAnimations() {
    const cards = document.querySelectorAll('.glow-card, .action-item, .txn-card');
    cards.forEach((card, i) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(15px)';
        card.style.transition = 'all 0.4s ease-out';
        setTimeout(() => {
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, 100 + (i * 30));
    });
}
