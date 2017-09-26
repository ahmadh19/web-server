/**
 * 
 */
package edu.wlu.cs.cs325;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.AccessDeniedException;
import java.util.Date;

import javax.imageio.ImageIO;

/**
 * @author Hammad Ahmad
 * @author Cooper Baird
 *
 */
public class ThreadedHandler extends Thread {
	
	private Socket incoming;
	private String docRoot;
	
	public ThreadedHandler(Socket incoming, String docRoot) {
		this.incoming = incoming;
		this.docRoot = docRoot;
	}
	
	/**
	 * @param fileName the name of the file to check
	 * @return true if the file exists
	 * @throws FileNotFoundException if the file isn't found
	 * @throws AccessDeniedException if the file doesn't have read permissions
	 */
	private boolean fileExists(String fileName) throws FileNotFoundException, AccessDeniedException {
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
	private void transmitContent(String fileName) throws UnknownHostException, IOException, FileFormatException {
		String extension = fileName.substring(fileName.indexOf('.') + 1).toLowerCase().trim();
		
		if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif") || extension.equals("png")) {
			BufferedImage im = ImageIO.read(new File(fileName));
			ImageIO.write(im, extension, incoming.getOutputStream());
		} else if(extension.equals("html") || extension.equals("css") || extension.equals("txt")) {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
			
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
	
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			//PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
			PrintStream out=new PrintStream(new BufferedOutputStream(
			        incoming.getOutputStream()));
			String line = in.readLine(); // read the line in the form of "GET /index.html HTTP/1.0"
			
			String fileName = "";
			String httpProtocol = "";
			String response = "";
			
			String[] parsedLine = line.split("\\s");
			
			if(parsedLine.length == 3 && parsedLine[0].equalsIgnoreCase("GET") && 
					(parsedLine[2].equals("HTTP/1.0") || parsedLine[2].equals("HTTP/1.1"))) {
				fileName = parsedLine[1];
				httpProtocol = parsedLine[2];
			} else {
				throw new Exception("Bad request...");
			}
			
			// if no explicit fileName is present, use index.html as fileName
			if(fileName.endsWith("/")) {
				fileName += "index.html";
			}
			
			fileName = docRoot + fileName;
			System.out.println(fileName);
			
			if(fileExists(fileName)) {
				String contentType = ""; // to be used for the Content-Type response header
				if(fileName.endsWith(".html")) {
					contentType = "text/html";
				} else if(fileName.endsWith(".txt")) {
					contentType = "text/plain";
				} else if(fileName.endsWith(".css")) {
					contentType = "text/css";
				} else if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
					contentType = "image/jpeg";
				} else if(fileName.endsWith(".png")) {
					contentType = "image/png";
				} else if(fileName.endsWith(".gif")) {
					contentType = "image/gif";
				}
				
				Date date = new Date();
				
				if(httpProtocol.equals("HTTP/1.0")) { 
					response = "HTTP/1.0 200 OK\r\n" + 
					          "Content-Type: "+ contentType +"\r\n\r\n";
				} else if(httpProtocol.equals("HTTP/1.1")) { 
					response = "HTTP/1.1 200 OK\r\n" + "Date: " + date + "\r\n" + 
							"Content-Length: " + "content_length_here" +
							"Content-Type: " + contentType +"\r\n\r\n";
				}
				out.print(response);
				
				if(httpProtocol.equals("HTTP/1.0")) incoming.close();
				
				transmitContent(fileName);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
