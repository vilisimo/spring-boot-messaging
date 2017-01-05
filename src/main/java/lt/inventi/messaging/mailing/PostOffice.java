package lt.inventi.messaging.mailing;

import lt.inventi.messaging.domain.Letter;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class PostOffice {
    private static BigInteger letterId = BigInteger.ONE;

    public Letter fillLetterDetails(String username, Letter letter, BigInteger id) {
        letter.setId(letterId);  // Make sure a letter has unique ID
        letterId = letterId.add(BigInteger.ONE);
        letter.setAuthor(username);  // Ensure draft's author is the same person as the one saving it.

        return letter;
    }
}
