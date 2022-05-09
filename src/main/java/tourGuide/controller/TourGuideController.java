package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.exception.UserNameNotFoundException;
import tourGuide.exception.UserPreferencesNotFoundException;
import tourGuide.service.InternalTestService;
import tourGuide.service.TourGuideService;

@RestController
public class TourGuideController {

    private Logger logger = LoggerFactory.getLogger(TourGuideController.class);

	@Autowired
	TourGuideService tourGuideService;

  @Autowired
  InternalTestService internalTestService;

    /** HTML GET request that returns a welcome message
     *
     * @return a string message
     */
    @GetMapping("/")
    public String index() {
        logger.debug("Access to / endpoint");
        return "Greetings from TourGuide!";
    }

    /**
     * HTML GET request that starts the tracker
     */
    @GetMapping("/location/startTracker")
    public void startTracker() {
        logger.debug("Access to /location/startTracker endpoint");
        tourGuideService.tracker.startTracking();
    }

    /**
     * HTML GET request that stops the tracker
     */
    @GetMapping("/location/stopTracker")
    public void stopTracker() {
        logger.debug("Access to /location/stopTracker endpoint");
        tourGuideService.tracker.stopTracking();
    }

  @PutMapping("/update/Preferences")
  public String updatePreferences(@RequestParam String userName, @RequestBody UserPreferencesDTO userPreferencesDTO)
    throws UserNameNotFoundException, UserPreferencesNotFoundException {
    logger.debug("Access to /update/Preferences endpoint with username : " + userName);
    logger.debug("Access to /update/Preferences endpoint with UserPreferencesDTO as a body : " + userPreferencesDTO);
    if(!internalTestService.checkIfUserNameExists(userName)) {
      logger.error("This username does not exist" + userName);
      throw new UserNameNotFoundException(userName);
    }
    if(userPreferencesDTO == null){
      logger.error("This UserPreferencesDTO does not exist or is invalid" + userPreferencesDTO);
      throw new UserPreferencesNotFoundException();
    }

    return JsonStream.serialize(new UserPreferencesDTO(userName,
      tourGuideService.userUpdatePreferences(userName, userPreferencesDTO)));
  }
}
