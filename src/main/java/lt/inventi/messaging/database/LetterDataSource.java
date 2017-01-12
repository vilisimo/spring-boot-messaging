package lt.inventi.messaging.database;

import lt.inventi.messaging.domain.Draft;
import lt.inventi.messaging.domain.SentLetter;
import lt.inventi.messaging.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LetterDataSource {

    @Autowired
    private DraftRepository draftRepository;
    @Autowired
    private InboxRepository inboxRepository;

    // private final DraftRepository usersDraftsCollection;  // Each user has a map of letters.
    // private final InboxRepository usersInboxCollection; // Map of sent messages of each user.
    private Long LETTER_ID = 0L;

    // public LetterDataSource() {
    //     this.usersDraftsCollection = new HashMap<String, HashMap<Long, Draft>>();
    //     this.usersInboxCollection = new HashMap<String, HashMap<Long, Draft>>();
    // }

    // public HashMap<Long, Draft> getAllUserDrafts(String username) {
    //     HashMap<Long, Draft> userDrafts = usersDraftsCollection.get(username);
    //     if (userDrafts == null) {
    //         userDrafts = new HashMap<Long, Draft>();
    //     }
    //     return userDrafts;
    // }

    // Returns letters in user's drafts
    public List<Draft> getAllUserDrafts(String author) {
        return draftRepository.findLettersByAuthor(author);
    }

    public Draft getUserDraft(Long letterID) {
        return draftRepository.findOne(letterID);
    }

    // public HashMap<Long, Draft> getUserInbox(String username) {
    //     HashMap<Long, Draft> userInbox = usersInboxCollection.get(username);
    //     if (userInbox == null) {
    //         userInbox = new HashMap<Long, Draft>();
    //     }
    //     return userInbox;
    // }

    // Returns letters in user's inbox
    public List<SentLetter> getUserInbox(String recipient) {
        return inboxRepository.findLettersByRecipient(recipient);
    }

    // public void saveDraftEntry(Draft letter) {
    //     String author = letter.getAuthor();
    //     HashMap<Long, Draft> userDrafts = usersDraftsCollection.get(author);
    //     if (userDrafts == null) {
    //         userDrafts = new HashMap<Long, Draft>();
    //         usersDraftsCollection.put(author, userDrafts);
    //     }
    //     letter.setId(++LETTER_ID);
    //     userDrafts.put(letter.getId(), letter);
    //     // return letter;
    // }

    public void saveDraftEntry(Draft draft) {
        draft.setId(++LETTER_ID);
        draftRepository.save(draft);
    }

    // public void saveInboxEntry(Draft letter) {
    //     String recipient = letter.getRecipient();
    //     HashMap<Long, Draft> userInbox = usersInboxCollection.get(recipient);
    //     if (userInbox == null) {
    //         userInbox = new HashMap<Long, Draft>();
    //         usersInboxCollection.put(recipient, userInbox);
    //     }
    //     userInbox.put(letter.getId(), letter);
    //     // return letter;
    // }

    public void saveInboxEntry(SentLetter letter) {
        inboxRepository.save(letter);
    }

    // public void removeDraftEntry(String username, Long letterId) {
    //     HashMap<Long, Draft> userDrafts = usersDraftsCollection.get(username);
    //     if (userDrafts == null) {
    //         throw new ResourceNotFoundException();
    //     }
    //
    //     Draft removed = userDrafts.remove(letterId);
    //     if (removed == null) {
    //         throw new ResourceNotFoundException();
    //     }
    // }

    public void removeDraftEntry(Draft draft) {
        // List<Draft> userDrafts = draftRepository.findLettersByRecipient(username);
        // if (userDrafts.isEmpty()) {
        //     throw new ResourceNotFoundException();
        // }
        // Draft toDelete = draftRepository.findOne(letterID);
        // if (toDelete == null) {
        //     throw new ResourceNotFoundException();
        // }
        if (draftRepository.exists(draft.getId())) {
            draftRepository.delete(draft);
        } else {
            throw new ResourceNotFoundException();
        }

        // What happens if resource is not found?
    }

    // public void updateEntry(Draft letter) {
    //     removeDraftEntry(letter.getAuthor(), letter.getId());
    //     usersDraftsCollection.get(letter.getAuthor()).put(letter.getId(), letter);
    // }

    public void updateEntry(Draft draft) {
        draftRepository.save(draft);
    }

    // public HashMap<String, HashMap<Long, Draft>> getUsersDraftsCollection() {
    //     return usersDraftsCollection;
    // }

    // public HashMap<String, HashMap<Long, Draft>> getUsersInboxCollection() {
    //     return usersInboxCollection;
    // }
}
