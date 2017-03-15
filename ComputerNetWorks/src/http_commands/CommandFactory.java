package http_commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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


	public static Command parseToCommand(InputStream input) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		String firstLine = br.readLine();
		if (firstLine == null) {
			return null;
		}
//		Document doc = Jsoup.parse(input, "UTF-8", null);
		br.close();
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
		}
		return result;
	}
	
	private static String getFilePath(String firstLine) throws MalformedURLException {
		int indexFirstSpace = firstLine.indexOf(' ');
		int indexSecondSpace = firstLine.lastIndexOf(' ');
		String url = firstLine.substring(indexFirstSpace, indexSecondSpace);
		return new URL(url).getQuery();
	}


	private static String getCommand(String firstLine) {
		int indexFirstSpace = firstLine.indexOf(' ');
		String command = firstLine.substring(0, indexFirstSpace);
		return command;
	}
}
