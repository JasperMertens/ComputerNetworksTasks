package client;


import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Client {
	
	public final static String FILE_SEP = System.getProperty("file.separator");
	private Socket clientSocket;
	private BufferedReader inFromServer;
	private DataOutputStream outToServer;

	public Client(String host, int port) throws UnknownHostException, IOException {
		resetSocket(host, port);
	}
	
	public static void main(String[] args) {
		String[] testArgs = {"GET", "http://www.tldp.org/index.html", "80"};
		try {
			if (testArgs.length != 3)
				throw new IllegalArgumentException("Wrong number of arguments!");
			String command = testArgs[0];
			URL uri = new URL(testArgs[1]);
			String host = uri.getHost();
			int port = Integer.parseInt(testArgs[2]);
			Client client = new Client(host, port);
			client.run(command, uri, host, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run(String command, URL uri, String host, int port) throws IOException {
		try {
			if (command.equals("GET")) {
				System.out.println("trying get");
				get(uri, host, port);
			} else if (command.equals("HEAD")) {
				head(uri, host, port);
			} else if (command.equals("PUT")) {
				put(uri, port);
			} else if (command.equals("POST")) {
				post(uri, port);
			}
		} catch (Exception e) {
			this.clientSocket.close();
			e.printStackTrace();
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

	private void get(URL uri, String host, int port) throws Exception {
		System.out.println("get : "+ "GET " + uri.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n");
		outToServer.writeBytes("GET " + uri.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n");
		int code = getCode(inFromServer);
		handle("GET", uri, code, host, port, inFromServer);
	}
	
	private void head(URL uri, String host, int port) throws Exception {
		String str = "HEAD " + uri.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n";
		System.out.println("query: " + str);
		outToServer.writeBytes(str);
		int code = getCode(inFromServer);
		handle("HEAD", uri, code, host, port, inFromServer);
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
//				}arg0
//			}
//		}
//		// clientSocket.close();
//	}
	
	private void handle(String command, URL uri, int code, String host, int port, BufferedReader inFromServer) throws Exception {
		if (code == 200) {
			System.out.println("OK");
			printAndWriteToFile(inFromServer, uri, host, port);
			clientSocket.close();
		}
		else if (code == 302) {
			System.out.println("Redirecting to the right page");
			String location = getLocation(inFromServer);
			URL locationUri = new URL(location);
			String newHost = locationUri.getHost();
			resetSocket(newHost, port);
			if (command == "GET")
				get(locationUri, newHost, port);
			else if (command == "HEAD")
				head(locationUri, newHost, port);
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
	
	private void printAndWriteToFile(BufferedReader br, URL uri, String host, int port) throws Exception {
		String path = System.getProperty("user.dir")+FILE_SEP+"src"+FILE_SEP+"client"+uri.getPath();
		File file = new File(path);
		System.out.println("path: "+file.getPath());
		if (file.createNewFile()) {
			System.out.println("File is created!");
		} else {
			System.out.println("File already existed!");
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		int count = 0;
		int Clength = 200;
		boolean body = false;
		boolean documentFinished = false;
		ArrayList<String> imgUrls = new ArrayList<>();
//		String line;
//		while ((line = br.readLine()) != null) {
		while (!documentFinished) {
			String line = br.readLine();
			System.out.println("FROM SERVER: " + line);
			if (line.contains("Content-Length:")) {
				String[] ClengthAr = line.split("Content-Length: ");
				Clength = Integer.parseInt(ClengthAr[1]);
			}
			if (line.contains("</HTML>")) { // cheator compleator
				System.out.println("Document finished");
				documentFinished = true;
			}
			
			count += line.getBytes("UTF-8").length + 2; // +2 voor \r\n
			System.out.println("COUNT: "+ count);
			System.out.println("Clength: "+ Clength);
			if (line.contains("Content-Type")) {
				System.out.println("body count reset");
				count = 0;
			}
			if (count >= Clength) {
				System.out.println("Document finished");
				documentFinished = true;
			}
			
			Document doc = Jsoup.parse(line, host);
			Elements imgs = doc.select("img");
			if (!imgs.isEmpty()) {
				for (Element img: imgs) {
					String src = img.attr("src");
					System.out.println(src);
					if (src.substring(0, 4).equals("http")) {
						System.out.println("CONTINUE: "+img.attr("href"));
						continue;
					}
					imgUrls.add(src);
					String urlString = host + "/" + src;
					System.out.println("urlString: "+urlString);
					URL url = new URL("http://"+urlString);
//					System.out.println("query: "+url.getQuery());
					String p = System.getProperty("user.dir")+FILE_SEP+"src"+FILE_SEP+"client"+FILE_SEP+src;
					File targetFile = new File(p);
					File directory = new File(targetFile.getParentFile().getAbsolutePath());
					directory.mkdirs();
//					File parent = targetFile.getParentFile();
//					if (!parent.exists() && !parent.mkdirs()) {
//					    throw new IllegalStateException("Couldn't create dir: " + parent);
//					}
//					targetFile.createNewFile();
					System.out.println("path: "+p);
//					if (file.createNewFile()) {
//						System.out.println("File is created!");
//					} else {
//						System.out.println("File already existed!");
//					}
//					get(url, host, port);
					outToServer.writeBytes("GET " + url.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n");
				}
			}
			bw.write(line+"\r\n");
		} 
		bw.close();
		for (String imgUrl : imgUrls) {
			System.out.println(imgUrl);
			String urlString = host + "/" + imgUrl;
			System.out.println("urlString: "+urlString);
			URL url = new URL("http://"+urlString);
			printAndWriteToFile(br, url, host, port);
		}
		br.close();
		System.out.println("Written to file.");
	}
	
//	private void getImages() throws Exception{
//		File input = new File("webPage.html");
//		Document doc = Jsoup.parse(input, "UTF-8", "http://www.tcpipguide.com");
//		Elements images = doc.select("img");
//		int i = 0;
//		for (Element image : images){
//			System.out.println(image.attr("src"));		
//		}
//	}
}