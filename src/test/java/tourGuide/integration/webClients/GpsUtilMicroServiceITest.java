package tourGuide.integration.webClients;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.exception.UUIDException;
import tourGuide.model.location.Attraction;
import tourGuide.service.TourGuideService;
import tourGuide.microService.GpsUtilMicroService;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
public class GpsUtilMicroServiceITest {

    @Autowired
    private GpsUtilMicroService gpsUtilMicroService;

    @Autowired
    private TourGuideService tourGuideService;

    @Test
    public void getUserLocationWebClientShouldReturnFieldsWithValues() throws UUIDException {
        UUID userId = new UUID(4872158, 1875147);
        Assertions.assertThat(gpsUtilMicroService.getUserLocationWebClient(userId))
                .isNotNull()
                .hasFieldOrPropertyWithValue("userId", userId)
                .hasFieldOrProperty("location")
                .hasFieldOrProperty("timeVisited");
    }

    @Test
    public void getAllAttractionsWebClientShouldReturnFieldsWithValues() {
        List<Attraction> attractionList = gpsUtilMicroService.getAllAttractionsWebClient();
        Assertions.assertThat(attractionList)
                .isNotNull()
                .isNotEmpty();
    }
}
