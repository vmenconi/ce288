package ce288.fileServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServer {

	public final static Logger logger = LoggerFactory.getLogger(FileServer.class);

	public FileServer() {
		Executor executor = Executors.newCachedThreadPool();
		logger.info("File server started.");

		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(12345);
			while (true) {
				Socket socket = serverSocket.accept();
				logger.info("Received connection from {}.", socket.getInetAddress());
				executor.execute(new FileServerWorker(socket));
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		new FileServer();
	}

}
