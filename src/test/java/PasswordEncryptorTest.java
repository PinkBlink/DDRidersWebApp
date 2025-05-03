import org.junit.jupiter.api.Test;
import org.riders.sharing.utils.PasswordEncryptor;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordEncryptorTest {
    @Test
    public void encryptPasswordReturnsSameHash() {
        //given
        final var password = "secret pass";

        //when
        final var encryptedPassword1 = PasswordEncryptor.encryptPassword(password);
        final var encryptedPassword2 = PasswordEncryptor.encryptPassword(password);

        //then
        assertEquals(encryptedPassword1, encryptedPassword2);
    }

    @Test
    public void encryptPasswordReturnsDifferentHash() {
        //given
        final var firstPassword = "first-pass";
        final var secondPassword = "second-pass";

        //when
        final var encryptedFirst = PasswordEncryptor.encryptPassword(firstPassword);
        final var encryptedSecond = PasswordEncryptor.encryptPassword(secondPassword);

        //then
        assertNotEquals(encryptedFirst, encryptedSecond);
    }
}
