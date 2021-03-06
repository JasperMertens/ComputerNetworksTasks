package src.http_commands;

import java.io.*;

public interface Command {
	
	public void getResponse(DataOutputStream outToClient) throws IOException;

	public void addHeaders(String readLine);

	public void addBody(String readLine);
	
	public boolean hasBody();

}
