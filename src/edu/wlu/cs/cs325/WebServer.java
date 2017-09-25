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
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.AccessDeniedException;
import java.util.IllegalFormatException;

import javax.imageio.ImageIO;

/**
 *
 */
public class WebServer {

	/**
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
	
	private static void transmitContents(String fileName, String address, int port) throws UnknownHostException, IOException, IllegalFormatException {
		Socket socket = new Socket(address, port);
		String extension = fileName.substring(fileName.indexOf('.')).toLowerCase().trim();
		
		if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif") || extension.equals("png")) {
			BufferedImage im = ImageIO.read(new File(fileName));
			ImageIO.write(im, extension, socket.getOutputStream());
		} else if(extension.equals("html") || extension.equals("css") || extension.equals("txt")) {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			String line;
			while((line = br.readLine()) != null) {
				out.println(line);
			}
			
			br.close();
		} else {
			throw new IllegalFormatException();
		}
	}

}
