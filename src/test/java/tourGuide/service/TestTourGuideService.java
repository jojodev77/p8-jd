package tourGuide.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.exception.UUIDException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.entity.UserLocationModel;
import tourGuide.entity.User;
import tourGuide.entity.UserNearestAttractionsModel;
import tourGuide.entity.UserPreferencesModel;
import tourGuide.entity.location.VisitedLocation;
import tourGuide.entity.trip.Provider;
import tourGuide.microService.GpsUtilMicroService;
import tourGuide.microService.RewardsMicroService;
import tourGuide.microService.TripPricerMicroService;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestTourGuideService {
  @BeforeAll()
  public static void Setup() {
    Locale.setDefault(new Locale("us"));
  }
	@Test
	public void getUserLocation() throws UUIDException {

		InternalTestHelper.setInternalUserNumber(0);
		InternalTestService internalTestService = new InternalTestService();
		GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
		TripPricerMicroService tripPricerMicroService = new TripPricerMicroService();
		RewardsMicroService rewardsMicroService = new RewardsMicroService();
		RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, internalTestService,
      gpsUtilMicroService, tripPricerMicroService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void addUser() {
				InternalTestHelper.setInternalUserNumber(0);

		InternalTestService internalTestService = new InternalTestService();
		GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
		TripPricerMicroService tripPricerMicroService = new TripPricerMicroService();
		RewardsMicroService rewardsMicroService = new RewardsMicroService();
		RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, internalTestService,
      gpsUtilMicroService, tripPricerMicroService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		internalTestService.addUser(user);
		internalTestService.addUser(user2);

		User retrievedUser = tourGuideService.getUser(user.getUserName());
		User retrievedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();

		assertEquals(user, retrievedUser);
		assertEquals(user2, retrievedUser2);
	}

	@Test
	public void getAllUsers() {

		InternalTestHelper.setInternalUserNumber(0);
		InternalTestService internalTestService = new InternalTestService();
		GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
		TripPricerMicroService tripPricerMicroService = new TripPricerMicroService();
		RewardsMicroService rewardsMicroService = new RewardsMicroService();
		RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, internalTestService,
      gpsUtilMicroService, tripPricerMicroService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		internalTestService.addUser(user);
		internalTestService.addUser(user2);

		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() throws UUIDException {

		InternalTestHelper.setInternalUserNumber(0);
		InternalTestService internalTestService = new InternalTestService();
		GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
		TripPricerMicroService tripPricerMicroService = new TripPricerMicroService();
		RewardsMicroService rewardsMicroService = new RewardsMicroService();
		RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, internalTestService,
      gpsUtilMicroService, tripPricerMicroService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearestAttractions() throws UUIDException {
		InternalTestHelper.setInternalUserNumber(1);
		InternalTestService internalTestService = new InternalTestService();
		GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
		TripPricerMicroService tripPricerMicroService = new TripPricerMicroService();
		RewardsMicroService rewardsMicroService = new RewardsMicroService();
		RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, internalTestService,
      gpsUtilMicroService, tripPricerMicroService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		List<UserNearestAttractionsModel> attractions = tourGuideService.getNearestAttractions(visitedLocation, user);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, attractions.size());
	}

	@Test
	public void getAllUserLocations() {
		InternalTestHelper.setInternalUserNumber(5);
		InternalTestService internalTestService = new InternalTestService();
		GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
		TripPricerMicroService tripPricerMicroService = new TripPricerMicroService();
		RewardsMicroService rewardsMicroService = new RewardsMicroService();
		RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, internalTestService,
      gpsUtilMicroService, tripPricerMicroService);

		List<UserLocationModel> listUserLocation = tourGuideService.getAllUsersLocation();

		tourGuideService.tracker.stopTracking();

		assertEquals(5, listUserLocation.size());
	}

	@Test
	public void getTripDeals() {
		InternalTestHelper.setInternalUserNumber(0);
		InternalTestService internalTestService = new InternalTestService();
		GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
		TripPricerMicroService tripPricerMicroService = new TripPricerMicroService();
		RewardsMicroService rewardsMicroService = new RewardsMicroService();
		RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, internalTestService,
      gpsUtilMicroService, tripPricerMicroService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, providers.size());
	}

	@Test
	public void userUpdatePreferences () {
		UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
		userPreferencesDTO.setNumberOfAdults(2);
		userPreferencesDTO.setTripDuration(3);
		userPreferencesDTO.setCurrency("USD");

		UUID userUUID = UUID.fromString("987b1312-768d-41e1-90c1-e62da7c93739");
		User user = new User(userUUID, "internalUser2", "1243456",
				"internalUser2@Gmail.com");
    assertEquals(user.getUserPreferences().getNumberOfAdults(), 1);
		//UserModel user= getUser(userPreferencesDTO.getUsername());
		user.setUserPreferences(new UserPreferencesModel(userPreferencesDTO));
	assertEquals(user.getUserPreferences().getNumberOfAdults(),
				userPreferencesDTO.getNumberOfAdults());
	}
}
