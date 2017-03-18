package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import http_commands.Command;
import http_commands.CommandFactory;


public class ProcessingModule implements Runnable {
	
	public Socket connectionSocket;
	private BufferedReader inFromClient;
	public DataOutputStream outToClient;
	private long startTime = System.currentTimeMillis();
	public static final long MAX_TIME = 30000;

	public ProcessingModule(Socket connectionSocket) throws IOException {
		this.connectionSocket = connectionSocket;
		this.inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		this.outToClient = new DataOutputStream(connectionSocket.getOutputStream());;
	}

	@Override
	public void run() {
		try {
			while (true) {
				System.out.println("Waiting for input");
				waitForInput();
				System.out.println("Input received");
				while (!inFromClientIsEmpty()) {
					Command c = CommandFactory.parseToCommand(this.inFromClient);
					if (c != null) {
						c.getResponse(this.outToClient);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean inFromClientIsEmpty() {
		try {
			this.inFromClient.mark(100);
			String line = this.inFromClient.readLine();
			this.inFromClient.reset();
			if (line == null) {
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private void waitForInput() throws InterruptedException, IOException {
		while (inFromClientIsEmpty()) {
			Thread.sleep(1000);
//			checkTimer();
		}
	}
	
	private void checkTimer() throws IOException {
		long elapsedTime = System.currentTimeMillis() - this.startTime;
		if (elapsedTime >= MAX_TIME) {
			this.connectionSocket.close();
		}
	}
}