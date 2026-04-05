import asyncio
import aiohttp
import time
import uuid
import random
import sys

# Configuration
GATEWAY_URL = "http://localhost:8081/api/v1/upi/pay"
CONCURRENT_REQUESTS = 50  # Number of concurrent workers
TOTAL_REQUESTS = 5000     # Total requests to send
TARGET_TPS = 600          # Targeted Transactions Per Second

async def send_payment(session, stats):
    transaction_id = str(uuid.uuid4())
    payload = {
        "transactionId": transaction_id,
        "merchantId": "MERCH_001",
        "customerVpa": f"user{random.randint(1, 1000)}@upi",
        "merchantVpa": "merchant@bank",
        "amount": round(random.uniform(10.0, 1000.0), 2),
        "currency": "INR",
        "remarks": "Performance Test Load",
        "timestamp": time.strftime('%Y-%m-%dT%H:%M:%SZ', time.gmtime())
    }
    
    headers = {
        "X-Transaction-Id": transaction_id,
        "Content-Type": "application/json"
    }

    start_time = time.time()
    try:
        async with session.post(GATEWAY_URL, json=payload, headers=headers) as response:
            latency = (time.time() - start_time) * 1000 # ms
            status = response.status
            stats['counts'][status] = stats['counts'].get(status, 0) + 1
            stats['latencies'].append(latency)
    except Exception as e:
        stats['errors'] += 1
        # print(f"Error: {e}")

async def load_tester():
    print(f"Starting High-Throughput UPI Switch Performance Test...")
    print(f"Target: {TOTAL_REQUESTS} requests at ~{TARGET_TPS} TPS...")
    
    stats = {'counts': {}, 'latencies': [], 'errors': 0}
    start_time = time.time()

    async with aiohttp.ClientSession() as session:
        tasks = []
        for i in range(TOTAL_REQUESTS):
            tasks.append(send_payment(session, stats))
            
            # Simple rate limiting to hit target TPS
            if len(tasks) >= CONCURRENT_REQUESTS:
                await asyncio.gather(*tasks)
                tasks = []
                
                # Check current TPS and sleep if ahead of target
                elapsed = time.time() - start_time
                current_tps = (i + 1) / elapsed
                if current_tps > TARGET_TPS:
                    sleep_time = (i + 1) / TARGET_TPS - elapsed
                    if sleep_time > 0:
                        await asyncio.sleep(sleep_time)

        if tasks:
            await asyncio.gather(*tasks)

    end_time = time.time()
    total_time = end_time - start_time
    avg_latency = sum(stats['latencies']) / len(stats['latencies']) if stats['latencies'] else 0
    tps = TOTAL_REQUESTS / total_time

    print("\n" + "="*40)
    print("UPI SWITCH PERFORMANCE RESULTS")
    print("="*40)
    print(f"Total Requests:   {TOTAL_REQUESTS}")
    print(f"Total Time:       {total_time:.2f} s")
    print(f"Calculated TPS:   {tps:.2f} TPS")
    print(f"Avg Latency:      {avg_latency:.2f} ms")
    print(f"HTTP Status Codes: {stats['counts']}")
    print(f"Total Errors:     {stats['errors']}")
    print("="*40)

if __name__ == "__main__":
    if len(sys.argv) > 1:
        TOTAL_REQUESTS = int(sys.argv[1])
    asyncio.run(load_tester())
