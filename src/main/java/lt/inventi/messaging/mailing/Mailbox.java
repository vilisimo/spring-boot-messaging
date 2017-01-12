package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDataSource;
import lt.inventi.messaging.domain.Draft;
import lt.inventi.messaging.domain.SentLetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mailbox {
    private final LetterDataSource database;

    @Autowired
    public Mailbox(LetterDataSource database) {
        this.database = database;
    }

    public List<Draft> getUserDrafts(String username) {
        return database.getAllUserDrafts(username);
    }

    public List<SentLetter> getUserInbox(String username) {
        return database.getUserInbox(username);
    }
}
