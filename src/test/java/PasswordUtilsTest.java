import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.riders.sharing.utils.PasswordUtils;

public class PasswordUtilsTest {
    private final String password = "qe21E12e%%2#2jmubVan";

    @Test
    public void hashPasswordTest1() {
        //Given
        String hashedPassword1;
        String hashedPassword2;

        //When
        hashedPassword1 = PasswordUtils.hashPassword(password);
        hashedPassword2 = PasswordUtils.hashPassword(password);

        //Then
        Assertions.assertNotEquals(hashedPassword1, hashedPassword2, "Should be not equals");
    }

    @Test
    public void checkPasswordTest() {
        //Given
        String hashedPassword1;
        String hashedPassword2;

        //When
        hashedPassword1 = PasswordUtils.hashPassword(password);
        hashedPassword2 = PasswordUtils.hashPassword(password);

        //Then
        Assertions.assertTrue(PasswordUtils.checkPassword(password, hashedPassword1));
        Assertions.assertTrue(PasswordUtils.checkPassword(password, hashedPassword2));
    }
}
