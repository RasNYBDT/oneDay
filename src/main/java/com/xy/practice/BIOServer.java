package com.xy.practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer extends Thread{

	private ServerSocket serverSocket;
	
	public void run() {
		try {
			serverSocket = new ServerSocket(0);
			
			ExecutorService executor = Executors.newFixedThreadPool(8);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("服务端：远程连接："+socket.getInetAddress()+":"+socket.getPort());
				RequestHandler requestHandler = new RequestHandler(socket);
				executor.execute(requestHandler);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		BIOServer server = new BIOServer();
		server.start();
		while (true) {
			try (Socket client = new Socket(InetAddress.getLocalHost(), server.serverSocket.getLocalPort())) {
				System.out.println("客户端：本地连接："+client.getLocalAddress()+":"+client.getLocalPort());
				System.out.println("客户端：远程连接："+client.getInetAddress()+":"+client.getPort());
				BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
				br.lines().forEach(s -> System.out.println(s + s));
			}
		}
	}
}


class RequestHandler extends Thread {
	private Socket socket;
	
	RequestHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		try (PrintWriter out = new PrintWriter(socket.getOutputStream())) {
			out.println("hello world!");
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
