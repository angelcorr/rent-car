package edu.rentcar.payment;

public interface CustomerVerificationService {
  boolean isAuthorized(String customerId);
}
