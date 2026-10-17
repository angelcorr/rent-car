package edu.rentcar.payment;

public interface PaymentGateway {
  PaymentStatus pay(String reservationId, String customerId, double amount, String paymentToken);
}
