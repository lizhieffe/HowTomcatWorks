package Chap03;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class HttpServer {
	
	// shutdown command
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	private boolean shutdown = false;
	
	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		// loop waiting for a request
		while (!shutdown) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				
				// create Request object and parse
				Request request = new Request(input);
				request.parse();
				
				// create Response object
 				Response response = new Response(output);
				response.setRequest(request);
				
				// check if this is a request for a servlet or a static resource
				if (request.getUri().startsWith("/servlet/")) {
					ServletProcessor1 processor = new ServletProcessor1();
					processor.process(request, response);
				}
				else {
					StaticResourceProcessor processor = new StaticResourceProcessor();
					processor.process(request, response);
				}				
				socket.close();
				
				// check if the previous  URI is a shutdown command
				shutdown = request.getUri().equalsIgnoreCase(SHUTDOWN_COMMAND);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public static void main(String[] args) {
		HttpServer server = new HttpServer();
		server.await();
	}
}