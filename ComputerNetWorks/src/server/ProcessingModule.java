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
	public BufferedReader inFromClient;
	public DataOutputStream outToClient;
	private long startTime = System.currentTimeMillis();
	public static final long MAX_TIME = 30000;

	public ProcessingModule(Socket connectionSocket) throws IOException {
		this.connectionSocket = connectionSocket;
		this.inFromClient =  new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));;
		this.outToClient = new DataOutputStream(connectionSocket.getOutputStream());;
	}

	@Override
	public void run() {
		Command c = CommandFactory.parseToCommand(inFromClient);
		String response = c.getResponse();
		try {
			this.outToClient.writeBytes(response);
			checkTimer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void checkTimer() throws IOException {
		long elapsedTime = System.currentTimeMillis() - this.startTime;
		if (elapsedTime >= MAX_TIME) {
			this.connectionSocket.close();
		}
	}
}