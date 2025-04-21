import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.riders.sharing.utils.PasswordEncryptor;

public class PasswordEncryptorTest {
    @Test
    public void encryptPassword(){
        final var password = "secret pass";

        final var encryptedPassword1 = PasswordEncryptor.encryptPassword(password);
        final var encryptedPassword2 = PasswordEncryptor.encryptPassword(password);

        Assertions.assertEquals(encryptedPassword1, encryptedPassword2);
    }
}
