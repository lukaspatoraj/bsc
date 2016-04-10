package bsc;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.bsc.Payment;
import com.bsc.PaymentFactory;
import com.bsc.PaymentRecords;
import com.bsc.PaymentTracker;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PaymentTracker.class)
public class PaymentTrackerTest {

	@Test
	public void testProcessLine() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NoSuchFieldException {

		PaymentTracker paymentTracker = new PaymentTracker(new String[0]);

		// mocks
		Payment paymentUsd = new Payment("USD", -123);
		Payment paymentEur = new Payment("EUR", 1234);

		PaymentFactory paymentFactory = mock(PaymentFactory.class);
		when(paymentFactory.makePayment("USD -123")).thenReturn(paymentUsd);
		when(paymentFactory.makePayment("EUR1234")).thenReturn(paymentEur);
		when(paymentFactory.makePayment("usd 1234")).thenReturn(null);

		PaymentRecords paymentRecords = mock(PaymentRecords.class);
		when(paymentRecords.getPaymentRecords()).thenReturn(new ConcurrentHashMap<String, Integer>());

		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				paymentRecords.getPaymentRecords().put(paymentUsd.getCurrency(), paymentUsd.getAmount());
				return null;
			}
		}).when(paymentRecords).putRecord(paymentUsd);

		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				paymentRecords.getPaymentRecords().put(paymentEur.getCurrency(), paymentEur.getAmount());
				return null;
			}
		}).when(paymentRecords).putRecord(paymentEur);

		// private mocked fields
		Field paymentFactoryField = PaymentTracker.class.getDeclaredField("paymentFactory");
		paymentFactoryField.setAccessible(true);
		paymentFactoryField.set(paymentTracker, paymentFactory);

		Field paymentRecordsField = PaymentTracker.class.getDeclaredField("paymentRecords");
		paymentRecordsField.setAccessible(true);
		paymentRecordsField.set(paymentTracker, paymentRecords);

		// private method
		Method processLineMethod = PaymentTracker.class.getDeclaredMethod("processLine", String.class);
		processLineMethod.setAccessible(true);
		processLineMethod.invoke(paymentTracker, "USD -123");
		processLineMethod.invoke(paymentTracker, "EUR1234");
		processLineMethod.invoke(paymentTracker, "usd 1234");

		// tests
		Assert.assertEquals(2, paymentRecords.getPaymentRecords().size());
		Assert.assertEquals(-123, paymentRecords.getPaymentRecords().get(paymentUsd.getCurrency()).intValue());
		Assert.assertEquals(1234, paymentRecords.getPaymentRecords().get(paymentEur.getCurrency()).intValue());
	}

	@Test
	public void testInit()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		PaymentTracker paymentTracker = new PaymentTracker(new String[0]);

		Field paymentFactoryField = PaymentTracker.class.getDeclaredField("paymentFactory");
		paymentFactoryField.setAccessible(true);
		Assert.assertNotNull(paymentFactoryField.get(paymentTracker));

		Field paymentRecords = PaymentTracker.class.getDeclaredField("paymentRecords");
		paymentRecords.setAccessible(true);
		Assert.assertNotNull(paymentRecords.get(paymentTracker));

		Field paymentPrinter = PaymentTracker.class.getDeclaredField("paymentPrinter");
		paymentPrinter.setAccessible(true);
		Assert.assertNotNull(paymentPrinter.get(paymentTracker));
	}

	@Test
	public void testStart() throws Exception {
		PaymentTracker paymentTracker = new PaymentTracker(new String[0]);

		PaymentTracker paymentTrackerSpy = PowerMockito.spy(paymentTracker);
		PowerMockito.doNothing().when(paymentTrackerSpy, "close");

		String userInput = String.format("USD 123%sUSDD 123%sUSD 123%squit", System.lineSeparator(),
				System.lineSeparator(), System.lineSeparator());
		ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes());
		System.setIn(in);

		Method startMethod = PaymentTracker.class.getDeclaredMethod("start");
		startMethod.setAccessible(true);
		startMethod.invoke(paymentTrackerSpy);

		PowerMockito.verifyPrivate(paymentTrackerSpy, Mockito.times(1)).invoke("close");

		PowerMockito.verifyPrivate(paymentTrackerSpy, Mockito.times(3)).invoke("processLine", Mockito.anyString());

		Method closeMethod = PaymentTracker.class.getDeclaredMethod("close");
		closeMethod.setAccessible(true);
		closeMethod.invoke(paymentTrackerSpy);
	}
}
