package src.http_commands;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import src.server.Server;

public class Get implements Command {
	
	public final static String FILE_SEP = System.getProperty("file.separator");
	private File file;
	private ArrayList<String> headers = new ArrayList<>();

	public Get(File file) {
		this.file = file;
	}

	@Override
	public void getResponse(DataOutputStream outToClient) throws IOException  {
		if (!hasLegalHostHeader()) {
			outToClient.writeBytes("HTTP/1.1 400 Bad Request\r\n");
		} else if (!isModifiedSince()) {
			outToClient.writeBytes(	"HTTP/1.1 304 Not Modified\r\n"+
									"Date: "+Server.getDate());
		} else {
			outToClient.writeBytes(	"HTTP/1.1 200 OK\r\n"+
									"Content-Length: "+file.length()+"\r\n"+
									"Content-Type: "+Files.probeContentType(Paths.get(file.getAbsolutePath()))+"\r\n" +
									"Date: "+Server.getDate()+ "\r\n\r\n"
									);
			String path = file.getPath();
			System.out.println("Loser: "+path);
			BufferedInputStream br = new BufferedInputStream(new FileInputStream(path));
			int ch;
			while ((ch = br.read()) != -1) {
				outToClient.write(ch);
			}
		}
	}
	
	private boolean hasLegalHostHeader() {
		for (String header: this.headers) {
			if (header.toLowerCase().contains("host")) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isModifiedSince() {
		for (String header : this.headers) {
			if (header.toLowerCase().contains("if-modified-since")) {
				Date dateToCheck = tryParse(header);
				Date lastModified = Server.getLastModified(file.getPath());
				if (dateToCheck == null) {
					System.out.println("Wrongly parsed date: "+ header);
				}
				// if the dateToCheck (from client) is later than the date when the file got last modified,
				//		then return that file hasn't been modified.
				else if (lastModified == null || dateToCheck.after(lastModified)) {
					return false;
				}
				return true;
			}
		}
		return true;
		
	}
	
	Date tryParse(String dateString) {
		for (String formatString : Server.DATE_FORMAT_STRINGS) {
			try { 
				return new SimpleDateFormat(formatString).parse(dateString);
			} catch (ParseException e) {}
		}
		return null;
	}
	
	@Override
	public void addHeaders(String header) {
		this.headers.add(header);
		System.out.println("Adding header: "+ header);
	}

	@Override
	public void addBody(String bodyLine) {
		System.out.println("Nog niet geïmplementeerd");
		
	}

	@Override
	public boolean hasBody() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
