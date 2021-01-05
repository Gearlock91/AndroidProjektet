package com.example.projektet;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Denna klass hanterar kryptering.
 * @author Andres Roghe, Sofia Ågren.
 * @version 2020-01-05
 */
public class Encryption {

    private static final String ALGORITHM = "RSA";
    private static String messageEncrypted;
    private static String decryptedMessage;

    public String getMessageEncrypted() {
        return messageEncrypted;
    }

    public String getMessageDecrypted() {
        return decryptedMessage;
    }

    public void encryptMessage(byte[] message, PublicKey key) {
        byte[] encryptedMessage = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedMessage = cipher.doFinal(message);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        messageEncrypted = Base64.getEncoder().encodeToString(encryptedMessage);
    }

    public void decryptMessage(byte[] encryptedMessage, PrivateKey key) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        try {
            decryptedMessage = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }
}
