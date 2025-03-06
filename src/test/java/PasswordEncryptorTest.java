import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.riders.sharing.utils.PasswordEncryptor;

public class PasswordEncryptorTest {
    private final String password = "qe21E12e%%2#2jmubVan";

    @Test
    public void hashPasswordTest1() {
        //Given
        String hashedPassword1;
        String hashedPassword2;

        //When
        hashedPassword1 = PasswordEncryptor.hashPassword(password);
        hashedPassword2 = PasswordEncryptor.hashPassword(password);

        //Then
        Assertions.assertEquals(hashedPassword1, hashedPassword2, "Should be equals");
    }

    @Test
    public void checkPasswordTest() {
        //Given
        String hashedPassword1;
        String hashedPassword2;

        //When
        hashedPassword1 = PasswordEncryptor.hashPassword(password);
        hashedPassword2 = PasswordEncryptor.hashPassword(password);

        //Then
        Assertions.assertEquals(PasswordEncryptor.hashPassword(password), hashedPassword1, "Should be true");
        Assertions.assertEquals(PasswordEncryptor.hashPassword(password), hashedPassword2, "Should be true");
    }
}
