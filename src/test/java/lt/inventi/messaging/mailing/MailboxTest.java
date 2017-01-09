package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDataSource;
import lt.inventi.messaging.domain.Letter;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MailboxTest {
    private Mailbox mailbox;
    private LetterDataSource dataSource;
    private HashMap<Long, Letter> stub;

    @Before
    public void setUp() {
        stub = new HashMap<Long, Letter>();
        dataSource = mock(LetterDataSource.class);
        Letter letter = new Letter();
        letter.setContent("content");
        stub.put(1L, letter);
        mailbox = new Mailbox(dataSource);
    }

    @Test
    public void getUserDrafts() {
        when(dataSource.getUserDrafts("test")).thenReturn(stub);
        HashMap<Long, Letter> actualDraft = mailbox.getUserDrafts("test");
        assertEquals(stub, actualDraft);
    }

    @Test
    public void getUserInbox() {
        when(dataSource.getUserInbox("test")).thenReturn(stub);
        HashMap<Long, Letter> actualInbox = mailbox.getUserInbox("test");
        assertEquals(stub, actualInbox);
    }
}