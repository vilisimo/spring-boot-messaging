package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDataSource;
import lt.inventi.messaging.domain.Letter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class Mailbox {
    private final LetterDataSource database;

    @Autowired
    public Mailbox(LetterDataSource database) {
        this.database = database;
    }

    public HashMap<Long, Letter> getUserDrafts(String username) {
        return database.getUserDrafts(username);
    }

    public HashMap<Long, Letter> getUserInbox(String username) {
        return database.getUserInbox(username);
    }
}
