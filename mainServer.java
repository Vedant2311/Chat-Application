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
import java.util.*;

class mainServer{

	public static void main(String args[]) throws Exception{

        int number = 0;

		Scanner input = new Scanner(System.in);
    	java.io.BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    	System.out.print("Enter an integer: ");
    	number = input.nextInt();

    	if(number==1){


            TCPServer1 server = new TCPServer1();
            server.main(null);
    		
    	}

    	else if(number==2){

            TCPServer2 server = new TCPServer2();
            server.main(null);
    		


    	}

    	else if(number==3){

            TCPServer3 server = new TCPServer3();
            server.main(null);

    	}

    	else{

    		System.out.println("You entered an invalid Integer\n");
    		return;
    	}

	}

}