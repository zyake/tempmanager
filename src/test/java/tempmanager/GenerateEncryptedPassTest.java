package tempmanager;

import com.sun.mail.iap.ByteArray;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.KeyStore;

public class GenerateEncryptedPassTest {

    private static final String STORE_KEY = "4b98af86-a445-468f-a358-7759ff1bbed2";

    @Test
    public void testGenerateEncryptedPass() throws Exception {
        FileInputStream inputStream = new FileInputStream("keystore.ks");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(inputStream, STORE_KEY.toCharArray());
        KeyStore.ProtectionParameter entryPassword =
                new KeyStore.PasswordProtection(STORE_KEY.toCharArray());

        KeyStore.SecretKeyEntry myapp = (KeyStore.SecretKeyEntry) keyStore.getEntry("dbpass", entryPassword);
        SecretKey privateKey = myapp.getSecretKey();
        System.out.println(privateKey.getEncoded().length);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        byte[] dataBytes = cipher.doFinal("myPassword".getBytes());

        System.out.println("encoded value: " + Hex.encodeHexString(dataBytes));
    }

    @Test
    public void testGenerateDecryptedPass() throws Exception {
        FileInputStream inputStream = new FileInputStream("keystore.ks");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(inputStream,STORE_KEY.toCharArray());
        KeyStore.ProtectionParameter entryPassword =
                new KeyStore.PasswordProtection(STORE_KEY.toCharArray());

        KeyStore.SecretKeyEntry myapp = (KeyStore.SecretKeyEntry) keyStore.getEntry("dbpass", entryPassword);
        SecretKey privateKey = myapp.getSecretKey();
        System.out.println(privateKey.getEncoded().length);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] bytes = Hex.decodeHex("216f6b12bdefa656a59d272f62b716e2");

        byte[] dataBytes = cipher.doFinal(bytes);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(dataBytes);
        System.out.println("decoded value: " + byteArrayOutputStream.toString());
    }
}
