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

    public void sendLetter(String username, Long letterId) {
        Letter removedFromDrafts = database.removeEntry(username, letterId);
        database.saveInboxEntry(removedFromDrafts);
    }

    public void sendReply(String username, Letter letter, Long letterId, Long replyId) {
        Letter originalLetter = database.getInboxLetter(username, letterId);
        if (originalLetter == null) {
            throw new LetterNotFoundException();
        }
        letter.setRecipient(originalLetter.getAuthor());
        letter.setAuthor(username);
        letter.setId(replyId);
        database.saveInboxEntry(letter);
    }

    public Long saveDraft(String username, Letter letter) {
        letter.setAuthor(username);
        letter = database.saveDraftEntry(letter);
        return letter.getId();
    }

    public void deleteDraft(String username, Long letterId) {
        database.removeEntry(username, letterId);
    }

    public void editDraft(String username, Letter letter, Long letterId) {
        letter.setId(letterId);
        letter.setAuthor(username);
        database.updateEntry(letter);
    }
}
