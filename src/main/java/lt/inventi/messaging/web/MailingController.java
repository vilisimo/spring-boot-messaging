package lt.inventi.messaging.web;

import lt.inventi.messaging.domain.Letter;
import lt.inventi.messaging.mailing.Mailbox;
import lt.inventi.messaging.mailing.PostOffice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;


@RestController
public class MailingController {
    private final Mailbox mailbox;
    private final PostOffice postOffice;

    @Autowired
    public MailingController(Mailbox mailbox, PostOffice postOffice) {
        this.mailbox = mailbox;
        this.postOffice = postOffice;
    }

    @GetMapping(path="/users/{username}/drafts", produces=MediaType.APPLICATION_JSON_VALUE)
    public HashMap<Long, Letter> viewDrafts(@PathVariable("username") String username) {
        return mailbox.getUserDrafts(username);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value="users/{username}/drafts", consumes=MediaType.APPLICATION_JSON_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public void saveDraft(@PathVariable("username") String username,
                          @RequestBody @Valid Letter letter) {
        postOffice.saveDraft(username, letter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value="/users/{username}/drafts/{letterid}")
    public void deleteDraft(@PathVariable("username") String username,
                            @PathVariable("letterid") Long letterid) {
        postOffice.deleteDraft(username, letterid);
    }

    @PutMapping(value="/users/{username}/drafts/{letterid}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public void editDraft(@PathVariable("username") String username,
                          @PathVariable("letterid") Long letterid,
                          @RequestBody @Valid Letter letter) {
        postOffice.editDraft(username, letter, letterid);
    }

    @GetMapping(value="/users/{username}/inbox", produces=MediaType.APPLICATION_JSON_VALUE)
    public HashMap<Long, Letter> viewInbox(@PathVariable("username") String username) {
        return mailbox.getUserInbox(username);
    }

    @PostMapping(value="/users/{username}/drafts/{letterid}")
    public void sendLetter(@PathVariable("username") String username,
                           @PathVariable("letterid") Long letterid) {
        postOffice.sendLetter(username, letterid);
    }

    // /reply needed? Perhaps better to send POST request to users/{username}/inbox/{letterid}?
    @PostMapping(value="/users/{username}/inbox/{letterid}/reply", consumes=MediaType.APPLICATION_JSON_VALUE)
    public void replyToLetter(@PathVariable("username") String username,
                              @PathVariable("letterid") Long letterid,
                              @RequestBody Letter letter){
        postOffice.sendReply(username, letter);
    }
}
