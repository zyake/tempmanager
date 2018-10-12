package tempmanager.io;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

public class SecureDataAccessor {

    private static final String STORE_KEY = "4b98af86-a445-468f-a358-7759ff1bbed2";

    private final SecretKey secretKey;

    public SecureDataAccessor(InputStream inputStream) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        try {
            keyStore.load(inputStream, STORE_KEY.toCharArray());
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }

        KeyStore.ProtectionParameter entryPassword =
                new KeyStore.PasswordProtection(STORE_KEY.toCharArray());

        KeyStore.SecretKeyEntry myapp = null;
        try {
            myapp = (KeyStore.SecretKeyEntry) keyStore.getEntry("dbpass", entryPassword);
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            throw new RuntimeException(e);
        }

        this.secretKey = myapp.getSecretKey();
    }

    public String decrypt(String encryptedText) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        byte[] bytes = null;
        try {
            bytes = Hex.decodeHex(encryptedText);
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
        byte[] dataBytes = null;
        try {
            dataBytes = cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(dataBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toString();

    }
}
