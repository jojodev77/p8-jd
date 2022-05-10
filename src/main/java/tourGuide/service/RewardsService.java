package tourGuide.service;

import tourGuide.entity.location.Location;
import tourGuide.entity.location.Attraction;
import tourGuide.entity.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.entity.User;
import tourGuide.entity.UserRewardModel;
import tourGuide.exception.UserNotPresent;
import tourGuide.microService.GpsUtilMicroService;
import tourGuide.microService.RewardsMicroService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RewardsService {

	//A statute mile is what is called more commonly a mile
	//An international statute mile is 1609.344 meters
	//A US statute mile (survey mile) is 1609.3472 meters
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	//Proximity range of the attraction
	private int attractionProximityRange = 200;
	private GpsUtilMicroService gpsUtilMicroService;
	private RewardsMicroService rewardsMicroService;

	public RewardsService(GpsUtilMicroService gpsUtilMicroService, RewardsMicroService rewardsMicroService) {
		this.gpsUtilMicroService = gpsUtilMicroService;
		this.rewardsMicroService = rewardsMicroService;
	}

	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	/**
	 * Calculate the rewards for each attraction in the visited location list
	 * @param user the user model
	 */
	public void calculateRewards(User user) {
    if (user == null) {
      throw  new UserNotPresent();
    }
		CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();
		List<Attraction> attractions = new CopyOnWriteArrayList<>();

		userLocations.addAll(user.getVisitedLocations());
		attractions.addAll(gpsUtilMicroService.getAllAttractionsWebClient());

		userLocations.forEach(v -> {
			attractions.forEach(a -> {
				if (user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(a.attractionName)).count() == 0) {
					if (nearAttraction(v, a)) {
						user.addUserReward(new UserRewardModel(v, a, getRewardPoints(a, user)));
					}
				}
			});
		});
	}

	/**
	 * Compare the distance between an attraction/location and the attraction proximity range
	 * @param attraction
	 * @param location
	 * @return boolean if location is within attraction range
	 */
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}

	/**
	 * Compare the distance between an attraction/visited location and the proximity buffer
	 * @param visitedLocation
	 * @param attraction
	 * @return boolean if visited location is within attraction range
	 */
	public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	/**
	 * Set a random reward point
	 * @param attraction non-used at the moment
	 * @param user non-used at the moment
	 * @return int of a reward point
	 */
	public int getRewardPoints(Attraction attraction, User user) {
    if (user == null) {
      throw  new UserNotPresent();
    }
		UUID attractionId = attraction.attractionId;
		UUID userId = user.getUserId();
		return rewardsMicroService.getRewardPointsWebClient(attractionId, userId);
	}

	/**
	 * Get the distance between two locations
	 * @param loc1 location 1 with latitude and longitude data
	 * @param loc2 location 2 with latitude and longitude data
	 * @return
	 */
	public double getDistance(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
		return statuteMiles;
	}
}
