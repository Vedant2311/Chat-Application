import java.io.*; 
import java.net.*; 
import java.util.*;

class TCPServer3
{
	public static HashMap<String, Socket[]> registered = new HashMap<>();
	public static HashMap<String, String> public_keys = new HashMap<>();

	public static void main(String[] argv) throws Exception
	{
		ServerSocket welcomeSocket = new ServerSocket(6789);

		while (true)
		{
			Socket forThread = welcomeSocket.accept();
			DataOutputStream outStream = new DataOutputStream(forThread.getOutputStream()); 
      		BufferedReader inStream = new BufferedReader(new InputStreamReader(forThread.getInputStream()));

      		SocketThread3 sthread = new SocketThread3(outStream, inStream, forThread);
      		Thread thread = new Thread(sthread);
      		thread.start();

		}
	}
}

class SocketThread3 implements Runnable
{
	DataOutputStream out;
	BufferedReader in;
	Socket sock;

	SocketThread3(DataOutputStream outStream, BufferedReader inStream, Socket s)
	{
		this.out = outStream;
		this.in = inStream;
		this.sock = s;
	}

	public void run()
	{
		try
		{
			String regMessage = in.readLine();
			String regMessage2 = in.readLine();

			if (regMessage == null)
			{
				throw new Exception("Not valid");
			}
			else
			{

			if (regMessage2.equals(""))
			{
				String[] regSplit = regMessage.split(" ");
				if (regSplit[0].equals("REGISTER"))
				{
					if (regSplit[1].equals("TORECV"))
					{
						if (regSplit[2].matches("[a-zA-Z0-9]+"))
						{
							if (TCPServer3.registered.containsKey(regSplit[2]))
							{
								System.out.println("ERROR 100 Malformed username\n\n");
								out.writeBytes("ERROR 100 Malformed username\n\n");
								out.close();
								in.close();
								throw new Exception("Socket is closed");
							}
							else
							{
								TCPServer3.registered.put(regSplit[2], new Socket[]{sock, null});

								out.writeBytes("REGISTERED TORECV " + regSplit[2] + "\n\n");	
								System.out.println("REGISTERED TORECV " + regSplit[2] + "\n\n");	
							}
						}
						else
						{
							out.writeBytes("ERROR 100 Malformed username\n\n");
							System.out.println("ERROR 100 Malformed username\n\n");
						}
					}
					else if (regSplit[1].equals("TOSEND"))
					{
						if (regSplit[2].matches("[a-zA-Z0-9]+"))
						{
							Socket sent = TCPServer3.registered.get(regSplit[2])[0];
							TCPServer3.public_keys.put(regSplit[2], regSplit[3]);
							TCPServer3.registered.put(regSplit[2], new Socket[]{sent,sock});

							out.writeBytes("REGISTERED TOSEND " + regSplit[2] + "\n\n");
							System.out.println("REGISTERED TOSEND " + regSplit[2] + "\n\n");

							while (true)
							{
								String message = in.readLine();
								String[] sent1 = message.split(" ");
								if (sent1[0].equals("SEND"))
								{
									String recipient = sent1[1];
									if (TCPServer3.registered.containsKey(recipient))
									{
										message = in.readLine();
										sent1 = message.split(" ");
										if (sent1[0].equals("Content-length:"))
										{
											int size = Integer.parseInt(sent1[1]);

											String hash = in.readLine();
											message = in.readLine();
											if (message.equals(""))
											{
												message = in.readLine();
												message = message.substring(0,size);
												Socket sendAck = TCPServer3.registered.get(recipient)[0];
												DataOutputStream toRecipient = new DataOutputStream(sendAck.getOutputStream());
												BufferedReader fromRecipient = new BufferedReader(new InputStreamReader(sendAck.getInputStream()));

												String[] hashSplit = hash.split(" ");

												toRecipient.writeBytes("FORWARD " + regSplit[2] + "\nContent-length: " + size + "\nMessage-Signature: "
																						+ hashSplit[1] + "\n\n" + message + "\n");
												System.out.println("FORWARD " + regSplit[2] + "\nContent-length: " + size + "\nMessage-Signature: "
																						+ hashSplit[1] + "\n\n" + message + "\n");

												String hashKey = fromRecipient.readLine();
												String[] hashKeySplit = hashKey.split(" ");
												if (hashKeySplit[0].equals("FETCHKEY")) 
												{
													String publicKey = TCPServer3.public_keys.get(hashKeySplit[1]);
													String hashKeyEnd = fromRecipient.readLine();
													if (hashKeyEnd.equals(""))
													{
														toRecipient.writeBytes("KEYSUPPLY " + publicKey + "\n\n");
														System.out.println("KEYSUPPLY " + publicKey + "\n\n");
													}
													else
													{
														toRecipient.writeBytes("ERROR 101 Can't be processed");
													}
													
												}

												String recAck1 = fromRecipient.readLine();
												String recAck2 = fromRecipient.readLine();
												if (recAck2.equals(""))
												{
													if (recAck1.equals("RECEIVED " + regSplit[2]))
													{
														out.writeBytes("SENT " + recipient + "\n\n");
														System.out.println("SENT " + recipient + "\n\n");
													}
													else if (recAck1.equals("ERROR 103 Header Incomplete"))
													{
														out.writeBytes("ERROR 103 Header Incomplete\n\n");
													}
												}
												else
												{
													out.writeBytes("ERROR 102 Unable To Send\n\n");
												}
											}
											else
											{
												out.writeBytes("ERROR 102 Unable To Send\n\n");
											}

										}
										else
										{
											out.writeBytes("ERROR 102 Unable To Send\n\n");
										}
									}
									else
									{
										out.writeBytes("ERROR 102 Unable To Send\n\n");
									}
								}
								else if (sent1[0].equals("FETCHKEY")) 
								{
									

									String publicKey = TCPServer3.public_keys.get(sent1[1]);
										System.out.println(sent1[1]);
										String sent2 = in.readLine();
										if (sent2.equals(""))
										{
											out.writeBytes("KEYSUPPLY " + publicKey + "\n\n");
											System.out.println("KEYSUPPLY " + publicKey + "\n\n");
										}
										else
										{
											out.writeBytes("ERROR 101 Can't be processed");
										}
								}
								else if (sent1[0].equals("UNREGISTER"))
								{
									message = in.readLine();
									if (message.equals(""))
									{
										String unregUser = sent1[1];
										Socket[] removed = TCPServer3.registered.remove(unregUser);
										String removedKey = TCPServer3.public_keys.remove(unregUser);


										out.writeBytes("UNREGISTERED " + sent1[1] + "\n\n");
										System.out.println("UNREGISTERED " + sent1[1] + "\n\n");
										out.close();
										in.close();
										removed[0].close();
										removed[1].close();
										break;
									}
									else
									{
										out.writeBytes("ERROR 103 Client Error");
									}
								}
								else
								{
									out.writeBytes("ERROR 101 No user registered\n\n");	
								}
							}
						}
						else
						{
							out.writeBytes("ERROR 100 Malformed username\n\n");
						}
					}
					else
					{
						out.writeBytes("ERROR 101 No user registered\n\n");
					}
				}
				else
				{
					out.writeBytes("ERROR 101 No user registered\n\n");
				}
			}
			else
			{
				out.writeBytes("ERROR 101 No user registered\n\n");
			}
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}