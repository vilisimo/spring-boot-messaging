package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDataSource;
import lt.inventi.messaging.domain.Draft;
import lt.inventi.messaging.domain.Message;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MailboxTest {
    private LetterDataSource dataSource;

    @Test
    public void getUserDrafts_shouldReturnUserDrafts() {
        List<Draft> stub = new ArrayList<>();
        dataSource = mock(LetterDataSource.class);
        Draft letter = new Draft();
        letter.setContent("content");
        stub.add(letter);
        Mailbox mailbox = new Mailbox(dataSource);

        when(dataSource.getAllUserDrafts("test")).thenReturn(stub);
        List<Draft> actualDraft = mailbox.getUserDrafts("test");
        assertEquals(stub, actualDraft);
    }

    @Test
    public void getUserInbox_shouldReturnUserInbox() {
        List<Message> stub = new ArrayList<>();
        dataSource = mock(LetterDataSource.class);
        Message letter = new Message();
        letter.setContent("content");
        stub.add(letter);
        Mailbox mailbox = new Mailbox(dataSource);

        when(dataSource.getUserInbox("test")).thenReturn(stub);
        List<Message> actualInbox = mailbox.getUserInbox("test");
        assertEquals(stub, actualInbox);
    }
}