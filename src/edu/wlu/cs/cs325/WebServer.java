/**
 * 
 */
package edu.wlu.cs.cs325;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
		try {
			server = new ServerSocket(1999);
			while(true) {
				Socket incoming = server.accept();
				Thread clientThread = new ThreadedHandler(incoming);
				clientThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
		

}
