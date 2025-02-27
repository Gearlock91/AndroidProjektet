package com.example.projektet;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Denna klass skapar ett unikt nyckelpar för kryptering.
 * @author Andreas Roghe, Sofia Ågren
 * @version 2020-01-05
 */
public class KeyGen {
    private final String ALGORITHM = "RSA";
    private PublicKey puk;
    private PrivateKey pik;

    public KeyGen() {
        generateKey();
    }

    private void generateKey() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            puk = kp.getPublic();
            pik = kp.getPrivate();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPuk() {
        return puk;
    }

    public PrivateKey getPik() {
        return pik;
    }

}