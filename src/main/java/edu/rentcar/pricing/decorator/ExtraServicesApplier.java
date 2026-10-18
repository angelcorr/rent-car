package edu.rentcar.pricing.decorator;
import java.util.List;
import edu.rentcar.pricing.RentService;

public final class ExtraServicesApplier {
    private ExtraServicesApplier() {
    }

    public static RentService applyTo(RentService base, List<RentService> extras) {
        RentService total = base;
        for (RentService extra : extras) {
            total = new ExtraServiceDecorator(total, extra);
        }
        return total;
    }
}
