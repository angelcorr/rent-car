package edu.rentcar.payment;

import edu.rentcar.data.SampleData;
import edu.rentcar.model.Customer;

public class SampleDataCustomerVerificationService implements CustomerVerificationService {
  @Override
  public boolean isAuthorized(String customerId) {
    for (Customer customer: SampleData.customers()) {
      if (customer.getId().equals(customerId)) {
        return customer.isAuthorized();
      }
    }

    return false;
  }
}
