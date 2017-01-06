package lt.inventi.messaging.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="The specified inbox does not exist.")
public class InboxNotFoundException extends RuntimeException {

}