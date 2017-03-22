package src.http_commands;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import src.server.Server;

public class Post implements Command {
	
	private ArrayList<String> bodyLines = new ArrayList<>();
	private ArrayList<String> headers = new ArrayList<>();
	private File file;

	public Post(File file) {
		this.file = file;
	}

	@Override
	public void getResponse(DataOutputStream outToClient) throws IOException {
		if (!file.exists()) {
			System.out.println(file.exists());
			File directory = new File(file.getParentFile().getAbsolutePath());
			directory.mkdirs();
		}
//		outToClient.writeBytes(	"HTTP/1.1 200 OK\r\n"+
//								"Date: "+Server.getDate()+ "\r\n\r\n");
		String path = file.getPath();
		BufferedWriter br = new BufferedWriter(new FileWriter(path));
		for (String bodyLine:this.bodyLines) {
			System.out.println("putprint: "+bodyLine);
			br.write(bodyLine);
		}
		br.close();
		
	}

	@Override
	public void addHeaders(String header) {
		this.headers.add(header);
		System.out.println("Adding header: "+ header);
	}

	@Override
	public void addBody(String readLine) {
		bodyLines.add(readLine);
		System.out.println("Adding bodyLine: "+ readLine);
	}

	@Override
	public boolean hasBody() {
		// TODO Auto-generated method stub
		return true;
	}

//	@Override
//	public boolean hasBody() {
//		// TODO Auto-generated method stub
//		return true;
//	}

}
