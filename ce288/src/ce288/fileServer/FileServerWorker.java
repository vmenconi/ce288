package ce288.fileServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServerWorker implements Runnable {

	public final static Logger logger = LoggerFactory.getLogger(FileServerWorker.class);
	private Socket socket;

	public FileServerWorker(Socket socket) {
		this.socket = socket;
	}

	@Override
	public synchronized void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

			String line = in.readLine();
			if (line == null) {
				logger.error("Received invalid file request: null");
				socket.close();
				return;
			}
			out.write(("Received " + line + "\n").getBytes());

			line = in.readLine();
			if (line == null) {
				logger.error("Received invalid position request: null");
				socket.close();
				return;
			}
			logger.info("The value is {}", Long.parseLong(line));
			out.write(("Received " + line + "\n").getBytes());

			line = in.readLine();
			if (line == null) {
				logger.error("Received invalid length request: null");
				socket.close();
				return;
			}
			logger.info("The value is {}", Long.parseLong(line));
			out.write(("Received " + line + "\n").getBytes());
			
			out.flush();
			socket.close();
			logger.info("Closed connection from {}.", socket.getInetAddress().getHostName());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

}
