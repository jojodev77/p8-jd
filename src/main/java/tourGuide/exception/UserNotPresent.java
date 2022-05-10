package tourGuide.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import tourGuide.entity.User;

@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class UserNotPresent extends RuntimeException {

  private final Logger logger = LoggerFactory.getLogger(UserNameNotFoundException.class);

  public UserNotPresent() {
    super("user not present in method ");
    logger.error("user not present in method");
  }
}
