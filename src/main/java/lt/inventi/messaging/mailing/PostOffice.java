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

    public void saveDraft(String username, Letter letter) {
        letter.setAuthor(username);
        database.saveDraftEntry(letter);
        // return letter.getId();
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
        Letter letter = database.getUserDrafts(username).get(letterId);
        database.removeDraftEntry(username, letterId);
        database.saveInboxEntry(letter);
    }

    // username: the one who replies to the letter
    public void sendReply(String username, Letter letter) {
        letter.setRecipient(letter.getAuthor());
        saveDraft(username, letter);
        sendLetter(username, letter.getId());
    }
}
