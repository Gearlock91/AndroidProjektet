package com.example.projektet;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public PublicKey getPuk() {
        return puk;
    }
    public PrivateKey getPik() {
        return pik;
    }

//    public void printToFile() {
//        try {
//            FileOutputStream privateKey = new FileOutputStream("PrivateKey");
//            FileOutputStream publicKey 	= new FileOutputStream("PublicKey");
//            ObjectOutputStream oi;
//            oi = new ObjectOutputStream(privateKey);
//            oi.writeObject(java.util.Base64.getEncoder().encodeToString(puk.getEncoded()));
//            oi = new ObjectOutputStream(publicKey);
//            oi.writeObject(java.util.Base64.getEncoder().encodeToString(pik.getEncoded()));
//            oi.close();
//
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
}