package client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Client2 {
	
	public final static String FILE_SEP = "/"; //System.getProperty("file.separator");
	private Socket clientSocket;
	private BufferedReader inFromServer;
	private DataOutputStream outToServer;
	
	public Client2(String host, int port) throws UnknownHostException, IOException {
		resetSocket(host, port);
	}

	private void resetSocket(String host, int port) throws IOException {
		if (this.clientSocket != null)
			this.clientSocket.close();
		this.clientSocket = new Socket(host, port);
		this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
	}
	
	public static void main(String[] args){
		String[] testArgs = {"GET", "http://www.tldp.org/index.html", "80"};
		try {
			if (testArgs.length != 3)
				throw new IllegalArgumentException("Wrong number of arguments!");
			String command = testArgs[0];
			URL uri = new URL(testArgs[1]);
			String host = uri.getHost();
			int port = Integer.parseInt(testArgs[2]);
			Client2 client = new Client2(host, port);
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
			} /*else if (command.equals("HEAD")) {
				head(uri, host, port);
			} else if (command.equals("PUT")) {
				put(uri, port);
			} else if (command.equals("POST")) {
				post(uri, port);
			}*/
		} catch (Exception e) {
			this.clientSocket.close();
			e.printStackTrace();
		}
	}
	
	private void get(URL uri, String host, int port) throws Exception {
		//System.out.println("get : "+ "GET " + uri.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n");
		outToServer.writeBytes("GET " + uri.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n");
		int code = getCode(inFromServer);
		handle("GET", uri, code, host, port);
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
	
	private void handle(String command, URL uri, int code, String host, int port) throws Exception {
		if ((200 <= code) && (code <300)) {
			System.out.println("OK");
			printAndWriteToFile(uri, host, port);
			clientSocket.close();
		}
		else if ((300 <= code) &&(code < 400)) {
			System.out.println("Redirecting to the right page");
			throw new IllegalStateException();
			}
		else {
			System.out.println("Unknown code: "+ code);
			throw new IllegalArgumentException();
		}
	}
	
	private void printAndWriteToFile(URL uri, String host, int port) throws Exception {
		String path = System.getProperty("user.dir")+FILE_SEP+"src"+FILE_SEP+"client"+uri.getPath();
		//al de rest wissen?
		File file = new File(path);
		System.out.println("path: "+file.getPath());
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		ArrayList<String> imgUrls = new ArrayList<>();
		String line;
		Document doc = null;
		while ((line = this.inFromServer.readLine()) != null) {
			doc = Jsoup.parse(line, host);
			bw.write(line+"\r\n");
		}
		Elements imgs = doc.select("img");
			for (Element img: imgs) {
				String src = img.attr("src");
				System.out.println(src);
				if (src.substring(0, 4).equals("http")) {
					//don't follow links
					continue;
				}
				imgUrls.add(src);
				String urlString = host + "/" + src;
				System.out.println("urlString: "+urlString);
				URL url = new URL("http://"+urlString);
				String p = System.getProperty("user.dir")+FILE_SEP+"src"+FILE_SEP+"client"+FILE_SEP+src;
				File targetFile = new File(p);
				File directory = new File(targetFile.getParentFile().getAbsolutePath());
				directory.mkdirs();
				System.out.println("path: "+p);
				//outToServer.writeBytes("GET " + url.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n");
				printAndWriteToFile(url, host, port);
		}
		bw.close();
		this.inFromServer.close();
		System.out.println("Written to file.");
	}
	
}
