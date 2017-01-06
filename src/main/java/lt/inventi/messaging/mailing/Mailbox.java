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

    public Long saveDraft(String username, Letter letter) {
        letter.setAuthor(username);
        letter.setId(++letterID);
        return database.saveDraftEntry(letter);
    }

    public void deleteDraft(String username, Long letterid) {
        database.removeEntry(username, letterid);
    }

    public void editDraft(String username, Letter letter, Long letterid) {
        letter.setId(letterid);
        letter.setAuthor(username);
        database.updateEntry(letter);
    }

    public static Long getAndIncrementLetterID() {
        return ++letterID;
    }
}
