package com.bsc;

public class Payment {

	private String currency;
	private int amount;

	public Payment(String currency, int amount) {
		this.currency = currency;
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return String.format("%s %d", this.currency, this.amount);
	}

}
