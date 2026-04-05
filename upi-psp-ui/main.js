import { ApiService } from './api.js';

let transactions = [];

document.addEventListener('DOMContentLoaded', async () => {
    await refreshData();
    initPaymentFlow();
    initQuickActions();
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
            logToConsole(`GATEWAY: Received ${txnId}. Validating X-Transaction-Id and Idempotency...`, 'gateway');
            await new Promise(r => setTimeout(r, 150));
            logToConsole(`GATEWAY: Security Check PASSED. Serializing Protobuf Envelope...`, 'gateway');

            // Phase 2: Kafka
            logToConsole(`KAFKA: Producing event to 'upi.transactions.initiate' [Partition 2]`, 'kafka');
            await new Promise(r => setTimeout(r, 100));
            logToConsole(`KAFKA: ACK received. Offset: 145902. Leader Election stable.`, 'kafka');

            // Phase 3: Routing
            logToConsole(`ROUTING: Consumed ${txnId}. Applying weighted routing logic...`, 'routing');
            await new Promise(r => setTimeout(r, 200));
            logToConsole(`ROUTING: NPCI Auth-Code: 99x0A2 secured. Event Produced to status-topic.`, 'routing');

            // Phase 4: Ledger
            logToConsole(`LEDGER: Received status event for ${txnId}`, 'ledger');
            logToConsole(`LEDGER: Database ACID Sync: Oracle XE (Row-Level Locking active)...`, 'ledger');
            await new Promise(r => setTimeout(r, 100));
            logToConsole(`LEDGER: Balance Cache refreshed (Redis LRU). Audit trail complete.`, 'ledger');
            
            const payload = {
                transactionId: txnId,
                merchantVpa: vpa,
                amount: parseFloat(amount),
                customerVpa: 'dheeraj.kumar@finneonet',
                timestamp: new Date().toISOString()
            };

            const result = await ApiService.sendPayment(payload);

            showStatus('Success! Transaction ID: ' + result.data.transactionId, 'success');
            logToConsole(`SYSTEM: Full Transaction Lifecycle: 550ms (Simulated Portfolio Flow)`, 'system');

            // Wait for simulator to 'persist' and then refresh
            setTimeout(async () => {
                await refreshData();
                modal.style.display = 'none';
            }, 1000);

        } catch (error) {
            showStatus(error.message || 'Payment failed', 'error');
            logToConsole(`ERROR: ${error.message}`, 'error');
            submitPay.disabled = false;
            submitPay.innerHTML = 'Authorize Transaction';
        }
    };
}

function initQuickActions() {
    const btnScan = document.getElementById('btn-scan');
    const btnContacts = document.getElementById('btn-contacts');
    const btnBills = document.getElementById('btn-bills');
    const btnAddMoney = document.querySelector('.add-money-btn');
    const btnSeeAll = document.querySelector('.see-all');

    btnScan.onclick = () => {
        logToConsole('CAMERA: Initializing QR Scanner...', 'system');
        setTimeout(() => {
            logToConsole('SCANNER: Decoding UPI QR [Merchant: Starbucks]...', 'gateway');
            alert('Scan & Pay Simulation: Pointing to starbucks@upi. Enter amount manually in the payment modal.');
            const modal = document.getElementById('payment-modal');
            document.getElementById('receiver-vpa').value = 'starbucks@upi';
            modal.style.display = 'block';
        }, 800);
    };

    btnContacts.onclick = () => {
        logToConsole('CONTACTS: Querying secure P2P contact enclave...', 'system');
        setTimeout(() => {
            logToConsole('CONTACTS: Found 142 linked UPI VPAs. Selecting top result.', 'routing');
            alert('Pay Contacts Simulation: Selecting "Dheeraj Kumar" (dheeraj@upi)');
            const modal = document.getElementById('payment-modal');
            document.getElementById('receiver-vpa').value = 'dheeraj@upi';
            modal.style.display = 'block';
        }, 600);
    };

    btnBills.onclick = () => {
        logToConsole('BBPS: Querying Bharat Bill Pay System [BBPS] for dues...', 'gateway');
        setTimeout(() => {
            logToConsole('BBPS: Unpaid Bill Found (Airtel Fiber - ₹999.00).', 'ledger');
            alert('Pay Bills Simulation: Found Airtel Fiber bill. Loading payment envelope...');
            const modal = document.getElementById('payment-modal');
            document.getElementById('receiver-vpa').value = 'airtel.bbps@upi';
            document.getElementById('pay-amount').value = '999.00';
            modal.style.display = 'block';
        }, 1000);
    };

    btnAddMoney.onclick = () => {
        logToConsole('BANK: Connecting to External NetBanking Gateway...', 'gateway');
        setTimeout(() => {
            const amount = prompt('Add Money Simulation: Enter amount to top up (INR)', '5000');
            if (amount) {
                logToConsole(`BANK: 3DS Auth Success. Credits pending for ₹${amount}.`, 'routing');
                logToConsole(`LEDGER: Persisting credit entry to Oracle XE...`, 'ledger');
                alert(`Top-Up Success: ₹${amount} added via simulated bank gateway.`);
                refreshData();
            }
        }, 500);
    };

    btnSeeAll.onclick = (e) => {
        e.preventDefault();
        logToConsole('LEDGER: Retrieving full 30-day transaction history...', 'ledger');
        alert('Ledger Insight: Displaying your full transaction history...');
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
