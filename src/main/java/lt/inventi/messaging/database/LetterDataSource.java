package lt.inventi.messaging.database;

import lt.inventi.messaging.domain.Letter;
import lt.inventi.messaging.exceptions.DraftNotFoundException;
import lt.inventi.messaging.exceptions.LetterNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class LetterDataSource {


    private final HashMap<String, HashMap<Long, Letter>> userDraftsMap;  // Each user has a map of letters.
    private final HashMap<String, HashMap<Long, Letter>> userInboxMap; // Map of sent messages of each user.
    private Long LETTER_ID = 0L;

    public LetterDataSource() {
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

    public void saveDraftEntry(Letter letter) {
        String author = letter.getAuthor();
        HashMap<Long, Letter> userDrafts = userDraftsMap.get(author);
        if (userDrafts == null) {
            userDrafts = new HashMap<Long, Letter>();
            userDraftsMap.put(author, userDrafts);
        }
        letter.setId(++LETTER_ID);
        userDrafts.put(letter.getId(), letter);
        // return letter;
    }

    public void saveInboxEntry(Letter letter) {
        String recipient = letter.getRecipient();
        HashMap<Long, Letter> userInbox = userInboxMap.get(recipient);
        if (userInbox == null) {
            userInbox = new HashMap<Long, Letter>();
            userInboxMap.put(recipient, userInbox);
        }
        userInbox.put(letter.getId(), letter);
        // return letter;
    }

    public void removeDraftEntry(String username, Long letterId) {
        HashMap<Long, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            throw new DraftNotFoundException();
        }

        Letter removed = userDrafts.remove(letterId);
        if (removed == null) {
            throw new LetterNotFoundException();
        }
    }

    public void updateEntry(Letter letter) {
        removeDraftEntry(letter.getAuthor(), letter.getId());
        userDraftsMap.get(letter.getAuthor()).put(letter.getId(), letter);
    }

    public HashMap<String, HashMap<Long, Letter>> getUserDraftsMap() {
        return userDraftsMap;
    }

    public HashMap<String, HashMap<Long, Letter>> getUserInboxMap() {
        return userInboxMap;
    }
}
