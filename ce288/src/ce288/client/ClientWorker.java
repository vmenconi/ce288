package ce288.client;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ce288.tasks.Task;

public class ClientWorker implements Runnable {

	public final static Logger logger = LoggerFactory.getLogger(ClientWorker.class);

	private InetAddress server;

	private Task task;

	public ClientWorker(InetAddress server, Task task) {
		this.server = server;
		this.task = task;
	}

	@Override
	public void run() {
		logger.info("Client worker started.");
		try {
			Socket socket = new Socket(server, 12345);
			byte[] buffer = new byte[8192];
			logger.info("Client worker started task {}.", task.getId());
			BufferedWriter out = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
			out.write(task.getFilename());
			out.newLine();
			out.write(Long.toString(task.getPosition()));
			out.newLine();
			out.write(Long.toString(task.getLength()));
			out.newLine();
			out.flush();
			int num;
			while ((num = in.read(buffer)) > 0) {
				logger.debug("Received {} bytes: {}", num, new String(buffer, 0, num));
			}
			socket.close();
			logger.info("Client worker ended.");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
