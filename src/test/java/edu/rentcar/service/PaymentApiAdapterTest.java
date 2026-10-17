package edu.rentcar.service;

import com.rentcar.paymentclient.PaymentApiResponse;
import edu.rentcar.payment.PaymentApiAdapter;
import edu.rentcar.payment.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentApiAdapterTest {
  @Test
  void approvedResponseIsApproved() {
    PaymentApiAdapter adapter =
        new PaymentApiAdapter(request -> new PaymentApiResponse(200, "APPROVED"));

    assertEquals(PaymentStatus.APPROVED, adapter.pay("RES-1", "CUS-1", 1000, "tok_approved"));
  }

  @Test
  void notApprovedResponseIsDeclined() {
    PaymentApiAdapter adapter =
        new PaymentApiAdapter(request -> new PaymentApiResponse(200, "NOT_APPROVED"));

    assertEquals(PaymentStatus.DECLINED, adapter.pay("RES-1", "CUS-1", 1000, "tok_declined"));
  }

  @Test
  void httpErrorIsError() {
    PaymentApiAdapter adapter =
        new PaymentApiAdapter(request -> new PaymentApiResponse(500, "Internal error"));

    assertEquals(PaymentStatus.ERROR, adapter.pay("RES-1", "CUS-1", 1000, "tok_api_error"));
  }

  @Test
  void connectionFailureIsError() {
    PaymentApiAdapter adapter = new PaymentApiAdapter(request -> {
      throw new IOException("API not available");
    });

    assertEquals(PaymentStatus.ERROR, adapter.pay("RES-1", "CUS-1", 1000, "tok_approved"));
  }
}
