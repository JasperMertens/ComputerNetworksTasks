package src.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Server {
	
	private static int nbOfClients = 0;
	public static final int MAX_NB_OF_CLIENTS = 5;
	
	public static final String[] DATE_FORMAT_STRINGS = {"E, d MMM y H:m:s z",
														"E, d-M-y H:m:s z",
														"E, M d H:m:s y"};
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(Server.DATE_FORMAT_STRINGS[0], Locale.ENGLISH);
	static ServerSocket welcomeSocket;
	static Socket connectionSocket;
	private static Map<String, Date> modifiedDatesMap = new HashMap<>();

	public static void main(String argv[]) throws IOException {
		try {
			welcomeSocket = new ServerSocket(80);
			welcomeSocket.setReuseAddress(true); //om sneller opnieuw te kunnen gebruiken om te testen
			while (true) {
				if (nbOfClients < MAX_NB_OF_CLIENTS) {
					connectionSocket = welcomeSocket.accept();
					System.out.println("Accepted");
					nbOfClients += 1;
					Thread t = new Thread(new ProcessingModule(connectionSocket));
					t.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			welcomeSocket.close();
			connectionSocket.close();
		}
	}
	
	public static Date getLastModified(String filePath) {
		return modifiedDatesMap.get(filePath);
	}
	
	public static String getDate() {
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
		return DATE_FORMAT.format(new Date());
	}
	
}
