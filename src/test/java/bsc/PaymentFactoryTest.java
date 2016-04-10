package bsc;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bsc.Payment;
import com.bsc.PaymentFactory;

public class PaymentFactoryTest {

	private static PaymentFactory paymentFactory;
	private final static String PAYMENT_INPUT_PATTERN = "^([A-Z]{3})(\\s*)((\\+|-)?\\d+)$";

	@BeforeClass
	public static void init() {
		paymentFactory = new PaymentFactory(PAYMENT_INPUT_PATTERN);
	}

	@Test
	public void testOk1() {
		Payment payment = paymentFactory.makePayment("USD 100");
		Assert.assertEquals("USD", payment.getCurrency());
		Assert.assertEquals(100, payment.getAmount());
	}

	@Test
	public void testOk2() {
		Payment payment = paymentFactory.makePayment("SKK-1");
		Assert.assertEquals("SKK", payment.getCurrency());
		Assert.assertEquals(-1, payment.getAmount());
	}

	@Test
	public void testFail1() {
		Payment payment = paymentFactory.makePayment("Skk-1");
		Assert.assertNull(payment);
	}

	@Test
	public void testFail2() {
		Payment payment = paymentFactory.makePayment("EUR -1.5");
		Assert.assertNull(payment);
	}
	
	@Test
	public void testFail3() {
		Payment payment = paymentFactory.makePayment("USDD 100");
		Assert.assertNull(payment);
	}
	
	@Test
	public void testFail4() {
		Payment payment = paymentFactory.makePayment("USDD 100000000000000");
		Assert.assertNull(payment);
	}

}
