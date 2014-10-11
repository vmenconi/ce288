package ce288.server;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;

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

		InetAddress localAddress = null;
		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				System.out.println(current);
				if (!current.isUp() || current.isLoopback() || current.isVirtual()) {
					continue;
				}
				Enumeration<InetAddress> addresses = current.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress current_addr = addresses.nextElement();
					if (!current_addr.isLoopbackAddress()) {
						localAddress = current_addr;
						logger.debug("Local address is {}.", localAddress.getHostAddress());
						break;
					}
				}
				break;
			}
		} catch (SocketException e) {
			logger.error(e.getMessage(), e);
		}
		if (localAddress == null) {
			throw new RuntimeException("Unable to determine address");
		}
		tasks.addTask(new Task(FileFormat.EMBRACE, localAddress, "teste.txt", 0, 173));
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

}
