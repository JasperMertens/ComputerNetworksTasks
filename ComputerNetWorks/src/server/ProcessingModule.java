package src.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import src.http_commands.Command;
import src.http_commands.CommandFactory;


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
						System.out.println("Getting response");
						c.getResponse(this.outToClient);
					}
				}
			}
		} catch (FileNotFoundException e) {
			try {
				outToClient.writeBytes("HTTP/1.1 404 Not Found\r\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			System.out.println("Closing ProcessingModule's socket");
			try {
				connectionSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean inFromClientIsEmpty() {
		try {
			this.inFromClient.mark(100);
			String line = this.inFromClient.readLine();
			this.inFromClient.reset();
			if (line.length() == 0) {
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
			System.out.println("socket timeout");
			this.connectionSocket.close();
		}
	}
}