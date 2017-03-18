package http_commands;

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


	public static Command parseToCommand(BufferedReader br) throws IOException {
		String firstLine = br.readLine();
		
		if (firstLine == null) {
			return null;
		}
//		Document doc = Jsoup.parse(input, "UTF-8", null);
		
		String commandStr = getCommand(firstLine);
		String filePath = getFilePath(firstLine);
		File file = new File(filePath);
//		Elements headers = doc.select("head");
//		for (Element header : headers) {
//			System.out.println(header);
//		}
		Command result = null;
		if (commandStr.equals("GET")) {
			result = new Get(file);
		} else if (commandStr.equals("HEAD")) {
			result = new Head(file);
		} else if (commandStr.equals("PUT")) {
			result = new Put();
		} else if (commandStr.equals("POST")) {
			result = new Post();
		} else 
			System.out.println("Wrong command: "+commandStr);
		return result;
	}
	
	private static Command parseFirstLine(String firstLine) throws MalformedURLException {
		String commandStr = getCommand(firstLine);
		String filePath = getFilePath(firstLine);
		File file = new File(filePath);
		Command result = null;
		if (commandStr.equals("GET")) {
			result = new Get(file);
		} else if (commandStr.equals("HEAD")) {
			result = new Head(file);
		} else if (commandStr.equals("PUT")) {
			result = new Put();
		} else if (commandStr.equals("POST")) {
			result = new Post();
		} else 
			System.out.println("Wrong command: "+commandStr);
		return result;
	}
	
	private static String getFilePath(String firstLine) throws MalformedURLException {
		String[] splitted = firstLine.split("\\s+");
		String path = splitted[1];
		return path;
	}


	private static String getCommand(String firstLine) {
		String[] splitted = firstLine.split("\\s+");
		return splitted[0];
	}
}
