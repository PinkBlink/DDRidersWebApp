import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;

public interface ScooterTestData {
    static Scooter.Builder aScooter() {
        return Scooter.Builder.scooter()
                .type(ScooterType.URBAN)
                .status(ScooterStatus.AVAILABLE)
                .batteryLevel(100);
    }
}
