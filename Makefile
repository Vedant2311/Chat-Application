JCC = javac
JFLAGS = -g

default: TCPClient1.class TCPClient2.class TCPClient3.class TCPServer1.class TCPServer2.class TCPServer3.class mainServer.class mainClass.class


TCPClient1.class: client1.java
	$(JCC) $(JFLAGS) client1.java

TCPClient2.class: client2.java
	$(JCC) $(JFLAGS) client2.java

TCPClient3.class: client3.java
	$(JCC) $(JFLAGS) client3.java


TCPServer1.class: server1.java
	$(JCC) $(JFLAGS) server1.java


TCPServer2.class: server2.java
	$(JCC) $(JFLAGS) server2.java


TCPServer3.class: server3.java
	$(JCC) $(JFLAGS) server3.java

mainServer.class: mainServer.java
	$(JCC) $(JFLAGS) mainServer.java

mainClass.class: mainClass.java
	$(JCC) $(JFLAGS) mainClass.java


clean: 
	$(RM) *.class

