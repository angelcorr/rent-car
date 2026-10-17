package edu.rentcar.payment;

import com.rentcar.paymentclient.PaymentApiRequest;
import com.rentcar.paymentclient.PaymentApiResponse;

import java.io.IOException;

public class PaymentApiAdapter implements PaymentGateway {
  private final PaymentApiCaller caller;

  public PaymentApiAdapter(PaymentApiCaller caller) {
    this.caller = caller;
  }

  @Override
  public PaymentStatus pay(String reservationId, String customerId, double amount, String paymentToken) {
    try {
      PaymentApiRequest request = new PaymentApiRequest(reservationId, customerId, amount, paymentToken);
      PaymentApiResponse response = caller.send(request);

      if (response.isApproved()) {
        return PaymentStatus.APPROVED;
      }
      if (response.isDeclined()) {
        return PaymentStatus.DECLINED;
      }
      return PaymentStatus.ERROR;

    } catch (IOException exception) {
      return PaymentStatus.ERROR;
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      return PaymentStatus.ERROR;
    }
  }
}
