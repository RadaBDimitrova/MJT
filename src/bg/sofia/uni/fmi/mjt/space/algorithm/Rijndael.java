package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Rijndael implements SymmetricBlockCipher {

    private static final int KILOBYTE = 1024;
    private static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_FILE_PATH = "secret.key";
    private static final int KEY_SIZE_IN_BITS = 128;
    private final SecretKey secretKey;

    public Rijndael(SecretKey secretKey) {
        this.secretKey = secretKey;

    }

    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(KEY_SIZE_IN_BITS);
        return keyGenerator.generateKey();
    }

    private static void persistSecretKey(SecretKey secretKey) throws IOException {
        byte[] keyBytes = secretKey.getEncoded();
        Path keyFilePath = Path.of(KEY_FILE_PATH);
        Files.write(keyFilePath, keyBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static SecretKey loadSecretKey() throws IOException {
        byte[] keyBytes = Files.readAllBytes(Path.of(KEY_FILE_PATH));
        return new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);
    }

    @Override
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            try (var cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                byte[] buffer = new byte[KILOBYTE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    cipherOutputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            throw new CipherException("Encryption failed", e);
        }
    }

    @Override
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            try (var cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                byte[] buffer = new byte[KILOBYTE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    cipherOutputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new CipherException("Decryption failed", e);
        }
    }
}