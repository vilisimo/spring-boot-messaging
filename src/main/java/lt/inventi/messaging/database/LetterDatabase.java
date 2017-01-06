package lt.inventi.messaging.database;

import lt.inventi.messaging.domain.Letter;
import lt.inventi.messaging.exceptions.DraftNotFoundException;
import lt.inventi.messaging.exceptions.InboxNotFoundException;
import lt.inventi.messaging.exceptions.LetterNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class LetterDatabase {

    private HashMap<String, HashMap<Long, Letter>> userDraftsMap;  // HashMap of users. Each user has a map of letters.
    private HashMap<String, HashMap<Long, Letter>> userInboxMap; // map of sent messages f each user.
    private static Long LETTER_ID = 0L;

    public LetterDatabase() {
        this.userDraftsMap = new HashMap<String, HashMap<Long, Letter>>();
        this.userInboxMap = new HashMap<String, HashMap<Long, Letter>>();
    }

    public HashMap<Long, Letter> getUserDrafts(String username) {
        HashMap<Long, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            userDrafts = new HashMap<Long, Letter>();
        }
        return userDrafts;
    }

    public HashMap<Long, Letter> getUserInbox(String username) {
        HashMap<Long, Letter> userInbox = userInboxMap.get(username);
        if (userInbox == null) {
            userInbox = new HashMap<Long, Letter>();
        }
        return userInbox;
    }

    public Letter getInboxLetter(String username, Long letterId) {
        HashMap<Long, Letter> userInbox = getUserInbox(username);
        if (userInbox == null) {
            throw new InboxNotFoundException();
        }
        return userInbox.get(letterId);
    }

    public Letter saveDraftEntry(Letter letter) {
        String author = letter.getAuthor();
        HashMap<Long, Letter> userDrafts = userDraftsMap.get(author);
        if (userDrafts == null) {
            userDrafts = new HashMap<Long, Letter>();
            userDraftsMap.put(author, userDrafts);
        }
        letter.setId(++LETTER_ID);
        userDrafts.put(letter.getId(), letter);
        return letter;
    }

    public Letter saveInboxEntry(Letter letter) {
        String recipient = letter.getRecipient();
        HashMap<Long, Letter> userInbox = userInboxMap.get(recipient);
        if (userInbox == null) {
            userInbox = new HashMap<Long, Letter>();
            userInboxMap.put(recipient, userInbox);
        }
        userInbox.put(letter.getId(), letter);
        return letter;
    }

    public Letter removeEntry(String username, Long letterId) {
        HashMap<Long, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            throw new DraftNotFoundException();
        }
        return userDrafts.remove(letterId);
    }

    public void updateEntry(Letter letter) {
        Letter removed = removeEntry(letter.getAuthor(), letter.getId());
        if (removed == null) {
            throw new LetterNotFoundException();
        }
        userDraftsMap.get(letter.getAuthor()).put(letter.getId(), letter);
    }

    public static Long getAndIncrementLetterID() {
        return ++LETTER_ID;
    }

}
