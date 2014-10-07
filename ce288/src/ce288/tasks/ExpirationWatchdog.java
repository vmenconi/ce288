package ce288.tasks;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ExpirationWatchdog implements Runnable {

	private long period;

	private Map<UUID, ExecutionInfo> executingTasks;

	public ExpirationWatchdog(Map<UUID, ExecutionInfo> executingTasks, long period) {
		this.executingTasks = executingTasks;
		this.period = period;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(period);
				Set<UUID> keys = executingTasks.keySet();
				synchronized (keys) {
					Iterator<UUID> iter = keys.iterator();
					while (iter.hasNext()) {
						UUID key = iter.next();
						if (executingTasks.get(key).isExpired()) {
							executingTasks.remove(key);
						}
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
