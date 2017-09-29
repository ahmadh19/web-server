/**
 * 
 */
package edu.wlu.cs.cs325.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.AccessDeniedException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wlu.cs.cs325.ThreadedHandler;

/**
 * @author Hammad Ahmad
 *
 */
public class ThreadedHandlerTest {
	
	ThreadedHandler testClient;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testClient = new ThreadedHandler(new Socket(), "");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetContentType() {
		assertEquals("text/html", testClient.getContentType("index.html"));
		assertEquals("text/plain", testClient.getContentType("test.txt"));
		assertEquals("text/css", testClient.getContentType("style.css"));
		assertEquals("image/jpeg", testClient.getContentType("test.jpg"));
		assertEquals("image/jpeg", testClient.getContentType("test.jpeg"));
		assertEquals("image/png", testClient.getContentType("test.png"));
		assertEquals("image/gif", testClient.getContentType("test.gif"));

	}

}
