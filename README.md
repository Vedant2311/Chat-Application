# Chat-Application

This is a chat application that allows users to do an encrypted chat with one another, which cannot be decrypted by the chat server. Users can direct messages to other users through an @ prefix, and the server needs to forward these messages to the intended recipient. The message itself would be encrypted between any pair of users so that the server cannot read the messages, the server can only infer that a communication is happening between the given pair of users

## User registration

A protocol was built for non-encrypted communication. A client application and a multi-threaded server application were written using TCP Sockets. The protocol format is much like HTTP, with a **\n** following each command/header line and a **\n\n** to indicate end of the message. Rather than HTTP’s stateless method though, we will preserve the state for each TCP connection

Each client application will open two TCP sockets to the server, one to send messages to other users,
and one to receive messages from other users. The client application will need to start with registering the user on both the TCP socket. 

Upon receiving the registration messages, the server will check whether the username is well formed, ie. only alphabets and numerals, with no spaces or special characters. It will acknowledge the registration requests if the username seems good, else it will return an error message. Also the server will need to maintain a list of users who have registered and the corresponding TCP Socket objects for the sending and receiving connections over which they registered

## Sending messages

The format of the SEND method is similar to that of various HTTP methods, with a header section
followed by the data section. The header section has a field for content length which tells the server how many bytes to read after the blank line. If the Content-length field is missing then the server should send an error message back to the client and close the socket. This is because in this case the server will not know how to parse any further data it receives on the TCP connection. The client can then reattempt to open a new connection and start do the registration again.

If the message is received correctly by the server then the server should check if the recipient has registered, and attempt to forward the message to the recipient over the socket on which the recipient had registered to receive messages from other users. This is why we mentioned earlier that the server will need to maintain a list of users who have registered, and the corresponding TCP Socket objects for the sending and receiving connections over which they registered. 

The method to forward a message to its intended recipient is explained next. If the recipient is registered at the server, and it acknowledges having received the message, then the server should send a SENT success notification to the sender. Else the server should send an error message to the sender. This will of course be done  on the TCP socket of the sender which is dedicated to sending messages to other users.

The format of the forwarded message is similar to the originally sent message, and the parsing procedure to be followed by the server will also be similar. 

## Client Application

The client application should take a command-line input for the username and the server’s IP address (localhost or 127.0.0.1 for when you are running the server locally), and then start with opening two TCP sockets to the server, one for sending messages to other users and one for receiving messages. When the sockets are opened, the client should first send REGISTER messages and read the acknowledgements.

The client will then need to start two threads. On one thread, it should read one line at a time from stdin, ie. what the user types on the keyboard. Each time this thread reads a line, it should parse it to get the intended recipient and the message. Each line typed by the user should be of the form:  **@[recipient username] [message]**

If the line is not in this format then the thread should reject the line and ask the user to type again. The thread should then send a SEND message to the server and wait for a response. Upon getting a successful response, it should inform the user that the message was delivered successfully, else convey an error to the user. The application can then loop back to waiting to read the next line typed by the user.

On the other thread, it should wait to read FORWARD messages from the server. Upon receiving a message successfully, it should send the appropriate response to the server, display the message to the user, and go back to waiting to receive more messages from the server

## Server Application

The server application would begin with listening for connections on some port number, you can choose any port number. Upon receiving new socket acceptances from a client application, the server should spawn threads for each socket to communicate with client. The threads will begin with expecting to receive REGISTER messages.

For the thread which receives a TORECV message, the thread should add the user and corresponding Socket object to a global Hashtable (or any other suitable data structure), and close the thread, but of course not the Socket. 

For the thread which receives a TOSEND message, the thread should begin to wait to receive SEND messages from the client application. It will parse these messages, and upon receiving well-formed messages the thread should send a FORWARD message to the recipient on the recipient’s Socket with which it registered through the TORECV message. This Socket will need to be looked up from the Hashtable being maintained by the server. The thread will then wait for a response, which it will  convey back to the sender on the TOSEND Socket of the sender, and then begin to wait for more messages from the sender.

This would give a nice and simple chat application through which users can talk one-on-one with
each other. Note that no encryption is done here. **This is the implementation for the first Server**

## Encrypting the messages

Now, this communication is made encrypted so that only the intended recipient can read the message. We used something called a public-private key based encryption for this purpose. Very simply put, this is how it works:

   * Say there are two users A and B, and A wants to send a message to B
   * The message is M
   * Both A and B will have their own respective public and private key pairs, let’s call for (Kpub A      , Kpvt A) for A and (Kpub B , Kpvt B) for B
   * As you would have figured, a public key is public, ie. it can be published publicly, but the private key should not be disclosed
   * For A to send an encrypted message to B, it will encrypt the message on B’s public key, ie. it will send M’ = Kpub B (M). B will then decrypt M’ through its private key, ie. it will obtain M = Kpvt B (M’)

Extended the methods implemented in the client and server applications to encrypt the messages being sent. Used the "RSA" public key algorithm. Also used "base64" encoding to convert binary data into text format. **This is the implementation for the second Server**

## Obtain a hash digest

Although the communication is encrypted, how can message integrity be checked to ensure that the
message has not been tampered with? This is where signatures come into the picture, and they work
as follows, in very simplified terms: 

  * A hash method is used to obtain a digest for the encrypted message, such as through MD5 or SHA1 or SHA2: H = hash(M’)
  * The hash is encrypted on the private key of the sender: H’ = Kpvt A (H) and sent along with the encrypted message (H’, M’) to the recipient
  * The recipient uses the public key of the sender to check whether it has received the encrypted message correctly, ie. whether hash(M’) is the same as Kpub A (H’)

Extended the methods implemented in the client and server applications to implement this. Used "SHA-256" to obtain the digest. **This is the implementation for the third Server**

## Running the code

* Just use the command 'make' to compile all the files. 

* Then run the command "java mainServer", which has all the three Servers combined. The user will be prompted for a mode: Only 1,2,3 are valid. Else you will receive an error. 

* There is also a code of "mainClass.java", which works the same way for the Clients. Also, note that you need to use the same input number for both of the codes else they will result in random errors! Also, run this code on a different terminal for the different Clients
