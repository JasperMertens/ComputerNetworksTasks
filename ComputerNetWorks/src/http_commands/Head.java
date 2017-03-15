package http_commands;

import java.io.*;

public class Head implements Command {

	private File file;

	public Head(File file) {
		this.file = file;
	}

	@Override
	public void getResponse(DataOutputStream outToClient) throws IOException {
		outToClient.writeBytes("HTTP/1.1 200 OK");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			outToClient.writeBytes(line);
		}
	}

}
