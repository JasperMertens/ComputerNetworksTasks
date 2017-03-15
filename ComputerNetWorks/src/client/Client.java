package client;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {
	
	public final static String FILE_SEP = System.getProperty("file.separator");
	private Socket clientSocket;
	private BufferedReader inFromServer;
	private DataOutputStream outToServer;

	public Client(String host, int port) throws UnknownHostException, IOException {
		resetSocket(host, port);
	}
	
	public static void main(String[] args) {
		String[] testArgs = {"GET", "localhost", "6789"};
		try {
			if (testArgs.length != 3)
				throw new IllegalArgumentException("Wrong number of arguments!");
			String command = testArgs[0];
			URL uri = new URL(testArgs[1]);
			String host = uri.getHost();
			int port = Integer.parseInt(testArgs[2]);
			try {
				Client client = new Client(host, port);
				client.run(command, uri, host, port);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IllegalArgumentException | MalformedURLException e) {
			e.getMessage();
		}
	}
	
	public void run(String command, URL uri, String host, int port) throws Exception {
		if (command.equals("GET")) {
			System.out.println("trying get");
			get(uri, host, port);
		} else if (command.equals("HEAD")) {
			head(uri, port);
		} else if (command.equals("PUT")) {
			put(uri, port);
		} else if (command.equals("POST")) {
			post(uri, port);
		}
	}

	private void resetSocket(String host, int port) throws UnknownHostException, IOException {
		if (this.clientSocket != null)
			this.clientSocket.close();
		this.clientSocket = new Socket(host, port);
		this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
	}

	private static void post(URL uri, int port) {
		// TODO Auto-generated method stub
		
	}

	private static void put(URL uri, int port) {
		// TODO Auto-generated method stub
		
	}

	private static void head(URL uri, int port) {
		// TODO Auto-generated method stub
		
	}

	private void get(URL uri, String host, int port) throws Exception {
		outToServer.writeBytes("GET " + uri.getFile() + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n");
//		writeToFile(inFromServer);
		int code = getCode(inFromServer);
		handle(code, host, port, inFromServer);
	}

//	private static void get2() throws IOException {
//		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
//		Socket clientSocket = new Socket("www.google.com", 80);
//		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//		String sentence = inFromUser.readLine();
//		outToServer.writeBytes(sentence + "\r\n" + "Host: www.google.com:80" + "\r\n\r\n");
//		String modifiedSentence;
//		while ((modifiedSentence = inFromServer.readLine()) != null) {
//			System.out.println("FROM SERVER: " + modifiedSentence);
//			if (modifiedSentence.contains("HREF=")) {
//				System.out.println("contains HREF");
//				String newLocation = modifiedSentence.substring(9, modifiedSentence.length() - 11);
//				System.out.println(newLocation);
//				DataOutputStream outToServer2 = new DataOutputStream(clientSocket.getOutputStream());
//				BufferedReader inFromServer2 = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//				outToServer2.writeBytes(
//						"GET " + newLocation + " HTTP/1.1" + "\r\n" + "Host: www.google.com:80" + "\r\n\r\n");
//				String serverResponse;
//				while ((serverResponse = inFromServer2.readLine()) != null) {
//					System.out.println("FROM SERVER: " + serverResponse);
//				}
//			}
//		}
//		// clientSocket.close();
//	}
	
	private void handle(int code, String host, int port, BufferedReader inFromServer) throws Exception {
		if (code == 200) {
			System.out.println("OK");
			printAndWriteToFile(inFromServer);
			clientSocket.close();
		}
		else if (code == 302) {
			System.out.println("Redirecting to the right page");
			String location = getLocation(inFromServer);
			URL locationUri = new URL(location);
			String newHost = locationUri.getHost();
			resetSocket(newHost, port);
			get(locationUri, newHost, port);
		}
		else {
			System.out.println("Unknown code: "+ code);
			throw new IllegalArgumentException();
		}
	}
	
	private static int getCode(BufferedReader br) throws IOException {
		br.mark(100);
		String firstLine = br.readLine();
		br.reset();
		System.out.println("first line: "+ firstLine);
		String result = firstLine.substring(9, 12);
		System.out.println("Received code: "+ result);
		return Integer.parseInt(result);
	}
	
	private static String getLocation(BufferedReader br) throws Exception {
		br.mark(100);
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println("FROM SERVER: "+line);
			if (line.contains("Location: ")) {
				int beginIndex = line.indexOf("Location: ") + 10;
				String result = line.substring(beginIndex);
				System.out.println("LOCATION FOUND: "+result);
				br.reset();
				return result;
			}
		}
		throw new Exception("No location found");
	}
	
//	private static void printBuffer(BufferedReader buffer) throws IOException, URISyntaxException {
//		String line;
//		while ((line = buffer.readLine()) != null) {
//			System.out.println("FROM SERVER: " + line);
//		}
//		System.out.println("Done printing!");
//	}
//	
//	private void writeToFile(BufferedReader input) throws IOException {
//		File file = new File("webPage.txt");
//		System.out.println("path: "+file.getPath());
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
//		String line;
//		while ((line = input.readLine()) != null) {
//			bw.write(line+"\r\n");
//		}
//		bw.close();
//		System.out.println("Written to file.");
//	}
	
	private void printAndWriteToFile(BufferedReader br) throws IOException {
		String path = System.getProperty("user.dir")+FILE_SEP+"src"+FILE_SEP+"client"+FILE_SEP+"webPage.txt";
		File file = new File(path);
		System.out.println("path: "+file.getPath());
		if (file.createNewFile()) {
			System.out.println("File is created!");
		} else {
			System.out.println("File already existed!");
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println("FROM SERVER: " + line);
			bw.write(line+"\r\n");
		}
		bw.close();
		br.close();
		System.out.println("Written to file.");
	}
}