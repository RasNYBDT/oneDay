package com.xy.practice;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NIOServer extends Thread {

	private ServerSocket server;
	public void run() {
		try (Selector selector = Selector.open();ServerSocketChannel channel = ServerSocketChannel.open();) {
			channel.bind(new InetSocketAddress(InetAddress.getLocalHost(), 8888));
			channel.configureBlocking(false);
			
			channel.register(selector, SelectionKey.OP_ACCEPT);
			
			while (true) {
				selector.select();
				Set<SelectionKey> selectKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectKeys.iterator();
				while(iterator.hasNext()) {
					SelectionKey key = iterator.next();
					sayHelloWorld((ServerSocketChannel)key.channel());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sayHelloWorld(ServerSocketChannel channel) throws IOException {
		try (SocketChannel client = channel.accept();) {
			client.write(Charset.defaultCharset().encode("Hello World"));
		}
		
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		NIOServer server = new NIOServer();
		server.start();
		
		try (Socket client = new Socket(InetAddress.getLocalHost(), server.server.getLocalPort())) {
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		}
		
	}
}
