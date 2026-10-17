package edu.rentcar.service;

import edu.rentcar.payment.PaymentGateway;
import edu.rentcar.payment.PaymentGatewayProxy;
import edu.rentcar.payment.PaymentStatus;
import edu.rentcar.payment.SampleDataCustomerVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentGatewayProxyTest {

  // Fake gateway: counts how many times it is called
  static class FakeGateway implements PaymentGateway {
    int calls = 0;

    @Override
    public PaymentStatus pay(String reservationId, String customerId, double amount, String token) {
      calls++;
      return PaymentStatus.APPROVED;
    }
  }

  @Test
  void authorizedCustomerReachesGateway() {
    FakeGateway fake = new FakeGateway();
    PaymentGatewayProxy proxy = new PaymentGatewayProxy(fake, id -> true);

    PaymentStatus result = proxy.pay("RES-1", "CUS-1001", 1000, "tok_approved");

    assertEquals(PaymentStatus.APPROVED, result);
    assertEquals(1, fake.calls);
  }

  @Test
  void unauthorizedCustomerNeverReachesGateway() {
    FakeGateway fake = new FakeGateway();
    PaymentGatewayProxy proxy = new PaymentGatewayProxy(fake, id -> false);

    PaymentStatus result = proxy.pay("RES-1", "CUS-1003", 1000, "tok_approved");

    assertEquals(PaymentStatus.DECLINED, result);
    assertEquals(0, fake.calls);          // the API was NOT called
  }

  @Test
  void verificationFailureIsErrorAndGatewayIsNotCalled() {
    FakeGateway fake = new FakeGateway();
    PaymentGatewayProxy proxy = new PaymentGatewayProxy(fake, id -> {
      throw new RuntimeException("verification service down");
    });

    PaymentStatus result = proxy.pay("RES-1", "CUS-1001", 1000, "tok_approved");

    assertEquals(PaymentStatus.ERROR, result);
    assertEquals(0, fake.calls);
  }

  @Test
  void sampleDataVerifierUsesCustomerAuthorization() {
    SampleDataCustomerVerifier verifier = new SampleDataCustomerVerifier();

    assertEquals(true, verifier.isAuthorized("CUS-1001"));
    assertEquals(false, verifier.isAuthorized("CUS-1003"));
    assertEquals(false, verifier.isAuthorized("CUS-9999"));
  }
}
