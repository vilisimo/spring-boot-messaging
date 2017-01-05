package lt.inventi.messaging.database;

import lt.inventi.messaging.domain.Letter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;

@Component
public class LetterDatabase {
    public HashMap<String, HashMap<BigInteger, Letter>> userDraftsMap;  // HashMap of users. Each user has a HM of letters.
    public HashMap<String, HashMap<BigInteger, Letter>> userInboxMap; // map of sent messages f each user.

    public LetterDatabase() {
        userDraftsMap = new HashMap<String, HashMap<BigInteger, Letter>>();
        userInboxMap = new HashMap<String, HashMap<BigInteger, Letter>>();
    }
}
