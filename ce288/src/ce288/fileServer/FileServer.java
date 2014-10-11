package ce288.fileServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServer {

	public static final Logger logger = LoggerFactory.getLogger(FileServer.class);
	
	public static final int PORT = 12345;

	public FileServer(String path) {
		Executor executor = Executors.newCachedThreadPool();
		logger.info("File server started.");

		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(PORT);
			while (true) {
				Socket socket = serverSocket.accept();
				logger.info("Received connection from {}.", socket.getInetAddress());
				executor.execute(new FileServerWorker(socket, path));
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		if (args.length >= 1) {
			new FileServer(args[0]);
		} else {
			logger.error("Missing path argument.");
		}
	}

}
