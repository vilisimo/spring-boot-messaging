package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDataSource;
import lt.inventi.messaging.domain.Draft;
import lt.inventi.messaging.domain.Message;
import lt.inventi.messaging.exceptions.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostOfficeTest {
    @Mock
    private LetterDataSource mockDataSource;
    private PostOffice postOffice;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;
    @Captor
    private ArgumentCaptor<Draft> draftCaptor;

    private static final String TEST_AUTHOR = "test-author";
    private static final String TEST_CONTENT = "test content";
    private static final String TEST_RECIPIENT = "test-recipient";
    private static final long TEST_ID = 1L;

    @Before
    public void setUp() {
        postOffice = new PostOffice(mockDataSource);
    }

    @Test
    public void sendLetter_shouldSendLetter() {
        Draft draft = new Draft();
        draft.setAuthor(TEST_AUTHOR);
        draft.setContent(TEST_CONTENT);
        draft.setRecipient(TEST_RECIPIENT);
        draft.setId(TEST_ID);
        when(mockDataSource.getUserDraft(TEST_ID)).thenReturn(draft);
        postOffice.sendLetter(TEST_AUTHOR, TEST_ID);
        verify(mockDataSource).getUserDraft(TEST_ID);
        verify(mockDataSource).saveInboxEntry(messageCaptor.capture());
        Message message = messageCaptor.getValue();

        assertEquals(TEST_AUTHOR, message.getAuthor());
        assertEquals(TEST_CONTENT, message.getContent());
        assertEquals(TEST_RECIPIENT, message.getRecipient());
        assertEquals(Long.valueOf(TEST_ID), message.getId());
    }

    @Test
    public void saveDraftEntry_shouldSaveDraft() {
        String username = "test";
        Draft letter = new Draft();
        postOffice.saveDraft(username, letter);

        verify(mockDataSource).saveDraftEntry(letter);
        assertEquals(username, letter.getAuthor());
    }

    @Test
    public void removeDraftEntry_shouldDeleteDraft() {
        Draft stubDraft = new Draft();
        stubDraft.setAuthor(TEST_AUTHOR);
        stubDraft.setId(TEST_ID);
        when(mockDataSource.getUserDraft(TEST_ID)).thenReturn(stubDraft);
        postOffice.deleteDraft(TEST_AUTHOR, TEST_ID);
        verify(mockDataSource).removeDraftEntry(draftCaptor.capture());
        Draft deletedDraft = draftCaptor.getValue();
        assertEquals(TEST_AUTHOR, deletedDraft.getAuthor());
        assertEquals(Long.valueOf(TEST_ID), deletedDraft.getId());
    }

    @Test
    public void updateEntry_shouldUpdateDraft() {
        Draft letter = new Draft();
        when(mockDataSource.draftExists(TEST_ID)).thenReturn(true);
        postOffice.editDraft(TEST_AUTHOR, letter, TEST_ID);
        verify(mockDataSource).updateEntry(letter);
        assertEquals(Long.valueOf(TEST_ID), letter.getId());
        assertEquals(TEST_AUTHOR, letter.getAuthor());
    }

    @Test
    public void sendReply_shouldSetRecipient() {
        Draft draft = new Draft();
        draft.setAuthor(TEST_AUTHOR);
        postOffice.saveDraft(TEST_AUTHOR, draft);
        verify(mockDataSource).saveDraftEntry(draftCaptor.capture());
        assertEquals(TEST_AUTHOR, draftCaptor.getValue().getAuthor());
    }

    @Test
    public void sendReply_shouldSendReply() {
        Draft draft = new Draft();
        draft.setAuthor(TEST_AUTHOR);
        draft.setRecipient(TEST_RECIPIENT);
        draft.setId(TEST_ID);
        when(mockDataSource.getUserDraft(TEST_ID)).thenReturn(draft);
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        when(mockDataSource.getUserInboxMessage(TEST_ID)).thenReturn(message);
        postOffice.sendReply(TEST_RECIPIENT, TEST_ID, draft);
        verify(mockDataSource).saveInboxEntry(messageCaptor.capture());

        message = messageCaptor.getValue();
        assertEquals(TEST_RECIPIENT, message.getAuthor());
        assertEquals(TEST_AUTHOR, message.getRecipient());
        assertEquals(Long.valueOf(TEST_ID), message.getId());
    }

    @Test(expected= ResourceNotFoundException.class)
    public void sendReply_shouldThrow404WhenInboxEntryNotFound() {
        Draft draft = new Draft();
        when(mockDataSource.getUserInboxMessage(TEST_ID)).thenReturn(null);
        postOffice.sendReply(TEST_RECIPIENT, TEST_ID, draft);
    }
}
