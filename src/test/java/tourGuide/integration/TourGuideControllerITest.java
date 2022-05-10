package tourGuide.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tourGuide.service.InternalTestService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.microService.GpsUtilMicroService;
import tourGuide.microService.RewardsMicroService;
import tourGuide.microService.TripPricerMicroService;

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class TourGuideControllerITest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webContext;

    @BeforeAll
    public void setupMockmvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
      Locale.setDefault(new Locale("us"));
    }

    @Test
    public void test1_getIndexITest() throws Exception {
        String response = "Greetings from TourGuide!";
        MvcResult result = mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
      Assertions.assertTrue(result.getResponse().getContentAsString().contains(response));
    }

    @Test
    public void test2_getStartTrackerITest() throws Exception {
        InternalTestService internalTestService = new InternalTestService();
        GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
        TripPricerMicroService tripPricerMicroService = new TripPricerMicroService();
        RewardsMicroService rewardsMicroService = new RewardsMicroService();
        RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
        TourGuideService tourGuideService = new TourGuideService(rewardsService, internalTestService,
          gpsUtilMicroService, tripPricerMicroService);

        tourGuideService.tracker.startTracking();

        mockMvc.perform(get("/location/startTracker"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void test3_getStopTrackerITest() throws Exception {
        InternalTestService internalTestService = new InternalTestService();
        GpsUtilMicroService gpsUtilMicroService = new GpsUtilMicroService();
        TripPricerMicroService tripPricerMicroService = new TripPricerMicroService();
        RewardsMicroService rewardsMicroService = new RewardsMicroService();
        RewardsService rewardsService = new RewardsService(gpsUtilMicroService, rewardsMicroService);
        TourGuideService tourGuideService = new TourGuideService(rewardsService, internalTestService,
          gpsUtilMicroService, tripPricerMicroService);

        tourGuideService.tracker.stopTracking();

        mockMvc.perform(get("/location/stopTracker"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
