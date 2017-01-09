package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDataSource;
import lt.inventi.messaging.domain.Letter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostOffice {
    private final LetterDataSource database;

    @Autowired
    public PostOffice(LetterDataSource database) {
        this.database = database;
    }

    public Long saveDraft(String username, Letter letter) {
        letter.setAuthor(username);
        letter = database.saveDraftEntry(letter);
        return letter.getId();
    }

    public void deleteDraft(String username, Long letterId) {
        database.removeDraftEntry(username, letterId);
    }

    public void editDraft(String username, Letter letter, Long letterId) {
        letter.setId(letterId);
        letter.setAuthor(username);
        database.updateEntry(letter);
    }

    public void sendLetter(String username, Long letterId) {
        Letter removedFromDrafts = database.removeDraftEntry(username, letterId);
        database.saveInboxEntry(removedFromDrafts);
    }

    // username: the one who replies to the letter
    public void sendReply(String username, Letter letter) {
        letter.setRecipient(letter.getAuthor());
        sendLetter(username, saveDraft(username, letter));
    }
}
