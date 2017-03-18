package http_commands;

import java.io.*;

public class Get implements Command {

	private File file;

	public Get(File file) {
		this.file = file;
	}

	@Override
	public void getResponse(DataOutputStream outToClient) throws IOException  {
		outToClient.writeBytes("HTTP/1.1 200 OK \r\n");
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
