package com.bsc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentFactory {

	private Pattern pattern;

	public PaymentFactory(String paymentInputPattern) {
		pattern = Pattern.compile(paymentInputPattern);
	}

	public Payment makePayment(String line) {
		Matcher matcher = pattern.matcher(line.trim());

		try {
			if (matcher.find()) {
				String currency = matcher.group(1);
				int amount = Integer.parseInt(matcher.group(3));

				return new Payment(currency, amount);
			}
		} catch (NumberFormatException e) {
			System.out.println("Amount is in invalid range.");
		}
		return null;
	}

}
