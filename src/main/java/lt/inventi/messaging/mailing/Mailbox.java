package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDatabase;
import lt.inventi.messaging.domain.Letter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class Mailbox {
    private static Long letterID = 0L;
    private final LetterDatabase database;

    @Autowired
    public Mailbox(LetterDatabase database) {
        this.database = database;
    }

    public HashMap<Long, Letter> getUserDrafts(String username) {
        return database.getUserDrafts(username);
    }

    public HashMap<Long, Letter> getUserInbox(String username) {
        return database.getUserInbox(username);
    }

    public Letter saveDraft(String username, Letter letter) {
        if (letter.getRecipient() == null) {
            return null;
        }
        // Should checks be in DB layer or Mailbox/PostOffice?
        letter.setId(++letterID);
        letter.setAuthor(username);
        return database.saveDraftEntry(letter);
    }

    public boolean deleteDraft(String username, Long letterid) {
        boolean deleted = database.removeEntry(username, letterid);
        return deleted;
    }

    public boolean editDraft(String username, Letter letter, Long letterid) {
        // Recipient was dropped in an edit.
        if (letter.getRecipient() == null) {
            return false;
        }

        letter.setId(letterid);
        letter.setAuthor(username);
        letter.setContent(letter.getContent());
        return database.updateEntry(letter);
    }

    public static Long getAndIncrementLetterID() {
        return ++letterID;
    }
}
