package edu.rentcar.payment;

public interface CustomerVerifier {
  boolean isAuthorized(String customerId);
}
