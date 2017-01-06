package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDatabase;
import lt.inventi.messaging.domain.Letter;
import lt.inventi.messaging.exceptions.LetterNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostOffice {
    private final LetterDatabase database;

    @Autowired
    public PostOffice(LetterDatabase database) {
        this.database = database;
    }

    public void sendLetter(String username, Long letterid) {
        Letter removedFromDrafts = database.removeEntry(username, letterid);
        database.saveInboxEntry(removedFromDrafts);
    }

    public void sendReply(String username, Letter letter, Long letterid, Long replyid) {
        Letter originalLetter = database.getInboxLetter(username, letterid);
        if (originalLetter == null) {
            throw new LetterNotFoundException();
        }
        letter.setRecipient(originalLetter.getAuthor());
        letter.setAuthor(username);
        letter.setId(replyid);
        database.saveInboxEntry(letter);
    }

    public Long saveDraft(String username, Letter letter) {
        letter.setAuthor(username);
        // letter.setId(++letterID);
        letter = database.saveDraftEntry(letter);
        return letter.getId();
    }

    public void deleteDraft(String username, Long letterid) {
        database.removeEntry(username, letterid);
    }

    public void editDraft(String username, Letter letter, Long letterid) {
        letter.setId(letterid);
        letter.setAuthor(username);
        database.updateEntry(letter);
    }
}
