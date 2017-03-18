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

}
