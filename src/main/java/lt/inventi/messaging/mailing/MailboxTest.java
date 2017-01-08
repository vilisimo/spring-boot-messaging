package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDataSource;
import lt.inventi.messaging.domain.Letter;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class MailboxTest {
    private Mailbox mailbox;
    private HashMap<Long, Letter> stub = new HashMap<Long, Letter>();
    private LetterDataSource stubLetter = new LetterDataSource() {
        public HashMap<Long, Letter> getUserDrafts(String username) {
            return stub;
        }

        public HashMap<Long, Letter> getUserInbox(String username) {
            return stub;
        }
    };

    @Before
    public void setUp() {
        Letter letter = new Letter();
        letter.setContent("content");
        stub.put(1L, letter);
        mailbox = new Mailbox(stubLetter);
    }

    @Test
    public void getUserDrafts() {
        HashMap<Long, Letter> actual = mailbox.getUserDrafts("doesn't matter");
        assertEquals(stub, actual);
    }

    @Test
    public void getUserInbox() {
        HashMap<Long, Letter> actual = mailbox.getUserDrafts("doesn't matter");
        assertEquals(stub, actual);
    }
}