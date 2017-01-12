// package lt.inventi.messaging.mailing;
//
// import lt.inventi.messaging.database.LetterDataSource;
// import lt.inventi.messaging.domain.Draft;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.Mock;
// import org.mockito.runners.MockitoJUnitRunner;
//
// import java.util.HashMap;
//
// import static org.junit.Assert.assertEquals;
// import static org.mockito.Mockito.*;
//
// @RunWith(MockitoJUnitRunner.class)
// public class PostOfficeTest {
//     @Mock
//     private LetterDataSource mockDataSource;
//     private PostOffice postOffice;
//
//     @Before
//     public void setUp() {
//         postOffice = new PostOffice(mockDataSource);
//     }
//
//     @Test
//     public void sendLetter_shouldSendLetter() {
//         Draft letter = new Draft();
//         HashMap<Long, Draft> stubHash = new HashMap<Long, Draft>();
//         stubHash.put(1L, letter);
//         when(mockDataSource.getAllUserDrafts("test")).thenReturn(stubHash);
//         postOffice.sendLetter("test", 1L);
//
//         verify(mockDataSource).removeDraftEntry("test", 1L);
//         verify(mockDataSource).saveInboxEntry(letter);
//     }
//
//     @Test
//     public void saveDraftEntry_shouldSaveDraft() {
//         String username = "test";
//         Draft letter = new Draft();
//         postOffice.saveDraft(username, letter);
//
//         verify(mockDataSource).saveDraftEntry(letter);
//         assertEquals(username, letter.getAuthor());
//     }
//
//     @Test
//     public void removeDraftEntry_shouldDeleteDraft() {
//         postOffice.deleteDraft("test", 1L);
//         verify(mockDataSource).removeDraftEntry("test", 1L);
//     }
//
//     @Test
//     public void updateEntry_shouldUpdateDraft() {
//         Draft letter = new Draft();
//         String author = "test";
//         Long id = 1L;
//         postOffice.editDraft(author, letter, 1L);
//
//         verify(mockDataSource).updateEntry(letter);
//         assertEquals(id, letter.getId());
//         assertEquals(author, letter.getAuthor());
//     }
//
//     @Test
//     public void sendReply_shouldSendReply() {
//         Draft letter = new Draft();
//         String sender = "repliesToLetter";
//         String originalAuthor = "future_recipient";
//         Long id = 1L;
//         letter.setId(id);
//         letter.setAuthor(originalAuthor);
//         // sendReply calls sendLetter, which needs to get a letter from a database
//         HashMap<Long, Draft> stubHash = new HashMap<Long, Draft>();
//         stubHash.put(1L, letter);
//         when(mockDataSource.getAllUserDrafts(sender)).thenReturn(stubHash);
//         postOffice.sendReply(sender, letter);
//
//         verify(mockDataSource).saveDraftEntry(letter);
//         verify(mockDataSource).getAllUserDrafts(sender);
//         verify(mockDataSource).removeDraftEntry(sender, id);
//         verify(mockDataSource).saveInboxEntry(letter);
//     }
// }
