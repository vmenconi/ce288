package ce288.client;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ce288.fileServer.FileServer;
import ce288.tasks.Result;
import ce288.tasks.Task;
import ce288.tasks.TaskRepositoryInterface;

public class Client {

	public final static Logger logger = LoggerFactory.getLogger(Client.class);

	private UUID id;
	
	public Client() {
		id = UUID.randomUUID();
	}

	public void run() {
		logger.info("Client {} started.", id);
		try {
			Registry registry = LocateRegistry.getRegistry();
			TaskRepositoryInterface stub = (TaskRepositoryInterface) registry.lookup("TaskRepository");
			while (true) {
				Task task = stub.getNext(id);
				if (task == null) {
					Thread.sleep(1000);
				} else {
					logger.info("Client started task {}.", task.getId());
					try {
						Socket socket = new Socket(task.getLocation(), FileServer.PORT);
						byte[] buffer = new byte[8192];
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
							logger.debug("Received {} bytes: {}", num, new String(buffer, 0, num).trim());
						}
						socket.close();
						logger.info("Task {} finished.", task.getId());
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
//					stub.setResult(id, task.getId(), new Result());
				}
			}
		} catch (IOException | NotBoundException | InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.run();
	}
}
