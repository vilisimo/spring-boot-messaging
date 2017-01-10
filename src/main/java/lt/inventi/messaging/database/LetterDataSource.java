package lt.inventi.messaging.database;

import lt.inventi.messaging.domain.Letter;
import lt.inventi.messaging.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class LetterDataSource {


    private final HashMap<String, HashMap<Long, Letter>> usersDraftsCollection;  // Each user has a map of letters.
    private final HashMap<String, HashMap<Long, Letter>> usersInboxCollection; // Map of sent messages of each user.
    private Long LETTER_ID = 0L;

    public LetterDataSource() {
        this.usersDraftsCollection = new HashMap<String, HashMap<Long, Letter>>();
        this.usersInboxCollection = new HashMap<String, HashMap<Long, Letter>>();
    }

    public HashMap<Long, Letter> getUserDrafts(String username) {
        HashMap<Long, Letter> userDrafts = usersDraftsCollection.get(username);
        if (userDrafts == null) {
            userDrafts = new HashMap<Long, Letter>();
        }
        return userDrafts;
    }

    public HashMap<Long, Letter> getUserInbox(String username) {
        HashMap<Long, Letter> userInbox = usersInboxCollection.get(username);
        if (userInbox == null) {
            userInbox = new HashMap<Long, Letter>();
        }
        return userInbox;
    }

    public void saveDraftEntry(Letter letter) {
        String author = letter.getAuthor();
        HashMap<Long, Letter> userDrafts = usersDraftsCollection.get(author);
        if (userDrafts == null) {
            userDrafts = new HashMap<Long, Letter>();
            usersDraftsCollection.put(author, userDrafts);
        }
        letter.setId(++LETTER_ID);
        userDrafts.put(letter.getId(), letter);
        // return letter;
    }

    public void saveInboxEntry(Letter letter) {
        String recipient = letter.getRecipient();
        HashMap<Long, Letter> userInbox = usersInboxCollection.get(recipient);
        if (userInbox == null) {
            userInbox = new HashMap<Long, Letter>();
            usersInboxCollection.put(recipient, userInbox);
        }
        userInbox.put(letter.getId(), letter);
        // return letter;
    }

    public void removeDraftEntry(String username, Long letterId) {
        HashMap<Long, Letter> userDrafts = usersDraftsCollection.get(username);
        if (userDrafts == null) {
            throw new ResourceNotFoundException();
        }

        Letter removed = userDrafts.remove(letterId);
        if (removed == null) {
            throw new ResourceNotFoundException();
        }
    }

    public void updateEntry(Letter letter) {
        removeDraftEntry(letter.getAuthor(), letter.getId());
        usersDraftsCollection.get(letter.getAuthor()).put(letter.getId(), letter);
    }

    public HashMap<String, HashMap<Long, Letter>> getUsersDraftsCollection() {
        return usersDraftsCollection;
    }

    public HashMap<String, HashMap<Long, Letter>> getUsersInboxCollection() {
        return usersInboxCollection;
    }
}
