package src.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Client {
	
	public final static String FILE_SEP = System.getProperty("file.separator");
	private Socket clientSocket;
	private ExtendedBufferedInputStream inFromServer;
	private DataOutputStream outToServer;

	public Client(String host, int port) throws UnknownHostException, IOException {
		resetSocket(host, port);
	}
	
	public static void main(String[] args) {
		String[] testArgs = {"GET", "http://localhost/index.htm", "80"};
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
				put(uri, host, port);
			} else if (command.equals("POST")) {
				post(uri, host, port);
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
		this.inFromServer = new ExtendedBufferedInputStream(clientSocket.getInputStream());
		this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
	}

	private void post(URL uri, String host, int port) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("What do you want to post: ");
		String message = br.readLine();
		String postMessage = "POST " + uri.getFile() + " HTTP/1.1" + "\r\n" +
							 "Host: " + host + ":" + port + "\r\n"+
							 "Content-Type: application/x-www-form-urlencoded \r\n" +   
							 "Content-Length: " + message.getBytes("UTF-8").length + "\r\n\r\n"
							 + message + "\r\n\r\n";
		System.out.println(postMessage);
		outToServer.writeBytes(postMessage);
		int code = getCode();
		handle("POST", uri, code, host, port);
	}

	private void put(URL uri, String host, int port) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("What do you want to put: ");		
		String message = br.readLine();
		String putMessage = "PUT " + uri.getFile() + " HTTP/1.1" + "\r\n" +
							 "Host: " + host + ":" + port + "\r\n"+
							 "Content-Type: application/x-www-form-urlencoded \r\n" +   
							 "Content-Length: " + message.getBytes("UTF-8").length + "\r\n\r\n"
							 + message + "\r\n\r\n";
		System.out.println(putMessage);
		outToServer.writeBytes(putMessage);
		int code = getCode();
		handle("PUT", uri, code, host, port);
	}

	private void get(URL uri, String host, int port) throws Exception {
		System.out.println("get : "+ "GET " + uri.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n");
		outToServer.writeBytes("GET " + uri.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n");
		int code = getCode();
		handle("GET", uri, code, host, port);
	}
	
	private void head(URL uri, String host, int port) throws Exception {
		String str = "HEAD " + uri.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n";
		System.out.println("query: " + str);
		outToServer.writeBytes(str);
		int code = getCode();
		handle("HEAD", uri, code, host, port);
	}

	private void handle(String command, URL uri, int code, String host, int port) throws Exception {
		if (code <= 200) {
			if (command.equals("GET")) {
			handleInput(uri, host, port);
			} 
			else if (command.equals("HEAD")) {
				printBuffer();
			}
			System.out.println("sluit zakske");	
			clientSocket.close();
		}
		else if (code == 302) {
			System.out.println("Redirecting to the right page");
			String location = getLocation();
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
			System.out.println("sluit zakske");
			clientSocket.close();
			throw new IllegalArgumentException();
		}
	}
	
	private int getCode() throws IOException {
		inFromServer.mark(100);
		String firstLine = inFromServer.readLine();
		inFromServer.reset();
		System.out.println("FROM SERVER: "+ firstLine);
		String result = firstLine.substring(9, 12);
		System.out.println("Received code: "+ result);
		return Integer.parseInt(result);
	}
	
	private String getLocation() throws Exception {
		inFromServer.mark(100);
		String line;
		while ((line = inFromServer.readLine()) != null) {
			System.out.println("FROM SERVER: "+line);
			if (line.contains("Location: ")) {
				int beginIndex = line.indexOf("Location: ") + 10;
				String result = line.substring(beginIndex);
				System.out.println("LOCATION FOUND: "+result);
				inFromServer.reset();
				return result;
			}
		}
		throw new Exception("No location found");
	}
	
	private void handleInput(URL uri, String host, int port) throws Exception {
		int code = 0;
		String path = System.getProperty("user.dir")+FILE_SEP+"webpages_client"+uri.getPath();
		File file = new File(path);
		System.out.println("path: "+file.getPath());
		int cLength = 0;
		String cType = null;
		boolean body = false;
		while (!body) {
			String line = inFromServer.readLine();
			System.out.println("FROM SERVER: " + line);
			if (line.contains("HTTP/")) {
				code = Integer.parseInt(line.substring(9, 12));
				System.out.println("code found: "+code);
			}
			if (line.contains("Content-Length")) {
				String[] ClengthAr = line.split("Content-Length *: *"); // * for zero or more spaces
				cLength = Integer.parseInt(ClengthAr[1]);
				System.out.println("Found content length: "+ cLength);
			}
			if (line.contains("Content-Type")) {
				String[] CtypeAr = line.split("Content-Type *: *");
				cType = CtypeAr[1];
				System.out.println("Found content type: "+cType);
			}
			if ((line.length() == 0)) {
				System.out.println("body start " + cLength + " " + cType);
				inFromServer.resetBytesRead();
				if ((code >= 300)) {
					inFromServer.skip(cLength);
					cLength = 0;
				}
				body = true;
			}
		}
		if (cType!=null && cType.contains("text")) {
			System.out.println("Printing and writing text!");
			if (cLength !=0) {
				printAndWriteText(file, host, port, cLength);
			}
		} else {
			System.out.println("Writing Image!");
			File directory = new File(file.getParentFile().getAbsolutePath());
			directory.mkdirs();
			writeImage(file, cLength);
		}
	}
	
	private void printAndWriteText(File file, String host, int port, long cLength) throws Exception {
		boolean documentFinished = false;
		ArrayList<String> imgUrls = new ArrayList<>();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		while (!documentFinished) {
			String line = inFromServer.readLine(cLength);
			System.out.println("FROM SERVER: "+line);
			bw.write(line+"\r\n");
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
					URL url = new URL("http://"+host + "/" + src);
					outToServer.writeBytes("GET " + url.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n");
				}
			}
//			System.out.println("buffercount: "+inFromServer.getBytesRead());
//			System.out.println("Clength:"+cLength);
			if ((inFromServer.getBytesRead() >= cLength)) {
				System.out.println("Document finished");
				documentFinished = true;
			}
		}
		bw.close();
		for (String imgUrl : imgUrls) {
			URL url = new URL("http://"+host + "/" + imgUrl);
			handleInput(url, host, port);
		}
		inFromServer.close();
		System.out.println("Written to file.");
	}

	private void writeImage(File file, int clength) throws IOException {
		boolean skipFirstLine = true;
		OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
		boolean documentFinished = false;
		while (!documentFinished ) {
			int ch = inFromServer.read();
			//			System.out.println("buffercount: "+inFromServer.getBytesRead());
			//			System.out.println("Clength:"+cLength);
			if ((inFromServer.getBytesRead() > clength)) {
				System.out.println("Document finished");
				documentFinished = true;
			}
			if (skipFirstLine) {
				skipFirstLine = false;
			} else {
				os.write(ch);
			}
		}
		os.close();
	}
	
	private void printBuffer() throws IOException {
		String line;
		while ((line = inFromServer.readLine()) != null) {
			System.out.println(line);
		}
		
	}
	
}