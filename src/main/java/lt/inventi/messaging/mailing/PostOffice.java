package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDatabase;
import lt.inventi.messaging.domain.Letter;
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

    public void sendReply(String username, Letter letter) {
        letter.setRecipient(letter.getAuthor());  // Original author of the message is the one who will receive a reply
        sendLetter(username, saveDraft(username, letter));
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
