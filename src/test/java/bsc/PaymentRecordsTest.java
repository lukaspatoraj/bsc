package bsc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.bsc.Payment;
import com.bsc.PaymentRecords;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PaymentRecords.class)
public class PaymentRecordsTest {

	private PaymentRecords paymentRecordsSpy;

	//cannot do Before because "loadExchangeRates" is tested in one method
	// @Before
	public void init() {
		try {
			PowerMockito.suppress(PowerMockito.method(PaymentRecords.class, "loadExchangeRates"));
			PaymentRecords paymentRecords = new PaymentRecords();
			paymentRecordsSpy = PowerMockito.spy(paymentRecords);
			PowerMockito.doNothing().when(paymentRecordsSpy, "loadExchangeRates");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIsNotEmpty() {
		init();
		Payment payment = new Payment("USD", 123);
		paymentRecordsSpy.putRecord(payment);
		Assert.assertFalse(paymentRecordsSpy.isEmpty());
	}

	@Test
	public void testIsEmpty() {
		init();
		Assert.assertTrue(paymentRecordsSpy.isEmpty());
	}

	@Test
	public void testPaymentRecords() {
		init();
		paymentRecordsSpy.putRecord(new Payment("USD", 123));

		Assert.assertEquals(1, paymentRecordsSpy.getPaymentRecords().size());
		Assert.assertEquals(123, paymentRecordsSpy.getPaymentRecords().get("USD").intValue());

		Assert.assertEquals(1, paymentRecordsSpy.getPaymentsHistory().size());
		Assert.assertEquals("USD", paymentRecordsSpy.getPaymentsHistory().peek().getCurrency());
		Assert.assertEquals(123, paymentRecordsSpy.getPaymentsHistory().peek().getAmount());
	}

	@Test
	public void testPaymentRecords2() {
		init();
		paymentRecordsSpy.putRecord(new Payment("USD", 123));
		paymentRecordsSpy.putRecord(new Payment("EUR", -123));

		Assert.assertEquals(2, paymentRecordsSpy.getPaymentRecords().size());
		Assert.assertEquals(123, paymentRecordsSpy.getPaymentRecords().get("USD").intValue());
		Assert.assertEquals(-123, paymentRecordsSpy.getPaymentRecords().get("EUR").intValue());

		Assert.assertEquals(2, paymentRecordsSpy.getPaymentsHistory().size());
		Assert.assertEquals("EUR", paymentRecordsSpy.getPaymentsHistory().peek().getCurrency());
		Assert.assertEquals(-123, paymentRecordsSpy.getPaymentsHistory().pop().getAmount());
		Assert.assertEquals("USD", paymentRecordsSpy.getPaymentsHistory().peek().getCurrency());
		Assert.assertEquals(123, paymentRecordsSpy.getPaymentsHistory().pop().getAmount());
	}

	@Test
	public void testPaymentRecords3() {
		init();
		paymentRecordsSpy.putRecord(new Payment("USD", 123));
		paymentRecordsSpy.putRecord(new Payment("USD", -10));

		Assert.assertEquals(1, paymentRecordsSpy.getPaymentRecords().size());
		Assert.assertEquals(113, paymentRecordsSpy.getPaymentRecords().get("USD").intValue());

		Assert.assertEquals(2, paymentRecordsSpy.getPaymentsHistory().size());
		Assert.assertEquals("USD", paymentRecordsSpy.getPaymentsHistory().peek().getCurrency());
		Assert.assertEquals(-10, paymentRecordsSpy.getPaymentsHistory().pop().getAmount());
		Assert.assertEquals("USD", paymentRecordsSpy.getPaymentsHistory().peek().getCurrency());
		Assert.assertEquals(123, paymentRecordsSpy.getPaymentsHistory().pop().getAmount());
	}

	@Test
	public void testToString() {
		init();
		Payment payment = new Payment("USD", 123);
		paymentRecordsSpy.putRecord(payment);

		Assert.assertEquals("USD 123", paymentRecordsSpy.toString());
	}

	@Test
	public void testLoadExchangeRates() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		
		PaymentRecords paymentRecords = new PaymentRecords();
		
		//not possible to suppress "loadExchangeRates" in spy and then call again -> test in constructor
//		Method loadExchangeRatesMethod = PaymentRecords.class.getDeclaredMethod("loadExchangeRates");
//		loadExchangeRatesMethod.setAccessible(true);
//		loadExchangeRatesMethod.invoke(paymentRecordsSpy);

		Field usdExchangeRatesField = PaymentRecords.class.getDeclaredField("usdExchangeRates");
		usdExchangeRatesField.setAccessible(true);
		Map<String, Double> usdExchangeRates = (Map<String, Double>) usdExchangeRatesField.get(paymentRecords);

		Assert.assertEquals(2, usdExchangeRates.size());
		Assert.assertEquals(new Double("0.95"), usdExchangeRates.get("EUR"));
		Assert.assertEquals(new Double("28.05"), usdExchangeRates.get("SKK"));
	}

}
