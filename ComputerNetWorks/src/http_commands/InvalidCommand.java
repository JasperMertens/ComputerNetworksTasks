package http_commands;

import java.io.DataOutputStream;
import java.io.IOException;

public class InvalidCommand implements Command {

	String problem;
	
	public InvalidCommand(String problem) {
		this.problem = problem;
	}

	@Override
	public void getResponse(DataOutputStream outToClient) throws IOException {
		outToClient.writeBytes("HTTP/1.1 500 Server Error \r\n There was a problem with " + this.problem);;
	}

}
