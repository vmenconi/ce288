package ce288.client;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			InetAddress server = stub.getAddress(id);
			while (true) {
				Task task = stub.getNext(id);
				if (task == null) {
					Thread.sleep(1000);
				} else {
					ClientWorker worker = new ClientWorker(server, task);
					new Thread(worker).start();
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
