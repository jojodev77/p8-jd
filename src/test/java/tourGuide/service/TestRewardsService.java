package tourGuide.service;

import org.junit.jupiter.api.Test;
import tourGuide.entity.location.Attraction;
import tourGuide.entity.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.entity.User;
import tourGuide.entity.UserRewardModel;
import tourGuide.microService.GpsUtilMicroService;
import tourGuide.microService.RewardsMicroService;
import tourGuide.microService.TripPricerMicroService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRewardsService {

	@Test
	public void userGetRewards() {


		InternalTestHelper.setInternalUserNumber(0);
		InternalTestService internalTestService = new InternalTestService();
		GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
		TripPricerMicroService tripPricerMicroService = new TripPricerMicroService();
		RewardsMicroService rewardsMicroService = new RewardsMicroService();
		RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, internalTestService,
      gpsUtilMicroService, tripPricerMicroService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtilMicroService.getAllAttractionsWebClient().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserRewardModel> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}

	@Test
	public void isWithinAttractionProximity() {
        GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
		RewardsMicroService rewardsMicroService = new RewardsMicroService();
		RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
		Attraction attraction = gpsUtilMicroService.getAllAttractionsWebClient().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}

	@Test
	public void nearAttraction() {
		GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
		RewardsMicroService rewardsMicroService = new RewardsMicroService();
		RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
		InternalTestHelper.setInternalUserNumber(1);

		InternalTestService internalTestService = new InternalTestService();
		TripPricerMicroService tripPricerMicroService = new TripPricerMicroService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, internalTestService,
      gpsUtilMicroService, tripPricerMicroService);
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		tourGuideService.tracker.stopTracking();

		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserRewardModel> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));

		System.out.println(userRewards);
		assertEquals(gpsUtilMicroService.getAllAttractionsWebClient().size(), userRewards.size());
	}
}
