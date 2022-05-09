package tourGuide.integration.webClients;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.service.TourGuideService;
import tourGuide.microService.TripPricerMicroService;

import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TripPricerlWebClientITest {

    @Autowired
    private TripPricerMicroService tripPricerMicroService;

    @Autowired
    private TourGuideService tourGuideService;

    @Test
    public void getPriceWebClientShouldReturnAttractionListNotNullNotEmpty() {
        String apiKey = "apiKey";
        UUID attractionId = new UUID(48721585, 18755147);
        int adults = 1;
        int children = 0;
        int nightsStay = 2;
        int rewardsPoints = 0;
        Assertions.assertThat(tripPricerMicroService.getPriceWebClient(apiKey, attractionId, adults, children, nightsStay,
                rewardsPoints))
                .isNotNull()
                .isNotEmpty();
    }
}
