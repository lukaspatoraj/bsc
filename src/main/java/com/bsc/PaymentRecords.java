package com.bsc;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentRecords {

	private Map<String, Integer> paymentRecords;
	private Stack<Payment> paymentsHistory;
	private Map<String, Double> usdExchangeRates;

	public PaymentRecords() {
		paymentRecords = new ConcurrentHashMap<String, Integer>();
		paymentsHistory = new Stack<Payment>();
		loadExchangeRates();
	}

	public void putRecord(Payment payment) {
		if (paymentRecords.containsKey(payment.getCurrency())) {
			paymentRecords.put(payment.getCurrency(), paymentRecords.get(payment.getCurrency()) + payment.getAmount());
		} else {
			paymentRecords.put(payment.getCurrency(), payment.getAmount());
		}
		paymentsHistory.push(payment);
	}

	private void loadExchangeRates() {
		usdExchangeRates = new HashMap<String, Double>();
		usdExchangeRates.put("EUR", 0.96);
		usdExchangeRates.put("SKK", 28.04);
		usdExchangeRates.put("CZK", 25.88);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String currency : paymentRecords.keySet()) {
			int amount = paymentRecords.get(currency);
			if (amount != 0) {
				sb.append(String.format("%s %d", currency, amount));
				if (usdExchangeRates.containsKey(currency)) {
					sb.append(String.format(" (USD %.2f)", amount / usdExchangeRates.get(currency)));
				}
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString().trim();
	}

	public boolean isEmpty() {
		return paymentRecords.isEmpty();
	}

	public Stack<Payment> getPaymentsHistory() {
		return paymentsHistory;
	}

	public Map<String, Integer> getPaymentRecords() {
		return paymentRecords;
	}

}
