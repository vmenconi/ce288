package ce288.tasks;

import java.util.UUID;

public class ExecutionInfo {

	private long startTime;

	private long timeout;
	
	private UUID clientId;
	
	private Task task;
	
	public ExecutionInfo(long startTime, long timeout, UUID clientId, Task task) {
		this.startTime = startTime;
		this.timeout = timeout;
		this.clientId = clientId;
		this.task = task;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public boolean isExpired() {
		return timeout > System.currentTimeMillis();
	}

	public UUID getClientId() {
		return clientId;
	}

	public void setClientId(UUID clientId) {
		this.clientId = clientId;
	}

	public Task getTask() {
		return task;
	}

	public void setTaskId(Task task) {
		this.task = task;
	}
}
