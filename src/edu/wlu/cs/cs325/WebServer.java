/**
 * 
 */
package edu.wlu.cs.cs325;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.AccessDeniedException;

import javax.imageio.ImageIO;

/**
 *
 */
public class WebServer {

	/**
	 */
	public static void main(String[] args) {
		ServerSocket server;
		try {
			server = new ServerSocket(9001);
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
