package edu.rentcar.payment;

public class PaymentGatewayProxy implements PaymentGateway {
  private final PaymentGateway realGateway;
  private final CustomerVerificationService verifier;

  public PaymentGatewayProxy(PaymentGateway realGateway, CustomerVerificationService verifier) {
    this.realGateway = realGateway;
    this.verifier = verifier;
  }

  @Override
  public PaymentStatus pay(String reservationId, String customerId, double amount, String paymentToken) {
    boolean authorized;
    try {
      authorized = verifier.isAuthorized(customerId);
    } catch (RuntimeException exception) {
      return PaymentStatus.ERROR;
    }

    if (!authorized) {
      return PaymentStatus.DECLINED;
    }

    return realGateway.pay(reservationId, customerId, amount, paymentToken);

  }
}
