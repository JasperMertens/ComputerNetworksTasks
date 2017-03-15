package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private static int nbOfClients = 0;
	public static final int MAX_NB_OF_CLIENTS = 5;
	static ServerSocket welcomeSocket;
	static Socket connectionSocket;

	public static void main(String argv[]) {
		try {
			welcomeSocket = new ServerSocket(6790);
			while (true) {
				if (nbOfClients < MAX_NB_OF_CLIENTS) {
					connectionSocket = welcomeSocket.accept();
					nbOfClients += 1;
					Thread t = new Thread(new ProcessingModule(connectionSocket));
					t.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			try {
				welcomeSocket.close();
				connectionSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
