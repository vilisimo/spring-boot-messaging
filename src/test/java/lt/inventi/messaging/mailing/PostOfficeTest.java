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
    Letter letter;

    @Before
    public void setUp() {
        postOffice = new PostOffice(mockDataSource);
        letter = new Letter();
    }

    @Test
    public void shouldSendLetter() {
        letter = new Letter();
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
        letter = new Letter();
        postOffice.saveDraft(username, letter);

        verify(mockDataSource).saveDraftEntry(letter);
        assertEquals(username, letter.getAuthor());
    }

    @Test
    public void shouldDeleteDraft() {
        letter = new Letter();
        postOffice.deleteDraft("test", 1L);

        verify(mockDataSource).removeDraftEntry("test", 1L);
    }

    @Test
    public void shouldEditDraft() {
        letter = new Letter();
        String author = "test";
        Long id = 1L;
        postOffice.editDraft(author, letter, id);

        verify(mockDataSource).updateEntry(letter);
        assertEquals(id, letter.getId());
        assertEquals(author, letter.getAuthor());
    }

    @Test
    public void shouldSendReply() {
        letter = new Letter();
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
