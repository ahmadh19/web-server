/**
 * 
 */
package edu.wlu.cs.cs325;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class WebServer {

	/**
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		ServerSocket server;
		
		// defaults to be used when no command line arguments are provided
		String docRoot = ""; //default document root
		int port = 8888; // default port
		
		if(args.length == 1) { // if only document root specified 
							   // assume first argument is always the document root
			docRoot = args[0];
		} else if(args.length == 2) { // if both document root and port specified
			docRoot = args[0];
			port = Integer.parseInt(args[1]); 
		}
		
		try {
			server = new ServerSocket(port, 15); // backlog of 15 connections
			while(true) {
				Socket incoming = server.accept();
				Thread clientThread = new ThreadedHandler(incoming, docRoot);
				int activeCount = clientThread.activeCount();
				if(activeCount == 1) {
					System.out.println("1");
					incoming.setSoTimeout(20000);
				} else if(activeCount <= 10) {
					System.out.println("2");
					incoming.setSoTimeout(10000);
				} else {
					System.out.println("3");
					incoming.setSoTimeout(5000);
				}
				clientThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}	

}
