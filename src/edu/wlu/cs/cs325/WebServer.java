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
		String docRoot;
		int port;
		
		if(args.length == 0) { // if no command line arguments specified
			docRoot = "";  //default document root
			port = 8888; // default port
		} else if(args.length == 1) { // if only document root specified
			docRoot = args[0];
			port = 8888;
		} else { // if both document root and port specified
			docRoot = args[0];
			port = Integer.parseInt(args[1]); 
		}
		
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
