package com.bsc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentRecords {

	private Map<String, Integer> paymentRecords;
	private Stack<Payment> paymentsHistory;
	private Map<String, Double> usdExchangeRates;

	public PaymentRecords() {
		paymentRecords = new ConcurrentHashMap<String, Integer>();
		paymentsHistory = new Stack<Payment>();
		usdExchangeRates = new HashMap<String, Double>();
		loadExchangeRates();
	}

	public void putRecord(Payment payment) {
		synchronized (paymentRecords) {
			if (paymentRecords.containsKey(payment.getCurrency())) {
				paymentRecords.put(payment.getCurrency(),
						paymentRecords.get(payment.getCurrency()) + payment.getAmount());
			} else {
				paymentRecords.put(payment.getCurrency(), payment.getAmount());
			}
		}
		paymentsHistory.push(payment);
	}

	private void loadExchangeRates() {
		Properties properties = new Properties();
		InputStream inputStream = null;

		try {
			String propsFilename = "exchange_rates.properties";
			inputStream = PaymentRecords.class.getClassLoader().getResourceAsStream(propsFilename);
			if (inputStream == null) {
				System.err.println("Unable to find file " + propsFilename);
				return;
			}

			properties.load(inputStream);

			Enumeration<?> enumKeys = properties.keys();
			while (enumKeys.hasMoreElements()) {
				String currency = (String) enumKeys.nextElement();
				try {
					Double rate = Double.valueOf(properties.getProperty(currency));
					usdExchangeRates.put(currency, rate);
				} catch (NumberFormatException e) {
					System.err.println(String.format("Exchange rate for curency '%s' is in invalid format: '%s'",
							currency, properties.getProperty(currency)));
				}
			}

		} catch (IOException ex) {
			System.err.println("Problem ocurred while processing properties file.");
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// not important
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		synchronized (paymentRecords) {
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
