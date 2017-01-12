package lt.inventi.messaging.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.inventi.messaging.domain.Draft;
import lt.inventi.messaging.domain.Message;
import lt.inventi.messaging.exceptions.ResourceNotFoundException;
import lt.inventi.messaging.mailing.Mailbox;
import lt.inventi.messaging.mailing.PostOffice;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
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

    @Captor
    private ArgumentCaptor<Draft> letterCaptor;

    private static final String TEST_USERNAME = "test-user";
    private static final String TEST_RECIPIENT = "test content";
    private static final String TEST_CONTENT = "test-recipient";


    @Test
    public void testViewDrafts_shouldReturnEmptyListOfDraftsAnd200() throws Exception {
        given(mailbox.getUserDrafts(TEST_USERNAME)).willReturn(new ArrayList<Draft>());
        this.mvc.perform(get("/users/{username}/drafts", TEST_USERNAME)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void testViewDrafts_shouldReturnNonEmptyListOfDraftsAnd200() throws Exception {
        List<Draft> mockDrafts = new ArrayList<Draft>();
        mockDrafts.add(new Draft());
        String stringMockedDrafts = objectMapper.writeValueAsString(mockDrafts);
        given(mailbox.getUserDrafts(TEST_USERNAME)).willReturn(mockDrafts);
        this.mvc.perform(get("/users/{username}/drafts", TEST_USERNAME)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(stringMockedDrafts));
    }

    @Test
    public void testSaveDraft_shouldSaveDraftLetterAndReturn201() throws Exception {
        JSONObject json = new JSONObject();
        json.put("recipient", TEST_RECIPIENT);
        json.put("content", TEST_CONTENT);
        this.mvc.perform(post("/users/{username}/drafts", TEST_USERNAME)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json.toString()))
                .andExpect(status().isCreated());
        verify(postOffice).saveDraft(eq(TEST_USERNAME), letterCaptor.capture());
        Draft capturedLetter = letterCaptor.getValue();
        assertEquals(TEST_RECIPIENT, capturedLetter.getRecipient());
        assertEquals(TEST_CONTENT, capturedLetter.getContent());
    }

    @Test
    public void testDeleteDraft_shouldDeleteExistingDraftAndReturn204() throws Exception {
        long letterID = 1L;
        this.mvc.perform(delete("/users/{username}/drafts/{letterID}", TEST_USERNAME, letterID))
                .andExpect(status().isNoContent());
        verify(postOffice).deleteDraft(TEST_USERNAME, letterID);
    }

    @Test
    public void testDeleteDraft_shouldThrow404WhenUserNotFound() throws Exception {
        long letterID = 1L;
        doThrow(new ResourceNotFoundException()).when(postOffice).deleteDraft(TEST_USERNAME, letterID);
        this.mvc.perform(delete("/users/{username}/drafts/{letterID}", TEST_USERNAME, letterID))
                .andExpect(status().isNotFound());
        verify(postOffice).deleteDraft(TEST_USERNAME, letterID);
    }

    @Test
    public void testDeleteDraft_shouldThrow404WhenUserHasNoDraft() throws Exception {
        long letterID = 1L;
        doThrow(new ResourceNotFoundException()).when(postOffice).deleteDraft(TEST_USERNAME, letterID);
        this.mvc.perform(delete("/users/{username}/drafts/{letterID}", TEST_USERNAME, letterID))
                .andExpect(status().isNotFound());
        verify(postOffice).deleteDraft(TEST_USERNAME, letterID);
    }

    @Test
    public void testEditDraft_shouldThrow200WhenDraftIsPresent() throws Exception {
        long letterID = 1L;
        JSONObject json = new JSONObject();
        json.put("recipient", TEST_RECIPIENT);
        json.put("content", TEST_CONTENT);
        this.mvc.perform(put("/users/{username}/drafts/{letterID}", TEST_USERNAME, letterID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json.toString()))
                .andExpect(status().isOk());
        verify(postOffice).editDraft(eq(TEST_USERNAME), letterCaptor.capture(), eq(letterID));
        Draft capturedLetter = letterCaptor.getValue();
        assertEquals(TEST_RECIPIENT, capturedLetter.getRecipient());
        assertEquals(TEST_CONTENT, capturedLetter.getContent());
    }

    @Test
    public void testEditDraft_shouldThrow404WhenDraftLetterIsNotPresent() throws Exception {
        long letterID = 1L;
        JSONObject json = new JSONObject();
        json.put("recipient", TEST_RECIPIENT);
        json.put("content", TEST_CONTENT);
        doThrow(new ResourceNotFoundException()).when(postOffice)
                                                .editDraft(eq(TEST_USERNAME), any(Draft.class), eq(letterID));
        this.mvc.perform(put("/users/{username}/drafts/{letterID}", TEST_USERNAME, letterID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json.toString()))
                .andExpect(status().isNotFound());
        verify(postOffice).editDraft(eq(TEST_USERNAME), letterCaptor.capture(), eq(letterID));
        Draft capturedLetter = letterCaptor.getValue();
        assertEquals(TEST_RECIPIENT, capturedLetter.getRecipient());
        assertEquals(TEST_CONTENT, capturedLetter.getContent());
    }

    @Test
    public void testViewInbox_shouldShowEmptyUserInbox() throws Exception {
        given(this.mailbox.getUserInbox(TEST_USERNAME)).willReturn(new ArrayList<>());
        this.mvc.perform(get("/users/{username}/inbox", TEST_USERNAME)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void testViewInbox_shouldShowNonEmptyUserInbox() throws Exception {
        List<Message> mockInbox = new ArrayList<>();
        mockInbox.add(new Message());
        String stringMockedInbox = objectMapper.writeValueAsString(mockInbox);
        given(this.mailbox.getUserInbox(TEST_USERNAME)).willReturn(mockInbox);
        this.mvc.perform(get("/users/{username}/inbox", TEST_USERNAME)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(stringMockedInbox));
    }

    @Test
    public void testSendLetter_shouldSendLetterAndReturn200() throws Exception {
        long letterID = 1L;
        this.mvc.perform(post("/users/{username}/drafts/{letterID}/dispatcher", TEST_USERNAME, letterID))
                .andExpect(status().isOk());
        verify(postOffice).sendLetter(TEST_USERNAME, letterID);
    }

    @Test
    public void testSendLetter_shouldThrow404WhenLetterNotFound() throws Exception {
        long letterID = 1L;
        doThrow(new ResourceNotFoundException()).when(postOffice).sendLetter(TEST_USERNAME, letterID);
        this.mvc.perform(post("/users/{username}/drafts/{lettersID}/dispatcher", TEST_USERNAME, letterID))
                .andExpect(status().isNotFound());
        verify(postOffice).sendLetter(TEST_USERNAME, letterID);
    }

    @Test
    public void testSendLetter_shouldThrow404WhenUserDraftsAreNotFound() throws Exception {
        long letterID = 1L;
        doThrow(new ResourceNotFoundException()).when(postOffice).sendLetter(TEST_USERNAME, letterID);
        this.mvc.perform(post("/users/{username}/drafts/{lettersID}/dispatcher", TEST_USERNAME, letterID))
                .andExpect(status().isNotFound());
        verify(postOffice).sendLetter(TEST_USERNAME, letterID);
    }

    @Test
    public void testReplyToLetter_shouldReplyToLetterAndReturn200() throws Exception {
        long letterID = 1L;
        JSONObject json = new JSONObject();
        json.put("recipient", TEST_RECIPIENT);
        json.put("content", TEST_CONTENT);
        this.mvc.perform(post("/users/{username}/inbox/{letterid}/reply", TEST_USERNAME, letterID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json.toString()))
                .andExpect(status().isOk());
        verify(postOffice).sendReply(eq(TEST_USERNAME), letterCaptor.capture());
        Draft capturedLetter = letterCaptor.getValue();
        assertEquals(TEST_RECIPIENT, capturedLetter.getRecipient());
        assertEquals(TEST_CONTENT, capturedLetter.getContent());
    }

    @Test
    public void testReplyToLetter_shouldThrow404WhenLetterDoesNotExist() throws Exception {
        long letterID = 1L;
        JSONObject json = new JSONObject();
        json.put("recipient", TEST_RECIPIENT);
        json.put("content", TEST_CONTENT);
        doThrow(new ResourceNotFoundException()).when(postOffice).sendReply(eq(TEST_USERNAME), any(Draft.class));
        this.mvc.perform(post("/users/{username}/inbox/{letterid}/reply", TEST_USERNAME, letterID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json.toString()))
                .andExpect(status().isNotFound());
        verify(postOffice).sendReply(eq(TEST_USERNAME), letterCaptor.capture());
        Draft capturedLetter = letterCaptor.getValue();
        assertEquals(TEST_RECIPIENT, capturedLetter.getRecipient());
        assertEquals(TEST_CONTENT, capturedLetter.getContent());
    }

    @Test
    public void testReplyToLetter_shouldThrow404WhenUserDraftsDoNotExist() throws Exception {
        long letterID = 1L;
        JSONObject json = new JSONObject();
        json.put("recipient", TEST_RECIPIENT);
        json.put("content", TEST_CONTENT);
        doThrow(new ResourceNotFoundException()).when(postOffice).sendReply(eq(TEST_USERNAME), any(Draft.class));
        this.mvc.perform(post("/users/{username}/inbox/{letterid}/reply", TEST_USERNAME, letterID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json.toString()))
                .andExpect(status().isNotFound());
        verify(postOffice).sendReply(eq(TEST_USERNAME), letterCaptor.capture());
        Draft capturedLetter = letterCaptor.getValue();
        assertEquals(TEST_RECIPIENT, capturedLetter.getRecipient());
        assertEquals(TEST_CONTENT, capturedLetter.getContent());
    }
}
