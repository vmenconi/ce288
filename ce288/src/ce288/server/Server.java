package ce288.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ce288.tasks.FileFormat;
import ce288.tasks.Task;
import ce288.tasks.TaskRepository;
import ce288.tasks.TaskRepositoryInterface;

public class Server {

	public final static Logger logger = LoggerFactory.getLogger(Server.class);

	private TaskRepository tasks;

	public Server() {
		try {
			tasks = new TaskRepository();
			UnicastRemoteObject.unexportObject(tasks, true);
			TaskRepositoryInterface stub = (TaskRepositoryInterface) UnicastRemoteObject.exportObject(tasks, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("TaskRepository", stub);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void run() {
		tasks.addTask(new Task(FileFormat.EMBRACE, "teste001.txt", 12, 45));
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

}
