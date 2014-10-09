package ce288.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExpirationWatchdog implements Runnable {

	public final static Logger logger = LoggerFactory.getLogger(TaskRepository.class);

	private long period;

	private TaskRepository repository;

	public ExpirationWatchdog(TaskRepository repository, long period) {
		this.repository = repository;
		this.period = period;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(period);
				repository.removeExpired();
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
