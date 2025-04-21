import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.riders.sharing.utils.PasswordEncryptor;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordEncryptorTest {
    @Test
    public void encryptPasswordReturnsSameHash() {
        final var password = "secret pass";

        final var encryptedPassword1 = PasswordEncryptor.encryptPassword(password);
        final var encryptedPassword2 = PasswordEncryptor.encryptPassword(password);

        assertEquals(encryptedPassword1, encryptedPassword2);
    }

    @Test
    public void encryptPasswordReturnsDifferentHash() {
        final var firstPassword = "first-pass";
        final var secondPassword = "second-pass";

        final var encryptedFirst = PasswordEncryptor.encryptPassword(firstPassword);
        final var encryptedSecond = PasswordEncryptor.encryptPassword(secondPassword);

        assertNotEquals(encryptedFirst, encryptedSecond);
    }
}
