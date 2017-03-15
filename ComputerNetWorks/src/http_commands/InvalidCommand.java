package http_commands;

public class InvalidCommand implements Command {

	String problem;
	
	public InvalidCommand(String problem) {
		this.problem = problem;
	}

	@Override
	public String getResponse() {
		return "HTTP/1.1 500 Server Error \r\n There was a problem with " + this.problem;
	}

}
