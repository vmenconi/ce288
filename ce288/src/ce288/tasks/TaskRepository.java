package ce288.tasks;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskRepository extends UnicastRemoteObject implements TaskRepositoryInterface {
	
	private static final long serialVersionUID = -5529240669926777932L;

	public final static Logger logger = LoggerFactory.getLogger(TaskRepository.class);

	public static final long TASK_TIMEOUT = 100000;

	private Map<UUID, ExecutionInfo> executingTasks;

	private Queue<Task> pendingTasks;

	private InetAddress localAddress;
	
	public TaskRepository() throws RemoteException {
		super();
		executingTasks = Collections.synchronizedMap(new HashMap<UUID, ExecutionInfo>());
		pendingTasks = new LinkedList<Task>();
		new Thread(new ExpirationWatchdog(executingTasks, 2500)).start();

		localAddress = null;
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
			throw new RemoteException(e.getMessage(), e);
		}
		if (localAddress == null) {
			throw new RemoteException("Unable to determine address");
		}
	}
	
	public synchronized void addTask(Task task) {
		pendingTasks.add(task);
	}

	@Override
	public synchronized Task getNext(UUID clientId) throws RemoteException {
		Task task = pendingTasks.poll();
		if (task != null) {
			long now = System.currentTimeMillis();
			ExecutionInfo info = new ExecutionInfo(now, now + TASK_TIMEOUT, clientId, task.getId());
			executingTasks.put(task.getId(), info);
			logger.info("Client {} executing task {}.", clientId, task.getId());
		}
		return task;
	}

	@Override
	public synchronized void setResult(UUID clientId, UUID taskId, Result result)
			throws RemoteException {
		logger.info("Client {} finished task {}.", clientId, taskId);
	}

	@Override
	public synchronized void setFailure(UUID clientId, UUID taskId, String msg)
			throws RemoteException {
		logger.info("Client {} failed task {}.", clientId, taskId);
	}

	@Override
	public InetAddress getAddress(UUID clientId) throws RemoteException {
		logger.info("Client {} requested address.", clientId);
		return localAddress;
	}

}
