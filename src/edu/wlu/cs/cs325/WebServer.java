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
	public static void main(String[] args) {
		ServerSocket server;
		
		String docRoot = args[0];
		int port = Integer.parseInt(args[1]); //TODO: set a default port if none specified
		
		try {
			server = new ServerSocket(port, 15);
			while(true) {
				Socket incoming = server.accept();
				Thread clientThread = new ThreadedHandler(incoming, docRoot);
				clientThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}	

}
