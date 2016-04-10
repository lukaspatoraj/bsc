package bsc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bsc.Payment;
import com.bsc.PaymentRecords;

public class PaymentRecordsTest {

	private PaymentRecords paymentRecords;

	@Before
	public void init() {
		paymentRecords = new PaymentRecords();
	}

	@Test
	public void testIsNotEmpty() {
		Payment payment = new Payment("USD", 123);
		paymentRecords.putRecord(payment);
		Assert.assertFalse(paymentRecords.isEmpty());
	}

	@Test
	public void testIsEmpty() {
		Assert.assertTrue(paymentRecords.isEmpty());
	}

	@Test
	public void testPaymentRecords() {
		paymentRecords.putRecord(new Payment("USD", 123));

		Assert.assertEquals(1, paymentRecords.getPaymentRecords().size());
		Assert.assertEquals(123, paymentRecords.getPaymentRecords().get("USD").intValue());

		Assert.assertEquals(1, paymentRecords.getPaymentsHistory().size());
		Assert.assertEquals("USD", paymentRecords.getPaymentsHistory().peek().getCurrency());
		Assert.assertEquals(123, paymentRecords.getPaymentsHistory().peek().getAmount());
	}

	@Test
	public void testPaymentRecords2() {
		paymentRecords.putRecord(new Payment("USD", 123));
		paymentRecords.putRecord(new Payment("EUR", -123));

		Assert.assertEquals(2, paymentRecords.getPaymentRecords().size());
		Assert.assertEquals(123, paymentRecords.getPaymentRecords().get("USD").intValue());
		Assert.assertEquals(-123, paymentRecords.getPaymentRecords().get("EUR").intValue());

		Assert.assertEquals(2, paymentRecords.getPaymentsHistory().size());
		Assert.assertEquals("EUR", paymentRecords.getPaymentsHistory().peek().getCurrency());
		Assert.assertEquals(-123, paymentRecords.getPaymentsHistory().pop().getAmount());
		Assert.assertEquals("USD", paymentRecords.getPaymentsHistory().peek().getCurrency());
		Assert.assertEquals(123, paymentRecords.getPaymentsHistory().pop().getAmount());
	}

	@Test
	public void testPaymentRecords3() {
		paymentRecords.putRecord(new Payment("USD", 123));
		paymentRecords.putRecord(new Payment("USD", -10));

		Assert.assertEquals(1, paymentRecords.getPaymentRecords().size());
		Assert.assertEquals(113, paymentRecords.getPaymentRecords().get("USD").intValue());

		Assert.assertEquals(2, paymentRecords.getPaymentsHistory().size());
		Assert.assertEquals("USD", paymentRecords.getPaymentsHistory().peek().getCurrency());
		Assert.assertEquals(-10, paymentRecords.getPaymentsHistory().pop().getAmount());
		Assert.assertEquals("USD", paymentRecords.getPaymentsHistory().peek().getCurrency());
		Assert.assertEquals(123, paymentRecords.getPaymentsHistory().pop().getAmount());
	}

	@Test
	public void testToString() {
		Payment payment = new Payment("USD", 123);
		paymentRecords.putRecord(payment);

		Assert.assertEquals("USD 123", paymentRecords.toString());
	}

}
