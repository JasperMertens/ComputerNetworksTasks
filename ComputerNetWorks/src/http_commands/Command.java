package http_commands;

import java.io.*;

public interface Command {
	
	public void getResponse(DataOutputStream outToClient) throws IOException;

}
