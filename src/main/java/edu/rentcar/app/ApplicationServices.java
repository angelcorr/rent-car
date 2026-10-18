package edu.rentcar.app;

import com.rentcar.paymentclient.PaymentApiClient;
import edu.rentcar.facade.RentalFacade;
import edu.rentcar.payment.PaymentApiAdapter;
import edu.rentcar.payment.PaymentGateway;
import edu.rentcar.payment.PaymentGatewayProxy;
import edu.rentcar.payment.SampleDataCustomerVerificationService;
import edu.rentcar.repository.VehicleRepository;
import edu.rentcar.service.ReservationService;
import edu.rentcar.service.VehicleService;

/**
 * ApplicationServices es un Service Locator: los controllers consultan una clase global para obtener los servicios.
 */
public final class ApplicationServices {
    private static final VehicleService VEHICLE_SERVICE = new VehicleService(new VehicleRepository());
    private static final ReservationService RESERVATION_SERVICE = new ReservationService();
    private static final PaymentApiClient PAYMENT_API_CLIENT = new PaymentApiClient("http://localhost:8080");

    private static final PaymentGateway PAYMENT_GATEWAY = new PaymentGatewayProxy(
        new PaymentApiAdapter(PAYMENT_API_CLIENT::pay),
        new SampleDataCustomerVerificationService());

    private static final RentalFacade RENTAL_FACADE = new RentalFacade(VEHICLE_SERVICE, RESERVATION_SERVICE, PAYMENT_GATEWAY);

    private ApplicationServices() {
    }

    public static VehicleService vehicleService() {
        return VEHICLE_SERVICE;
    }
    public static ReservationService reservationService() {
        return RESERVATION_SERVICE;
    }
    public static RentalFacade rentalFacade() {
        return RENTAL_FACADE;
    }
}
