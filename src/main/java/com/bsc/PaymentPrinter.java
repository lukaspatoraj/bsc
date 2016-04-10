package com.bsc;

import java.util.Timer;
import java.util.TimerTask;

public class PaymentPrinter extends TimerTask {

	private final int UPDATE_TIME = 60000;

	private PaymentRecords actualPaymentStatus;

	public PaymentPrinter(PaymentRecords actualPaymentStatus) {
		this.actualPaymentStatus = actualPaymentStatus;

		Timer timer = new Timer();
		timer.schedule(this, UPDATE_TIME, UPDATE_TIME);
	}

	public void run() {
		if (!actualPaymentStatus.isEmpty()) {
			System.out.println();
			System.out.println("Current status:");
			System.out.println(actualPaymentStatus);
			System.out.println();
		}
	}
}
