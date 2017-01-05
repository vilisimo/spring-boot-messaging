package lt.inventi.messaging.database;

import lt.inventi.messaging.domain.Letter;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class LetterDatabase {

    public HashMap<String, HashMap<Long, Letter>> userDraftsMap;  // HashMap of users. Each user has a HM of letters.
    public HashMap<String, HashMap<Long, Letter>> userInboxMap; // map of sent messages f each user.

    public LetterDatabase() {
        this.userDraftsMap = new HashMap<String, HashMap<Long, Letter>>();
        this.userInboxMap = new HashMap<String, HashMap<Long, Letter>>();
    }

    public HashMap<Long, Letter> getUserDrafts(String username) {
        return userDraftsMap.get(username);
    }

    public HashMap<Long, Letter> getUserInbox(String username) {
        return userInboxMap.get(username);
    }

    public Letter getInboxLetter(String username, Long letterid) {
        HashMap<Long, Letter> userInbox = getUserInbox(username);
        if (userInbox == null) {
            return null;
        }
        return userInbox.get(letterid);
    }

    public Letter saveDraftEntry(Letter letter) {
        String author = letter.getAuthor();
        HashMap<Long, Letter> userDrafts = userDraftsMap.get(author);
        if (userDrafts == null) {
            userDrafts = new HashMap<Long, Letter>();
            userDraftsMap.put(author, userDrafts);
        }
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

    public boolean removeEntry(String username, Long letterid) {
        HashMap<Long, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            return false;
        }
        Letter removedLetter = userDrafts.remove(letterid);
        return removedLetter != null;
    }

    public boolean updateEntry(Letter letter) {
        boolean removed = removeEntry(letter.getAuthor(), letter.getId());
        if (!removed) {
            return false;
        }
        userDraftsMap.get(letter.getAuthor()).put(letter.getId(), letter);
        return true;
    }
}
