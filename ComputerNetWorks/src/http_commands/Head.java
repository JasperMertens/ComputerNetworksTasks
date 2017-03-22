package src.http_commands;

import java.io.*;
import java.util.Date;

import src.server.Server;

public class Head implements Command {

	private File file;

	public Head(File file) {
		this.file = file;
	}

	@Override
	public void getResponse(DataOutputStream outToClient) throws IOException {
		outToClient.writeBytes("HTTP/1.1 200 OK"+
								"Date: "+Server.DATE_FORMAT.format(new Date()));
		BufferedReader br = new BufferedReader(new FileReader(file));
		boolean head = true;
		String line;
		while (((line = br.readLine()) != null) && head) {
			if (line.length() == 0) {
				head = false;
			}
			else {
				outToClient.writeBytes(line);
			}
		}
	}

	@Override
	public void addHeaders(String readLine) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBody(String readLine) {
		// TODO Auto-generated method stub
		
	}

}
