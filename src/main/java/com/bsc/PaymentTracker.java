package com.bsc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

public class PaymentTracker {

	private final String COMMAND_QUIT = "quit";
	private final String COMMAND_HISTORY = "history";
	private final String PAYMENT_INPUT_PATTERN = "^([A-Z]{3})(\\s*)((\\+|-)?\\d+)$";

	private PaymentRecords paymentRecords;
	private PaymentFactory paymentFactory;
	private PaymentPrinter paymentPrinter;

	public static void main(String[] args) {
		new PaymentTracker(args).start();
	}

	public PaymentTracker(String[] params) {
		init();
		
		System.out.println("PaymentTracker has started!");
		
		loadFiles(params);

		System.out.println("Insert new payments!");
	}

	private void start() {
		Scanner in = new Scanner(System.in);

		String line = in.nextLine();

		while (!line.toLowerCase().equals(COMMAND_QUIT)) {
			if (line.toLowerCase().equals(COMMAND_HISTORY)) {
				paymentRecords.getPaymentsHistory().stream().forEach(System.out::println);
			} else {
				processLine(line);
			}
			line = in.nextLine();
		}

		in.close();
		
		close();		
	}

	private void processLine(String line) {
		Payment payment = paymentFactory.makePayment(line.trim());

		if (payment != null) {
			paymentRecords.putRecord(payment);
		} else {
			System.err.println(String.format("Payment '%s' was in incorrect format!", line));
		}
	}

	private void loadFiles(String[] fileNames) {
		for (String fileName : fileNames) {
			try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
				
				System.out.println(String.format("File '%s' was read!", fileName));
				
				stream.forEach(this::processLine);

			} catch (NoSuchFileException e) {
				System.err.println(String.format("File '%s' was not found!", fileName));
			} catch (IOException e) {
				System.err.println(String.format("Error ocured while processing file '%s'!", fileName));
			}
		}
	}

	private void init() {
		paymentFactory = new PaymentFactory(PAYMENT_INPUT_PATTERN);
		paymentRecords = new PaymentRecords();
		paymentPrinter = new PaymentPrinter(paymentRecords);
	}

	private void close() {
		paymentPrinter.cancel();
		System.out.println("PaymentTracker was closed!");
		System.exit(0);
	}

}
