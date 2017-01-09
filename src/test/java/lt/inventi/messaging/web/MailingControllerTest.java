package lt.inventi.messaging.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.inventi.messaging.domain.Letter;
import lt.inventi.messaging.exceptions.DraftNotFoundException;
import lt.inventi.messaging.mailing.Mailbox;
import lt.inventi.messaging.mailing.PostOffice;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(MailingController.class)
public class MailingControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private Mailbox mailbox;
    @MockBean
    private PostOffice postOffice;

    private String username;

    @Before
    public void setUp() {
        username = "test";
    }

    @Test
    public void testViewDrafts_shouldReturnDrafts() throws Exception {
        HashMap<Long, Letter> mockHashMap = new HashMap<Long, Letter>();
        given(this.mailbox.getUserDrafts(username)).willReturn(mockHashMap);
        this.mvc.perform(get("/users/{username}/drafts", username)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    public void testSaveDraft_shouldSaveDraft() throws Exception {
        Letter stubLetter = new Letter();
        stubLetter.setRecipient(username);
        stubLetter.setContent("test");
        String json = objectMapper.writeValueAsString(stubLetter);
        doNothing().when(postOffice).saveDraft(username, stubLetter);
        this.mvc.perform(post("/users/{username}/drafts", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json))
                .andExpect(status().isOk());
        verify(postOffice).saveDraft(eq(username), isA(Letter.class)); // deserialized object different?
    }

    @Test
    public void testDeleteDraft_shouldDeleteExistingDraft() throws Exception {
        Long letterID = 1L;
        doNothing().when(postOffice).deleteDraft(username, letterID);
        this.mvc.perform(delete("/users/{username}/drafts/{letterid}", username, letterID))
                .andExpect(status().isOk());
        verify(postOffice).deleteDraft(username, letterID);
    }

    @Test
    public void testDeleteDraft_shouldThrow404WhenUserNotFound() throws Exception {
        Long letterID = 1L;
        doThrow(new DraftNotFoundException()).when(postOffice).deleteDraft(username, letterID);
        this.mvc.perform(delete("/users/{username}/drafts/{letterid}", username, letterID))
                .andExpect(status().isNotFound());
        verify(postOffice).deleteDraft(username, letterID);
    }

    // @Test
    // public void testViewDraft_shouldReturn404IfUserDoesNotExist() throws Exception {
    //     String username = "test";
    //     Letter stubLetter = new Letter();
    //     stubLetter.setRecipient(username);
    //     stubLetter.setContent("test");
    //     String json = objectMapper.writeValueAsString(stubLetter);
    //     doThrow(new DraftNotFoundException()).when(this.postOffice).saveDraft(eq("test"), any(Letter.class));
    //     this.mvc.perform(
    //                 post("/users/{username}/drafts", username)
    //                         .contentType(MediaType.APPLICATION_JSON_VALUE)
    //                         .content(json))
    //             .andExpect(status().isNotFound());
    // }

    // @Test

}
