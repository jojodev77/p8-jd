package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.entity.*;
import tourGuide.entity.location.Attraction;
import tourGuide.entity.location.VisitedLocation;
import tourGuide.entity.trip.Provider;
import tourGuide.tracker.Tracker;
import tourGuide.microService.GpsUtilMicroService;
import tourGuide.microService.TripPricerMicroService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private RewardsService rewardsService;
	private GpsUtilMicroService gpsUtilMicroService;
	private InternalTestService internalTestService;
	private TripPricerMicroService tripPricerMicroService;
	public Tracker tracker = new Tracker(this);
	boolean testMode = true;
	private final int nbNearestAttractions = 5;

	ExecutorService executorService = Executors.newFixedThreadPool(88);

	/**
	 * Constructor of the class TourGuideService for initializing users
	 * if testMode (default value = true) then initializeInternalUsers based on internalUserNumber value in
	 * InternalTestHelper
	 * Initialize Tracker
	 * Ensure that the thread Tracker shuts down by calling addShutDownHook before closing the JVM
	 * @param rewardsService
	 */
	public TourGuideService(RewardsService rewardsService, InternalTestService internalTestService,
                          GpsUtilMicroService gpsUtilMicroService, TripPricerMicroService tripPricerMicroService) {
		this.rewardsService = rewardsService;
		this.internalTestService = internalTestService;
		this.gpsUtilMicroService = gpsUtilMicroService;
		this.tripPricerMicroService = tripPricerMicroService;

		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			internalTestService.initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		//Start the tracker
		tracker.startTracking();

		addShutDownHook();
	}

	/**
	 * Get a single user from InternalUserMap
	 * @param userName
	 * @return a user
	 */
	public User getUser(String userName) {
		return internalTestService.internalUserMap.get(userName);
	}

	/**
	 * Get a list of all users from the InternalUserMap
	 * @return a list of users
	 */
	public List<User> getAllUsers() {
		return internalTestService.internalUserMap.values().stream().collect(Collectors.toList());
	}

	/**
	 * Get the UserRewards of the concerned user
	 * @param user
	 * @return a list of UserRewards
	 */
	public List<UserRewardModel> getUserRewards(User user) {
		return user.getUserRewards();
	}

	/**
	 * Get the VisitedLocation of the concerned user
	 * If user.getVisitedLocations size is greater than 0 then get the lastVisitedLocation
	 * Else trackUserLocation
	 * @param user
	 * @return a visitedLocation
	 */
	public VisitedLocation getUserVisitedLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}

	/**
	 * Get a list of Trip Deals in a form of a list of Providers according to the user preferences
	 * @param user the concerned user
	 * @return list of Provider
	 */
	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

		UUID userId = user.getUserId();
			List<Provider> providers = tripPricerMicroService.getPriceWebClient(internalTestService.tripPricerApiKey, userId,
					user.getUserPreferences().getNumberOfAdults(),
					user.getUserPreferences().getNumberOfChildren(),
					user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
			user.setTripDeals(providers);
		return providers;
	}

	/**
	 * Get the UserLocation from GpsUtil, add it to the visitedLocation and calculate the Rewards
	 * @param user
	 * @return the visited location of the random location of user
	 */
	public VisitedLocation trackUserLocation(User user) {
		UUID userId = user.getUserId();
		VisitedLocation visitedLocation = gpsUtilMicroService.getUserLocationWebClient(userId);
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	/**
	 * Create an ExecutorService thread pool in which a runnable of trackUserLocation is executed
	 * @param userList the list containing all users
	 * @throws InterruptedException
	 */
	public void trackListUserLocation(List<User> userList) throws InterruptedException {
		for (User user: userList) {
			Runnable runnable = () -> {
				trackUserLocation(user);
			};
			executorService.execute(runnable);
		}
	}

	/**
	 * Get a list of all user Locations from the existent GpsUtil list (tracking locations every X second)
	 * @return a list of UserLocationModel containing all user ID's and location
	 */
	public List<UserLocationModel> getAllUsersLocation() {
		List<User> userList = getAllUsers();
		List<UserLocationModel> userLocationList = new ArrayList<>();

		userList.forEach(u -> {
			userLocationList.add(new UserLocationModel(u.getLastVisitedLocation().location, u.getUserId().toString()));
		});

		return userLocationList;
	}

	/**
	 * Get the closest 5 attractions of the user
	 * @param visitedLocation
	 * @return a list of attractions
	 */
	public List<UserNearestAttractionsModel> getNearestAttractions(VisitedLocation visitedLocation, User user) {

		List<Attraction> attractions = gpsUtilMicroService.getAllAttractionsWebClient();
		List<UserNearestAttractionsModel> nearestAttractions = new ArrayList<>();

		List<Future> futuresList = new ArrayList<>();
		for (Attraction attraction : attractions) {
			Callable changeUserNearest = () -> new UserNearestAttractionsModel(attraction.attractionName,
					attraction.longitude, attraction.latitude,
					visitedLocation.location, rewardsService.getDistance(attraction, visitedLocation.location),
					rewardsService.getRewardPoints(attraction, user));
			Future mapUserNearestAttractions = executorService.submit(changeUserNearest);
			futuresList.add(mapUserNearestAttractions);
		};

		for (Future future: futuresList) {
			UserNearestAttractionsModel at = null;
			try {
				at = (UserNearestAttractionsModel) future.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			nearestAttractions.add(at);
		}

		List<UserNearestAttractionsModel> listAttractionsSorted = nearestAttractions
				.stream()
				.sorted(Comparator.comparing(UserNearestAttractionsModel::getAttractionProximityRangeMiles))
				.limit(nbNearestAttractions)
				.collect(Collectors.toList());

		return listAttractionsSorted;
	}

	/**
	 * Add a shut down hook for stopping the Tracker thread before shutting down the JVM
	 */
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		        tracker.stopTracking();
		      }
		    });
	}

	/**
	 * Sets the user preferences with the new user preferences from UserPreferencesDTO
	 *
	 * @param userPreferencesDTO
	 * @return new UserPreferences
	 */
	public UserPreferencesModel userUpdatePreferences (String userName, UserPreferencesDTO userPreferencesDTO) {
		User user= getUser(userName);
		user.setUserPreferences(new UserPreferencesModel(userPreferencesDTO));
		return user.getUserPreferences();
	}
}
