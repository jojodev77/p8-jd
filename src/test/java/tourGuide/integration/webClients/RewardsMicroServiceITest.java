package tourGuide.integration.webClients;

import org.apache.commons.lang3.math.NumberUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.exception.UUIDException;
import tourGuide.service.TourGuideService;
import tourGuide.microService.RewardsMicroService;

import java.util.UUID;

@SpringBootTest
//@ActiveProfiles("test")
public class RewardsMicroServiceITest {

    @Autowired
    private RewardsMicroService rewardsMicroService;

    @Autowired
    private TourGuideService tourGuideService;

    @Test
    public void getRewardPointsWebClientShouldReturnFieldsWithValues() throws UUIDException {

        UUID attractionId = new UUID(4872158, 1875147);
        UUID userId = new UUID(41872158, 18175147);
        int rewardPoints = rewardsMicroService.getRewardPointsWebClient(attractionId, userId);
        String rewardPointsString = String.valueOf(rewardPoints);

        Assertions.assertThat(rewardPoints)
                .isNotNull();
        Assertions.assertThat(NumberUtils.isNumber(rewardPointsString)).isTrue();
    }
}
