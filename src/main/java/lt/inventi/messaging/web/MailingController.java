package lt.inventi.messaging.web;

import jdk.nashorn.internal.ir.RuntimeNode;
import lt.inventi.messaging.domain.IdContainer;
import lt.inventi.messaging.domain.Letter;
import lt.inventi.messaging.mailing.Mailbox;
import lt.inventi.messaging.mailing.PostOffice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
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

    @RequestMapping(
            value="users/{username}/drafts",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HashMap<Long, Letter>> viewDrafts(@PathVariable("username") String username) {
        HashMap<Long, Letter> userDrafts = mailbox.getUserDrafts(username);
        if (userDrafts == null) {
            return new ResponseEntity<HashMap<Long, Letter>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<HashMap<Long, Letter>>(userDrafts, HttpStatus.OK);
    }

    @RequestMapping(
            value="users/{username}/drafts",
            method=RequestMethod.POST,
            consumes=MediaType.APPLICATION_JSON_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<IdContainer> saveDraft(@PathVariable("username") String username,
                                                 @RequestBody Letter letter) {
        Letter letterDraft = mailbox.saveDraft(username, letter);
        if (letterDraft == null) {
            return new ResponseEntity<IdContainer>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<IdContainer>(new IdContainer(letterDraft.getId()), HttpStatus.OK);
    }

    @RequestMapping(
            value="/users/{username}/drafts/{letterid}",
            method=RequestMethod.DELETE
    )
    public ResponseEntity<Letter> deleteDraft(@PathVariable("username") String username,
                                              @PathVariable("letterid") Long letterid) {
        boolean deleted = mailbox.deleteDraft(username, letterid);
        if (!deleted) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Letter>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(
            value="/users/{username}/drafts/{letterid}",
            method=RequestMethod.PUT,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> editDraft(@PathVariable("username") String username,
                                            @PathVariable("letterid") Long letterid,
                                            @RequestBody Letter letter) {
        boolean updated = mailbox.editDraft(username, letter, letterid);
        if (!updated) {
            return new ResponseEntity<Letter>(HttpStatus.I_AM_A_TEAPOT);
        }
        return new ResponseEntity<Letter>(HttpStatus.OK);
    }

    @RequestMapping(
            value="/users/{username}/inbox",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HashMap<Long, Letter>> viewInbox(@PathVariable("username") String username) {
        HashMap<Long, Letter> userInbox = mailbox.getUserInbox(username);
        if (userInbox == null) {
            return new ResponseEntity<HashMap<Long, Letter>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<HashMap<Long, Letter>>(userInbox, HttpStatus.OK);
    }

    // Note: does not send the letter if it doesn't exist in the drafts (after all, user specifies ID of the letter)
    // Should user be allowed to send a letter with non-existent ID? Or should he/she submit POST to
    // users/{username}/send instead?
    @RequestMapping(
            value="/users/{username}/send/{letterid}",
            method=RequestMethod.POST,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> sendLetter(@PathVariable("username") String username,
                                             @PathVariable("letterid") Long letterid,
                                             @RequestBody Letter letter) {
        boolean sent = postOffice.sendLetter(username, letter, letterid);
        if (!sent) {
            return new ResponseEntity<Letter>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Letter>(HttpStatus.OK);
    }

    @RequestMapping(
            value="/users/{username}/inbox/{letterid}/reply",
            method=RequestMethod.POST,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> replyToLetter(@PathVariable("username") String username,
                                                @PathVariable("letterid") Long letterid,
                                                @RequestBody Letter letter){
        Long replyID = Mailbox.getAndIncrementLetterID();
        boolean replied = postOffice.sendReply(username, letter, letterid, replyID);
        if (!replied) {
            return new ResponseEntity<Letter>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Letter>(HttpStatus.OK);
    }
}
