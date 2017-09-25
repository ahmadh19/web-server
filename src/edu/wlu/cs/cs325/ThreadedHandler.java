/**
 * 
 */
package edu.wlu.cs.cs325;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * @author Hammad Ahmad
 *
 */
public class ThreadedHandler extends Thread {
	
	private Socket incoming;
	
	public ThreadedHandler(Socket incoming) {
		this.incoming = incoming;
	}
	
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
			String line = in.readLine(); // read the line in the form of "GET /index.html HTTP/1.0"
			
			String fileName = "";
			StringTokenizer stringTokenizer = new StringTokenizer(line);
			
			if(stringTokenizer.hasMoreElements()) {
				if(stringTokenizer.nextToken().equalsIgnoreCase("GET")) {
					if(stringTokenizer.hasMoreElements()) {
						fileName = stringTokenizer.nextToken();
					} else {
						throw new Exception("Bad request.");
					}
				} else {
					throw new Exception("Bad request.");
				}
			} else {
				throw new Exception("Bad request.");
			}
			
			// if no explicit fileName is present, use index.html as fileName
			if(fileName.endsWith("/")) {
				fileName += "index.html";
			}
			
			String contentType = ""; // to be used for the Content-Type response header
			if(fileName.endsWith(".html")) {
				contentType = "text/html";
			} else if(fileName.endsWith(".txt")) {
				contentType = "text/plain"; //TODO: Is this correct?
			} else if(fileName.endsWith(".css")) {
				contentType = "text/css";
			} else if(fileName.endsWith(".jpg")) {
				contentType = "image/jpeg";
			} else if(fileName.endsWith(".png")) {
				contentType = "image/png";
			} else if(fileName.endsWith(".gif")) {
				contentType = "image/gif";
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
