import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;

public interface ScooterTestData {
    static Scooter.Builder aScooter() {
        return Scooter.Builder.getNewBuilderWithId()
                .setScooterType(ScooterType.URBAN)
                .setStatus(ScooterStatus.AVAILABLE)
                .setBatteryLevel(100);
    }
}
