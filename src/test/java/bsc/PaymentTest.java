package bsc;

import org.junit.Assert;
import org.junit.Test;

import com.bsc.Payment;

public class PaymentTest {

	@Test
	public void test() {
		Payment payment = new Payment("USD", 123);
		Assert.assertEquals("USD 123", payment.toString());
	}

}
