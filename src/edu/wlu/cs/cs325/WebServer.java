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
	
	/**
	 * @param fileName the name of the file to check
	 * @return true if the file exists
	 * @throws FileNotFoundException if the file isn't found
	 * @throws AccessDeniedException if the file doesn't have read permissions
	 */
	private static boolean fileExists(String fileName) throws FileNotFoundException, AccessDeniedException {
		File f = new File(fileName);
		
		if(!f.exists())
			throw new FileNotFoundException();
		else if(!f.canRead())
			throw new AccessDeniedException(fileName);
		
		return true;
	}
	
	/**
	 * @param fileName the file name
	 * @param socket the web server's socket
	 * @throws UnknownHostException default exception to account for
	 * @throws IOException default exception to account for
	 * @throws FileFormatException if you try to send an unsupported file-type over the socket
	 */
	private static void transmitContent(String fileName, Socket socket) throws UnknownHostException, IOException, FileFormatException {
		String extension = fileName.substring(fileName.indexOf('.')).toLowerCase().trim();
		
		if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif") || extension.equals("png")) {
			BufferedImage im = ImageIO.read(new File(fileName));
			ImageIO.write(im, extension, socket.getOutputStream());
		} else if(extension.equals("html") || extension.equals("css") || extension.equals("txt")) {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			String line;
			while((line = br.readLine()) != null) {
				out.println(line);
			}
			
			br.close();
			out.close();
		} else {
			throw new FileFormatException();
		}
	}	

}
