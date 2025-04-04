import org.riders.sharing.model.Scooter.Builder;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;

import static org.riders.sharing.model.Scooter.Builder.*;

public interface ScooterTestData {
    static Builder aScooter() {
        return scooter()
            .type(ScooterType.URBAN)
            .status(ScooterStatus.AVAILABLE)
            .batteryLevel(100);
    }
}
