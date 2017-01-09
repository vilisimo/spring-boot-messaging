package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDataSource;
import lt.inventi.messaging.domain.Letter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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

    // @Test
    // public void shouldSendLetter() {
    //     letter = new Letter();
    //     when(mockDataSource.removeDraftEntry("test", 1L)).thenReturn(letter);
    //     when(mockDataSource.saveInboxEntry(letter)).thenReturn(letter);
    //     postOffice.sendLetter("test", 1L);
    //
    //     verify(mockDataSource, times(1)).removeDraftEntry("test", 1L);
    //     verify(mockDataSource, times(1)).saveInboxEntry(letter);
    // }

    // @Test
    // public void shouldSaveDraft() {
    //     String username = "test";
    //     Long expectedID = 1L;
    //     letter = new Letter();
    //     letter.setId(expectedID);
    //     when(mockDataSource.saveDraftEntry(letter)).thenReturn(letter);
    //     Long actualID = postOffice.saveDraft(username, letter);
    //
    //     verify(mockDataSource, times(1)).saveDraftEntry(letter);
    //     assertEquals(expectedID, actualID);
    // }

    // @Test
    // public void shouldDeleteDraft() {
    //     letter = new Letter();
    //     letter.setId(1L);
    //     when(mockDataSource.removeDraftEntry("test", 1L)).thenReturn(letter);
    //     postOffice.deleteDraft("test", 1L);
    //
    //     verify(mockDataSource, times(1)).removeDraftEntry("test", 1L);
    // }

    @Test
    public void shouldEditDraft() {
        letter = new Letter();
        String author = "test";
        Long id = 1L;
        postOffice.editDraft(author, letter, id);

        verify(mockDataSource, times(1)).updateEntry(letter);
        assertEquals(id, letter.getId());
        assertEquals(author, letter.getAuthor());
    }

    // @Test
    // public void shouldSendReply() {
    //     // Set up all the required data
    //     letter = new Letter();
    //     String futureRecipient = "future_recipient";  // toLowerCase() called when author is set.
    //     String sender = "repliesToLetter";
    //     Long id = 1L;
    //     letter.setId(id);
    //     letter.setAuthor(futureRecipient);
    //     // Mock what's needed and actuallly call methods
    //     when(mockDataSource.removeDraftEntry(sender, id)).thenReturn(letter);
    //     when(mockDataSource.saveDraftEntry(letter)).thenReturn(letter);
    //     when(mockDataSource.saveInboxEntry(letter)).thenReturn(letter);
    //     postOffice.sendReply(sender, letter);
    //
    //     verify(mockDataSource, times(1)).removeDraftEntry(sender, id);
    //     verify(mockDataSource, times(1)).saveDraftEntry(letter);
    //     verify(mockDataSource, times(1)).saveInboxEntry(letter);
    //     assertEquals(letter.getRecipient(), futureRecipient);
    //     assertEquals(letter.getAuthor(), sender);
    // }
}
