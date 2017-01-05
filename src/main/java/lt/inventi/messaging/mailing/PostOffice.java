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

    public boolean sendLetter(String username, Letter letter, Long letterid) {
        if (letter.getRecipient() == null) {
            return false;
        }

        letter.setId(letterid);
        letter.setAuthor(username);
        boolean removedFromDrafts = database.removeEntry(letter.getAuthor(), letter.getId());

        if (!removedFromDrafts) {
            return false;
        }

        return database.saveInboxEntry(letter) != null;
    }

    public boolean sendReply(String username, Letter letter, Long letterid, Long replyid) {
        Letter originalLetter = database.getInboxLetter(username, letterid);
        if (originalLetter == null){
            return false;
        }

        letter.setRecipient(originalLetter.getAuthor());
        letter.setAuthor(username);
        letter.setId(replyid);
        return database.saveInboxEntry(letter) != null;
    }
}
