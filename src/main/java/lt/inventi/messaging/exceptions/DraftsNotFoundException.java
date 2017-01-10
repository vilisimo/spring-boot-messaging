package lt.inventi.messaging.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="The specified draft does not exist.")
public class DraftsNotFoundException extends RuntimeException {
}
