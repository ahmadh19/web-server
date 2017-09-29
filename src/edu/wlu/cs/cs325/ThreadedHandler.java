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
	private String responseStatus;
	
	public ThreadedHandler(Socket incoming, String docRoot) {
		this.incoming = incoming;
		this.docRoot = docRoot;
	}
	
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			PrintStream out = new PrintStream(new BufferedOutputStream(incoming.getOutputStream()));
			
			try {
				String fileName = "";
				String httpProtocol = "";
				String host = "";
				String response = "";
				String line;
				
				// read the request
				if((line = in.readLine()) != null) {
					
					System.out.println("Request: '" + line + "'");
				
					String[] parsedParams = parseRequest(in, line);
					
					fileName = parsedParams[0];
					httpProtocol = parsedParams[1];
					host = parsedParams[2];
				
					// if no explicit fileName is present, use index.html as fileName
					if(fileName.equals("/")) {
						fileName = "/index.html";
					}
					
					fileName = docRoot + fileName;
					
					if(fileExists(fileName)) {
						String contentType = getContentType(fileName); // to be used for the Content-Type response header
						
						Date date = new Date();
						
						if(httpProtocol.equals("HTTP/1.0")) { 
							response = "HTTP/1.0 200 OK\r\n" + "Date: " + date + "\r\n" +  
									"Content-Length: " + new File(fileName).length() + "\r\n" +
							        "Content-Type: "+ contentType +"\r\n\r\n";
						} else { 
							response = "HTTP/1.1 200 OK\r\n" + "Date: " + date + "\r\n" + 
									"Content-Length: " + new File(fileName).length() + "\r\n" +
									"Content-Type: " + contentType + "\r\n" +
									"Connection: " + "close" + "\r\n\r\n";
						}
						out.print(response);
						
						transmitContents(out, fileName);
						
						//if(httpProtocol.equals("HTTP/1.0")) incoming.close();
					}
				}
				
				out.close();
				if(httpProtocol.equals("HTTP/1.0") && !incoming.isClosed()) incoming.close();
			
			} catch (FileNotFoundException e) {
				out.println("HTTP/1.1 404 Not Found\r\n" + "Date: " + new Date() + "\r\n" +
				          "Content-Type: "+ "text/html" + "\r\n\r\n" + 
						"<html><body>Error: 404 Not Found</body></html>");
				out.close();
				e.printStackTrace();
			} catch (AccessDeniedException e) {
				out.println("HTTP/1.1 403 Forbidden\r\n" + "Date: " + new Date() + "\r\n" +
				          "Content-Type: "+ "text/html" + "\r\n\r\n" + 
						"<html><body>Error: 403 Forbidden</body></html>");
				out.close();
				e.printStackTrace();
			} catch (BadHTTPRequestException e) {
				out.println("HTTP/1.1 400 Bad Request\r\n" + "Date: " + new Date() + "\r\n" +
				          "Content-Type: "+ "text/html" + "\r\n\r\n" + 
						"<html><body>Error: 400 Bad Request</body></html>");
				out.close();
				e.printStackTrace();
			} catch(FileFormatException e) {
				e.printStackTrace();
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
	public boolean fileExists(String fileName) throws FileNotFoundException, AccessDeniedException {
		File f = new File(fileName);
		
		if(!f.exists()) {
			responseStatus = "404";
			throw new FileNotFoundException();
		}
		else if(!f.canRead()) {
			responseStatus = "403";
			throw new AccessDeniedException(fileName);
		}
		
		return true;
	}
	
	public String getContentType(String fileName) {
		String contentType = "";
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
		return contentType;
	}

	public String[] parseRequest(BufferedReader in, String line)
			throws IOException, BadHTTPRequestException {
		String fileName, httpProtocol, host;
		String[] parsedLine = line.split("\\s");

		if(parsedLine.length == 3 && parsedLine[0].equalsIgnoreCase("GET") && 
				(parsedLine[2].equals("HTTP/1.0") || parsedLine[2].equals("HTTP/1.1"))) {
			fileName = parsedLine[1];
			httpProtocol = parsedLine[2];
			String[] returnVals = new String[3];
			returnVals[0] = fileName;
			returnVals[1] = httpProtocol;
			returnVals[2] = "";
			if(httpProtocol.equals("HTTP/1.1")) {
				line = in.readLine(); // read the Host: ... line
				if(line != null) {
					parsedLine = line.split("\\s");
					if(!parsedLine[0].equalsIgnoreCase("Host:") || parsedLine[1].equals("")){
						responseStatus = "400";
						throw new BadHTTPRequestException();
					} else {
						host = parsedLine[1];
						returnVals[2] = host;
						while(!(line = in.readLine()).equals("")) {
							// read other request headers but don't process them
						}
						return returnVals;
					}
				} else {
					responseStatus = "400";
					throw new BadHTTPRequestException();
				}
			} 
			
			while(!(line = in.readLine()).equals("")) {
				// read other request headers but don't process them
			}
			return returnVals;
			
		} else {
			responseStatus = "400";
			throw new BadHTTPRequestException();
		}
	}

	public void transmitContents(PrintStream out, String fileName)
			throws IOException, FileNotFoundException, FileFormatException {
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase().trim();
		
		if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif") || extension.equals("png")) {
			BufferedImage im = ImageIO.read(new File(fileName));
			ImageIO.write(im, extension, out);
		} else if(extension.equals("html") || extension.equals("css") || extension.equals("txt")) {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
			String fileLine;
			while((fileLine = br.readLine()) != null) {
				out.println(fileLine);
			}
			
			br.close();
			
		} else {
			throw new FileFormatException();
		}
	}
	
}
