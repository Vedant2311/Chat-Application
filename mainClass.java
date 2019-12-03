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

class mainClass{

	public static void main(String args[]) throws Exception{

		Scanner input = new Scanner(System.in);
    	java.io.BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    	System.out.print("Enter an integer: ");
    	int number = input.nextInt();

    	if(number==1){

    		TCPClient1 client = new TCPClient1();
    		
        	System.out.print("Enter Client Information: Enter as 'Name' 'IP address': \n");
	    	String myString = in.readLine();
	    	String [] args1 = myString.split(" ");

	    	System.out.println(args1.length);
	    	System.out.println(args1[0]);

    		client.main(args1);
    		
    	}

    	else if(number==2){

    	    TCPClient2 client = new TCPClient2();
    		
        	System.out.print("Enter Client Information: Enter as 'Name' 'IP address': \n");
	    	String myString = in.readLine();
	    	String [] args1 = myString.split(" ");

	    	System.out.println(args1.length);
	    	System.out.println(args1[0]);

    		client.main(args1);
    		


    	}

    	else if(number==3){

			TCPClient3 client = new TCPClient3();
    		
        	System.out.print("Enter Client Information: Enter as 'Name' 'IP address': \n");
	    	String myString = in.readLine();
	    	String [] args1 = myString.split(" ");

	    	System.out.println(args1.length);
	    	System.out.println(args1[0]);

    		client.main(args1);
    		

    	}

    	else{

    		System.out.println("You entered an invalid Integer\n");
    		return;
    	}

	}

}