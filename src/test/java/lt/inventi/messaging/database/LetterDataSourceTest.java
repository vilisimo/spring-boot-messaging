package lt.inventi.messaging.database;

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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LetterDataSourceTest {
    private LetterDataSource db;
    private static final String TEST_AUTHOR = "test-author";
    private static final String TEST_RECIPIENT = "test-recipient";
    private static final Long TEST_ID = 1L;

    @Mock
    private DraftRepository draftRepository;
    @Mock
    private InboxRepository inboxRepository;
    @Captor
    private ArgumentCaptor<Draft> draftCaptor;

    @Before
    public void setUp() throws Exception {
        db = new LetterDataSource(draftRepository, inboxRepository);
    }

    @Test
    public void getAllUserDrafts_shouldReturnNonEmptyUserDrafts() {
        List<Draft> stubDrafts = new ArrayList<>();
        Draft draft = new Draft();
        draft.setAuthor(TEST_AUTHOR);
        draft.setRecipient(TEST_RECIPIENT);
        draft.setId(TEST_ID);
        stubDrafts.add(draft);

        when(draftRepository.findLettersByAuthor(TEST_AUTHOR)).thenReturn(stubDrafts);
        List<Draft> actualDrafts = db.getAllUserDrafts(TEST_AUTHOR);
        assertEquals(stubDrafts, actualDrafts);
    }

    @Test
    public void getUserDrafts_ShouldCreateEmptyDraftWhenNoDraftFound() {
        when(draftRepository.findLettersByAuthor(TEST_AUTHOR)).thenReturn(new ArrayList<>());
        List<Draft> emptyDraftList = db.getAllUserDrafts(TEST_AUTHOR);
        assertTrue(emptyDraftList.isEmpty());
    }

    @Test
    public void getUserInbox_shouldReturnNonEmptyUserInbox() throws Exception {
        List<Message> stubInbox = new ArrayList<>();
        Message message = new Message();
        message.setAuthor(TEST_AUTHOR);
        message.setRecipient(TEST_RECIPIENT);
        message.setId(TEST_ID);
        stubInbox.add(message);

        when(inboxRepository.findLettersByRecipient(TEST_AUTHOR)).thenReturn(stubInbox);
        List<Message> actualInbox = db.getUserInbox(TEST_AUTHOR);
        assertEquals(stubInbox, actualInbox);
    }

    @Test
    public void getUserDrafts_shouldCreateEmptyInboxWhenNoDraftFound() {
        when(inboxRepository.findLettersByRecipient(TEST_AUTHOR)).thenReturn(new ArrayList<>());
        List<Message> emptyDraftList = db.getUserInbox(TEST_AUTHOR);
        assertTrue(emptyDraftList.isEmpty());
    }

    @Test
    public void saveDraftEntry_shouldAssignIDToLetter() {
        Draft draft = new Draft();
        draft.setAuthor(TEST_AUTHOR);
        db.saveDraftEntry(draft);
        assertEquals(Long.valueOf(1), draft.getId());

        Draft draft2 = new Draft();
        draft2.setAuthor(TEST_AUTHOR);
        db.saveDraftEntry(draft2);
        assertEquals(Long.valueOf(2), draft2.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void removeDraftEntry_shouldThrow404WhenIDNotFound() {
        when(draftRepository.exists(any(Long.class))).thenReturn(false);
        db.removeDraftEntry(new Draft());
    }

    @Test
    public void removeDraftEntry_shouldRemoveDraftEntryWhenItExits() {
        Draft draft = new Draft();
        draft.setAuthor(TEST_AUTHOR);
        draft.setRecipient(TEST_RECIPIENT);
        db.saveDraftEntry(draft);
        verify(draftRepository).save(draftCaptor.capture());
        Draft capturedDraft = draftCaptor.getValue();
        assertEquals(draft.getAuthor(), capturedDraft.getAuthor());
        assertEquals(draft.getRecipient(), capturedDraft.getRecipient());
        assertEquals(Long.valueOf(1), capturedDraft.getId());
    }

    @Test
    public void updateEntry_shouldUpdateEntry() {
        Draft draft = new Draft();
        draft.setAuthor(TEST_AUTHOR);
        draft.setRecipient(TEST_RECIPIENT);
        draft.setId(TEST_ID);
        db.updateEntry(draft);
        verify(draftRepository).save(draftCaptor.capture());
        Draft capturedDraft = draftCaptor.getValue();
        assertEquals(draft.getAuthor(), capturedDraft.getAuthor());
        assertEquals(draft.getRecipient(), capturedDraft.getRecipient());
        assertEquals(Long.valueOf(1), capturedDraft.getId());
    }

    @Test
    public void draftExists_shouldReturnTrueWhenDraftExists() {
        when(draftRepository.exists(TEST_ID)).thenReturn(true);
        assertTrue(db.draftExists(TEST_ID));
    }

    @Test
    public void draftExists_shouldReturnFalseWhenDraftDoesNotExist() {
        when(draftRepository.exists(TEST_ID)).thenReturn(false);
        assertFalse(db.draftExists(TEST_ID));
    }
}