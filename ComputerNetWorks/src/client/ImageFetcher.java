package client.images;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ImageFetcher implements Runnable{
	
	
	
	private static final String FILE_SEP = System.getProperty("file.separator");;
	private DataOutputStream outToServer;
	private int port;
	private String line;
	private String host;

	public ImageFetcher(DataOutputStream outToServer, int port, String host, String line) {
		this.outToServer = outToServer;
		this.port = port;
		this.host = host;
		this.line = line;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		//try{
		ArrayList<String> imgUrls = new ArrayList<>();
		Document doc = Jsoup.parse(line, host);
		Elements imgs = doc.select("img");
		if (!imgs.isEmpty()) {
			for (Element img: imgs) {
				try{
					String src = img.attr("src");
					System.out.println(src);
					if (src.substring(0, 4).equals("http")) {
						System.out.println("CONTINUE: "+img.attr("href"));
						continue;
					}
					imgUrls.add(src);
					String urlString = this.host + "/" + src;
					System.out.println("urlString: "+urlString);
					URL url;
					url = new URL("http://"+urlString);
	//				System.out.println("query: "+url.getQuery());
					String p = System.getProperty("user.dir")+FILE_SEP+"src"+FILE_SEP+"client"+FILE_SEP+src;
					File targetFile = new File(p);
					File directory = new File(targetFile.getParentFile().getAbsolutePath());
					directory.mkdirs();
	//				File parent = targetFile.getParentFile();
	//				if (!parent.exists() && !parent.mkdirs()) {
	//				    throw new IllegalStateException("Couldn't create dir: " + parent);
	//				}
	//				targetFile.createNewFile();
					System.out.println("path: "+p);
	//				if (file.createNewFile()) {
	//					System.out.println("File is created!");
	//				} else {
	//					System.out.println("File already existed!");
	//				}
	//				get(url, host, port);
					Thread.sleep(1000);
					outToServer.writeBytes("GET " + url.getFile() + " HTTP/1.1" + "\r\n" + "Host: " + host + ":" + port + "\r\n\r\n");
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
				}
			}
		}
	}

}
