package edu.rentcar.payment;

import com.rentcar.paymentclient.PaymentApiRequest;
import com.rentcar.paymentclient.PaymentApiResponse;

import java.io.IOException;

public interface PaymentApiCaller {
  PaymentApiResponse send(PaymentApiRequest request) throws IOException, InterruptedException;
}
