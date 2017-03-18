package http_commands;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import server.Server;

public class Get implements Command {

	private File file;
	private String headers = "";

	public Get(File file) {
		this.file = file;
	}

	@Override
	public void getResponse(DataOutputStream outToClient) throws IOException  {
		if (!hasLegalHostHeader()) {
			outToClient.writeBytes("HTTP/1.1 400 Bad Request\r\n");
		} else if (!isModifiedSince()) {
			outToClient.writeBytes("HTTP/1.1 304 Not Modified\r\n");
		} else {
			outToClient.writeBytes("HTTP/1.1 200 OK\r\n");
			String path = file.getPath();
			System.out.println("Loser: "+System.getProperty("user.dir")+path);
			BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+path));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				outToClient.writeBytes(line);
			}
		}
	}
	
	private boolean hasLegalHostHeader() {
		return (this.headers.toLowerCase().contains("host"));
	}
	
	private boolean isModifiedSince() {
		String[] lines = this.headers.split("\r\n");
		for (String line : lines) {
			if (line.toLowerCase().contains("if-modified-since")) {
				Date dateToCheck = tryParse(line);
				if (dateToCheck == null)
					System.out.println("Wrongly parsed date: "+ line);
				// if the dateToCheck (from client) is later than the date when the file got last modified,
				//		then return that file hasn't been modified.
				else if (dateToCheck.after(Server.getLastModified(this.file.getPath()))) {
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
		this.headers.concat(header + "\r\n");
	}

	@Override
	public void addBody(String readLine) {
		// TODO Auto-generated method stub
		
	}
	
	
}
