package src.http_commands;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CommandFactory {
	
	public final static String FILE_SEP = System.getProperty("file.separator");
	
	// Om getHeaders te testen
	public static void main(String[] args) throws IOException {
		String path = System.getProperty("user.dir")+FILE_SEP+"src"+FILE_SEP+"client"+FILE_SEP+"webPage.txt";
		File file = new File(path);
		getHeaders(file);
	}
	

	private static void getHeaders(File file) throws IOException {
		Document doc = Jsoup.parse(file, "UTF-8");
		String title = doc.title();
		Elements headers = doc.select("head");
		System.out.println("Title: "+ title);
		for (Element header : headers) {
			System.out.println(header.text());
		}
	}


	public static Command parseToCommand(BufferedReader inFromClient) throws IOException {
		String firstLine = inFromClient.readLine();
		if (firstLine == null) {
			throw new IllegalStateException("Buffer is empty!");
		}
		System.out.println("Parsing firstLine: "+firstLine);
		Command cmd = parseFirstLine(firstLine);
		long cLength = 0;
		while(!endOfHeaders(inFromClient)) {
			String nextLine = inFromClient.readLine();
			if (nextLine.contains("Content-Length")) {
				String[] ClengthAr = nextLine.split("Content-Length *: *"); // * for zero or more spaces
				cLength = Integer.parseInt(ClengthAr[1]);
				System.out.println("Found content length: "+ cLength);
			}
			System.out.println("Parsing: "+ nextLine);
			cmd.addHeaders(nextLine);
		}
		System.out.println("letste reigel lejug? "+(inFromClient.readLine().length() == 0));
		while(cmd.hasBody() && !endOfRequest(inFromClient)) {
			cmd.addBody(inFromClient.readLine());
		}
		System.out.println("Command created!");
		return cmd;
	}
	
	private static Command parseFirstLine(String firstLine) {
		String commandStr = getCommand(firstLine);
		String filePath = getFilePath(firstLine);
		File file = new File(System.getProperty("user.dir")+FILE_SEP+"webpages"+filePath);
		Command result = null;
		if (commandStr.equals("GET")) {
			result = new Get(file);
		} else if (commandStr.equals("HEAD")) {
			result = new Head(file);
		} else if (commandStr.equals("PUT")) {
			result = new Put();
		} else if (commandStr.equals("POST")) {
			result = new Post(file);
		} else 
			System.out.println("Wrong command: "+commandStr);
		return result;
	}
	
	private static boolean endOfHeaders(BufferedReader br) throws IOException {
		if (endOfRequest(br)) {
			System.out.println("is end of request");
			return true;
		}
		br.mark(100);
		String nextLine = br.readLine();
		br.reset();
		if (nextLine.length() == 0) {
			System.out.println("endOfHeaders");
			return true;
		}
		System.out.println("not end of headers");
		return false;
	}
	
	private static boolean endOfRequest(BufferedReader br) throws IOException {
		br.mark(100);
		String nextLine = br.readLine();
		if ((nextLine == null) || isInitRequestLine(nextLine)) { // checken of er hierna een volgende request komt
			return true;
		}
		br.reset();
		return false;
	}
	
	private static boolean isInitRequestLine(String line) {
		String[] splitted = line.split("\\s+");
		if (splitted.length != 3)
			return false;
		if (!(splitted[0].equals("GET") || splitted[0].equals("HEAD") || 
				splitted[0].equals("PUT") || splitted[0].equals("GET")))
				return false;
		if (!splitted[2].equals("HTTP\\.+"))
			return false;
		return true;
	}
	
	private static String getFilePath(String firstLine) {
		String[] splitted = firstLine.split("\\s+");
		String path = splitted[1];
		return path;
	}


	private static String getCommand(String firstLine) {
		String[] splitted = firstLine.split("\\s+");
		return splitted[0];
	}
}
