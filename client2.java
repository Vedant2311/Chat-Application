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


class TCPClient2
{
	public static int Register(DataOutputStream out, BufferedReader in, String username, int mode, Socket s, String publicKey) throws Exception
	{
		String phrase;
		if (mode == 0)
		{
			phrase = "SEND ";
		}
		else
		{
			phrase = "RECV ";
		}

		out.writeBytes("REGISTER TO" + phrase + username + " " + publicKey + "\n\n");
		System.out.println("REGISTER TO" + phrase + username + "\n\n");
		String recv1 = in.readLine();
		String recv2 = in.readLine();

		//System.out.print("*" + recv2 + "&");

		if (recv2.equals(""))
		{
			if (recv1.equals("REGISTERED TO" + phrase + username))
			{
				return 1;
			}
			else
			{
				System.out.println(recv1);
				out.close();
				in.close();
				s.close();
				return 0;
			}
		}
		else
		{
			System.out.println("Error in server");
			return 0;
		}
	}

	//public static int SendMessage(DataOutputStream out, BufferedReader in, String username) throws Exception

	public static void main(String[] argv) throws Exception
	{
		// boolean regSend = false;
		// boolean regRecv = false;
		KeyPair generateKeyPairSender = CryptographyExample.generateKeyPair();
        byte[] publicKeySender = generateKeyPairSender.getPublic().getEncoded();
        byte[] privateKeySender = generateKeyPairSender.getPrivate().getEncoded();

	    Socket toReceive = new Socket(argv[1], 6789); 
	    Socket toSend = new Socket(argv[1], 6789);

	    DataOutputStream outRecvStream = new DataOutputStream(toReceive.getOutputStream()); 
		BufferedReader inRecvStream = new BufferedReader(new InputStreamReader(toReceive.getInputStream()));

	    DataOutputStream outSendStream = new DataOutputStream(toSend.getOutputStream()); 
		BufferedReader inSendStream = new BufferedReader(new InputStreamReader(toSend.getInputStream()));

		String keyToString = java.util.Base64.getEncoder().encodeToString(publicKeySender);
		int boolRecv = Register(outRecvStream, inRecvStream, argv[0], 1, toReceive, keyToString);
		

		if (boolRecv == 1)
		{
			int boolSend = Register(outSendStream, inSendStream, argv[0], 0, toSend, keyToString);

			if (boolSend == 1)
			{
				System.out.println("Registered");
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				SendMessage2 sent = new SendMessage2(outSendStream, inSendStream, toSend, argv[0]);
				Thread sentThread = new Thread(sent);
				sentThread.start();

				RecvMessage2 recv = new RecvMessage2(outRecvStream, inRecvStream, privateKeySender);
				Thread recvThread = new Thread(recv);
				recvThread.start();
			}
			else
			{
				System.out.println("Registration failed");
			}
			//System.exit(0);
		}
		else
		{
			System.out.println("Registration failed");
		}

		
	}


}

class SendMessage2 implements Runnable
{
	DataOutputStream out;
	BufferedReader in;
	Socket socket;
	String username;

	SendMessage2(DataOutputStream outSend, BufferedReader inSend, Socket socket, String uname)
	{
		this.out = outSend;
		this.in = inSend;
		this.socket = socket;
		this.username = uname;
	}

	public void run()
	{
		try
		{
			String recipient;
			while (true)
			{
				BufferedReader inputFromUser = new BufferedReader(new InputStreamReader(System.in));
				String[] message = inputFromUser.readLine().split(" ", 2);
				if (message[0].charAt(0) == '@')
				{
					recipient = message[0].substring(1);
				}
				else
				{
					System.out.println("Incorrect Format");
					continue;
				}

				if (recipient.equals("Server#") && message[1].equals("UNREGISTER"))
				{
					out.writeBytes("UNREGISTER " + username + "\n\n");
					String recv1 = in.readLine();
					String recv2 = in.readLine();
					if (recv2.equals(""))
					{
						if (recv1.equals("UNREGISTERED " + username))
						{
							out.close();
							in.close();
							socket.close();
							break;
						}
					}
				}

				byte[] publicKeyRecipient;
				out.writeBytes("FETCHKEY " + recipient + "\n\n");
				String getKey = in.readLine();
				String getKeyEnd = in.readLine();

				String[] getKeySplit = getKey.split(" ");
				if (getKeyEnd.equals(""))
				{
					if (getKeySplit[0].equals("KEYSUPPLY"))
					{
						publicKeyRecipient = java.util.Base64.getDecoder().decode(getKeySplit[1]);
						byte[] sentEncryptedMessage = CryptographyExample.encrypt(publicKeyRecipient, message[1].getBytes());
						String sentMessage = java.util.Base64.getEncoder().encodeToString(sentEncryptedMessage);

						out.writeBytes("SEND " + recipient + "\nContent-length: " + sentMessage.length() + "\n\n" + sentMessage + "\n");
						String recv1 = in.readLine();
						String recv2 = in.readLine();

						if (recv2.equals(""))
						{
							if (recv1.equals("SENT " + recipient))
							{
								System.out.println(recv1);
							}
							else if (recv1.equals("ERROR 102 Unable To Send")) 
							{
								System.out.println(recv1);	
							}
							else if (recv1.equals("ERROR 103 Header Incomplete"))
							{
								System.out.println(recv1);
							}
							else
							{
								System.out.println("Server Error");
							}
						}
						else
						{
							System.out.println("Server Error");
						}
					}
				}

			}
			System.exit(0);

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}

class RecvMessage2 implements Runnable
{
	DataOutputStream out;
	BufferedReader in;
	byte[] privateKey;

	RecvMessage2(DataOutputStream outStream, BufferedReader inStream, byte[] private_key)
	{
		this.out = outStream;
		this.in = inStream;
		this.privateKey = private_key;
	}

	public void run()
	{
		
			while(true)
			{
				try{
				String username;
				int len;
				String recv1 = in.readLine();
				String[] m1 = recv1.split(" ");
				if(m1[0].equals("FORWARD"))
				{
					username = m1[1];
					String recv2 = in.readLine();
					String[] m2 = recv2.split(" ");
					if (m2[0].equals("Content-length:"))
					{
						len = Integer.parseInt(m2[1]);
						String recv3 = in.readLine();
						if (recv3.equals(""))
						{
							String recv4 = in.readLine();
							byte[] receivedEncryptedMessage = java.util.Base64.getDecoder().decode(recv4.getBytes());
							byte[] receivedDecryptedMessage = CryptographyExample.decrypt(privateKey, receivedEncryptedMessage);
							String receivedMessage = new String(receivedDecryptedMessage);
							System.out.println(username + " " + receivedMessage);
							out.writeBytes("RECEIVED " + username + "\n\n");
						}
						else
						{
							out.writeBytes("ERROR 103 Header Incomplete\n\n");

						}
					}
					else
					{
						//continue;
						out.writeBytes("ERROR 103 Header Incomplete\n\n");
						continue;
					}

				}
				else
				{
					//continue;
					out.writeBytes("ERROR 103 Header Incomplete\n\n");
					continue;
				}
			
		}
		catch (Exception e)
		{
			//out.writeBytes("ERROR 103 Header Incomplete\n\n");
			continue;
		}
		}
	}
}


















