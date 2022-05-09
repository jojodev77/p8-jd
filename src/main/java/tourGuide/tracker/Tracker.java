package tourGuide.tracker;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.model.User;
import tourGuide.service.TourGuideService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class Tracker extends Thread {
	private Logger logger = LoggerFactory.getLogger(Tracker.class);
	private static final long trackingPollingInterval = TimeUnit.SECONDS.toSeconds(10);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final TourGuideService tourGuideService;
	private boolean stop = false;

	public Tracker(TourGuideService tourGuideService) {
	this.tourGuideService = tourGuideService;
		//executorService.submit(this);
	}

	/**
	 * Starting the tracker
	 */
	public void startTracking() {
		stop = false;
		executorService.submit(this);
	}

	/**
	 * Assures to shutdown the Tracker thread
	 */
	public void stopTracking() {
		stop = true;
		executorService.shutdownNow();
	}

	/**
	 * Override of the run method once Tracker class is launched
	 * Get a list of all users
	 * Start a StopWatch to count the time
	 * You can either track the time for tracking user locations or calculate rewards
	 * Reset the StopWatch and put to sleep the thread for the time defined in trackingPollingInterval (in seconds)
	 */
	@Override
	public void run() {
		StopWatch stopWatch = new StopWatch();
		while(true) {
			if(Thread.currentThread().isInterrupted() || stop) {
				logger.debug("Tracker stopping");
				break;
			}

			List<User> users = tourGuideService.getAllUsers();
			logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
			stopWatch.start();

			try {
				tourGuideService.trackListUserLocation(users);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			stopWatch.stop();
			logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
			stopWatch.reset();
			try {
				logger.debug("Tracker sleeping");
				TimeUnit.SECONDS.sleep(trackingPollingInterval);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
