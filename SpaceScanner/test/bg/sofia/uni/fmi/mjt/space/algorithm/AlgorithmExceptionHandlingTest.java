package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class AlgorithmExceptionHandlingTest {
    private static final int KEY_SIZE = 128;

    @Test
    public void testEncryptWrongKey() {
        SecretKey randomKey = new SecretKeySpec("randomKey".getBytes(StandardCharsets.ISO_8859_1), "AES");
        Rijndael cipher = new Rijndael(randomKey);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("name".getBytes(StandardCharsets.ISO_8859_1));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        assertThrows(CipherException.class, () -> cipher.encrypt(inputStream, outputStream));
    }

    @Test
    public void testDecryptWithWrongKey() {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenerator.init(KEY_SIZE);
        SecretKey secretKey = keyGenerator.generateKey();
        SecretKey randomKey = new SecretKeySpec("randomKey".getBytes(StandardCharsets.ISO_8859_1), "AES");
        Rijndael cipher = new Rijndael(secretKey);
        Rijndael cipherRandom = new Rijndael(randomKey);

        ByteArrayInputStream inputStream = new ByteArrayInputStream("name".getBytes(StandardCharsets.ISO_8859_1));
        ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();

        try {
            cipher.encrypt(inputStream, encryptedOutputStream);
        } catch (CipherException e) {
            fail("Encryption failed with the right key");
            throw new RuntimeException(e);
        }

        ByteArrayInputStream encryptedInputStream = new ByteArrayInputStream(encryptedOutputStream.toByteArray());
        ByteArrayOutputStream decryptedOutputStream = new ByteArrayOutputStream();

        assertThrows(CipherException.class, () -> cipherRandom.decrypt(encryptedInputStream, decryptedOutputStream));
    }
}
