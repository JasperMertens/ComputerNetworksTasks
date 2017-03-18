package http_commands;

import java.io.*;

public class Get implements Command {

	private File file;

	public Get(File file) {
		this.file = file;
	}

	@Override
	public void getResponse(DataOutputStream outToClient) throws IOException {
		outToClient.writeBytes("HTTP/1.1 200 OK");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			outToClient.writeBytes(line);
		}
	}
	
	
}
