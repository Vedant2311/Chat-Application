import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import java.io.*; 
import java.net.*; 
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


import javax.crypto.Cipher;

public class CryptographyExample {

    private static final String ALGORITHM = "RSA";

    public static byte[] encrypt(byte[] publicKey, byte[] inputData)
            throws Exception {
        PublicKey key = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKey));

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(inputData);

        return encryptedBytes;
    }

	 public static byte[] encryptUsingPrivate(byte[] privateKey, byte[] inputData) throws Exception {        
        PrivateKey key = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(inputData);
        return encryptedBytes;
    }

	
    public static byte[] decrypt(byte[] privateKey, byte[] inputData)
            throws Exception {

        PrivateKey key = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(privateKey));

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedBytes = cipher.doFinal(inputData);

        return decryptedBytes;
    }

	public static byte[] decryptUsingPublic(byte[] publicKey, byte[] inputData) throws Exception {
        PublicKey key = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKey));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(inputData);
        return decryptedBytes;
    }    

	
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

        keyGen.initialize(512, random);

        KeyPair generateKeyPair = keyGen.generateKeyPair();
        return generateKeyPair;
    }

    
}

