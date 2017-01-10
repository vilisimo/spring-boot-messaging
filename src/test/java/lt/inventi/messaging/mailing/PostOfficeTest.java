package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDataSource;
import lt.inventi.messaging.domain.Letter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostOfficeTest {
    @Mock
    private LetterDataSource mockDataSource;
    private PostOffice postOffice;

    @Before
    public void setUp() {
        postOffice = new PostOffice(mockDataSource);
    }

    @Test
    public void shouldSendLetter() {
        Letter letter = new Letter();
        HashMap<Long, Letter> stubHash = new HashMap<Long, Letter>();
        stubHash.put(1L, letter);
        when(mockDataSource.getUserDrafts("test")).thenReturn(stubHash);
        postOffice.sendLetter("test", 1L);

        verify(mockDataSource).removeDraftEntry("test", 1L);
        verify(mockDataSource).saveInboxEntry(letter);
    }

    @Test
    public void shouldSaveDraft() {
        String username = "test";
        Letter letter = new Letter();
        postOffice.saveDraft(username, letter);

        verify(mockDataSource).saveDraftEntry(letter);
        assertEquals(username, letter.getAuthor());
    }

    @Test
    public void shouldDeleteDraft() {
        postOffice.deleteDraft("test", 1L);
        verify(mockDataSource).removeDraftEntry("test", 1L);
    }

    @Test
    public void shouldEditDraft() {
        Letter letter = new Letter();
        String author = "test";
        Long id = 1L;
        postOffice.editDraft(author, letter, 1L);

        verify(mockDataSource).updateEntry(letter);
        assertEquals(id, letter.getId());
        assertEquals(author, letter.getAuthor());
    }

    @Test
    public void shouldSendReply() {
        Letter letter = new Letter();
        String sender = "repliesToLetter";
        String originalAuthor = "future_recipient";
        Long id = 1L;
        letter.setId(id);
        letter.setAuthor(originalAuthor);
        // sendReply calls sendLetter, which needs to get a letter from a database
        HashMap<Long, Letter> stubHash = new HashMap<Long, Letter>();
        stubHash.put(1L, letter);
        when(mockDataSource.getUserDrafts(sender)).thenReturn(stubHash);
        postOffice.sendReply(sender, letter);

        verify(mockDataSource).saveDraftEntry(letter);
        verify(mockDataSource).getUserDrafts(sender);
        verify(mockDataSource).removeDraftEntry(sender, id);
        verify(mockDataSource).saveInboxEntry(letter);
    }
}
