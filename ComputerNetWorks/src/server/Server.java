package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private static int nbOfClients = 0;
	public static final int MAX_NB_OF_CLIENTS = 5;

	public static void main(String argv[]) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6789);
		while (true) {
			if (nbOfClients < MAX_NB_OF_CLIENTS) {
				Socket connectionSocket = welcomeSocket.accept();
				nbOfClients += 1;
				Thread t = new Thread(new ProcessingModule(connectionSocket));
				t.start();
			}
		}
	}
}
