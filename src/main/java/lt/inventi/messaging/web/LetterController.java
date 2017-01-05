package lt.inventi.messaging.web;

import java.math.BigInteger;
import java.util.HashMap;

import lt.inventi.messaging.domain.Letter;
import lt.inventi.messaging.domain.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class LetterController {
    private static BigInteger letterId = BigInteger.ONE;
    private static HashMap<String, HashMap<BigInteger, Letter>> userDraftsMap =
            new HashMap<String, HashMap<BigInteger, Letter>>();  // HashMap of users. Each user has a HM of letters.
    private static HashMap<String, HashMap<BigInteger, Letter>> userInboxMap =
            new HashMap<String, HashMap<BigInteger, Letter>>(); // map of sent messages for each user.

    /**
     * ******* *
     * HELPERS *
     * ******* *
     */

    private static Letter sendUserLetter(String username, Letter letter) {
        Letter filledLetter = null;
        HashMap<BigInteger, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts != null) {
            filledLetter = userDrafts.remove(letter.getId());
        }

        if (filledLetter == null) {
            filledLetter = fillInLetter(username, letter);
        }
        String recipient = filledLetter.getRecipient();
        saveLetterToHashMap(recipient, letter, userInboxMap);

        return filledLetter;
    }

    private static void saveLetterToHashMap(String username, Letter letter,
                                              HashMap<String, HashMap<BigInteger, Letter>> hashMap) {
        HashMap<BigInteger, Letter> userLetters = hashMap.get(username);
        if (userLetters == null) {
            userLetters = new HashMap<BigInteger, Letter>();
            hashMap.put(username, userLetters);
        }
        userLetters.put(letter.getId(), letter);
    }

    private static boolean isInHashMap(String username, BigInteger letterid,
                                       HashMap<String, HashMap<BigInteger, Letter>> hashMap) {
        HashMap<BigInteger, Letter> userMessages = hashMap.get(username);
        if (userMessages == null) {
            return false;
        }

        Letter letter = userMessages.get(letterid);
        if (letter == null) {
            return false;
        }
        return true;
    }

    private static Letter fillInLetter(String username, Letter letter) {
        letter.setId(letterId);  // Make sure a letter has unique ID
        letterId = letterId.add(BigInteger.ONE);
        letter.setAuthor(username);  // Ensure draft's author is the same person as the one saving it.

        return letter;
    }

    private static Letter editLetter(String username, BigInteger letterid, Letter letter) {
        HashMap<BigInteger, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            return null;
        }

        Letter oldLetter = userDrafts.get(letterid);
        if (oldLetter != null) {
            userDrafts.remove(letterid);
            letter.setId(letterid);
            letter.setAuthor(username);
            userDrafts.put(letterid, letter);

            return letter;
        }
        Letter filledLetter = fillInLetter(username, letter);
        saveLetterToHashMap(username, letter, userDraftsMap);

        return filledLetter;
    }

    private static boolean deleteLetter(String username, BigInteger letterId) {
        HashMap<BigInteger, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            return false;
        }

        Letter deletedLetter = userDrafts.remove(letterId);

        return deletedLetter != null;
    }

    // TEMPORARY - just to quickly see how the data looks
    static {
        Letter draftInitial = new Letter();
        draftInitial.setContent("initial content");
        draftInitial.setRecipient("Two");
        Letter filledLetter = fillInLetter("one", draftInitial);
        saveLetterToHashMap("one", filledLetter, userDraftsMap);

        Letter draftInitial2 = new Letter();
        draftInitial2.setContent("initial content 2");
        draftInitial2.setRecipient("One");
        Letter filledLetter2 = fillInLetter("one", draftInitial2);
        saveLetterToHashMap("one", filledLetter2, userDraftsMap);
    }


    /**
     * ***************** *
     * MAPPINGS TO URLS  *
     * ***************** *
     */


    @RequestMapping(
            value="/users/{username}/inbox/{letterid}/reply",
            method=RequestMethod.POST,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> replyToLetter(@PathVariable("username") String username,
                                                @PathVariable("letterid") BigInteger letterid,
                                                @RequestBody Letter letter) {
        boolean letterInInbox = isInHashMap(username, letterid, userInboxMap);
        if (!letterInInbox) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        Letter originalLetter = userInboxMap.get(username).get(letterid);
        String recipient = originalLetter.getAuthor();
        letter.setRecipient(recipient);
        Letter reply = fillInLetter(username, letter);
        Letter sentLetter = sendUserLetter(username, reply);

        return new ResponseEntity<Letter>(HttpStatus.OK);
    }

    @RequestMapping(
            value="/users/{username}/send/{letterid}",
            method=RequestMethod.POST,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> sendLetter(@PathVariable("username") String username,
                                              @PathVariable("letterid") BigInteger letterid,
                                              @RequestBody Letter letter) {
        boolean inDrafts = isInHashMap(username, letterid, userDraftsMap);
        if (inDrafts) {
            editLetter(username, letterid, letter);
        }
        Letter sentLetter = sendUserLetter(username, letter);

        return new ResponseEntity<Letter>(HttpStatus.OK);
    }


    @RequestMapping(
            value="/users/{username}/inbox",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HashMap<BigInteger, Letter>> viewInbox(@PathVariable("username") String username) {
        HashMap<BigInteger, Letter> userReceivedMessages = userInboxMap.get(username);
        if (userReceivedMessages == null) {
            return new ResponseEntity<HashMap<BigInteger, Letter>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<HashMap<BigInteger, Letter>>(userReceivedMessages, HttpStatus.OK);
    }

    @RequestMapping(
            value="users/{username}/inbox/{letterid}",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> viewInboxLetter(@PathVariable("username") String username,
                                                   @PathVariable("letterid") BigInteger letterid) {
        HashMap<BigInteger, Letter> userInbox = userInboxMap.get(username);
        if (userInbox == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }
        Letter letter = userInbox.get(letterid);
        if (letter == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Letter>(letter, HttpStatus.OK);
    }

    @RequestMapping(
            value="/users/{username}/drafts",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HashMap<BigInteger, Letter>> viewDrafts(@PathVariable("username") String username) {
        HashMap<BigInteger, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            return new ResponseEntity<HashMap<BigInteger, Letter>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<HashMap<BigInteger, Letter>>(userDrafts, HttpStatus.OK);
    }

    @RequestMapping(
            value="/users/{username}/drafts/{letterid}",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> viewDraftLetter(@PathVariable("username") String username,
                                                  @PathVariable("letterid") BigInteger letterid) {
        HashMap<BigInteger, Letter> userLetters = userDraftsMap.get(username);
        if (userLetters == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        Letter letter = userDraftsMap.get(username).get(letterid);
        if (letter == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Letter>(letter, HttpStatus.OK);
    }

    @RequestMapping(
            value="/users/{username}/drafts",
            method=RequestMethod.POST,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> saveDraft(@PathVariable("username") String username,
                                              @RequestBody Letter letter) {
        if (letter.getRecipient() == null) {
            return new ResponseEntity<Response>(HttpStatus.BAD_REQUEST);
        }
        Letter filledLetter = fillInLetter(username, letter);
        saveLetterToHashMap(username, filledLetter, userDraftsMap);

        return new ResponseEntity<Response>(new Response(filledLetter.getId()), HttpStatus.OK);
    }

    @RequestMapping(
            value="/users/{username}/drafts/{letterid}",
            method=RequestMethod.DELETE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> deleteDraft(@PathVariable("username") String username,
                                              @PathVariable("letterid") BigInteger letterid) {
        boolean letterDeleted = deleteLetter(username, letterid);
        if (letterDeleted) {
            return new ResponseEntity<Letter>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(
            value="/users/{username}/drafts/{letterid}",
            method=RequestMethod.PUT,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> editDraft(@PathVariable("username") String username,
                                            @PathVariable("letterid") BigInteger letterid,
                                            @RequestBody Letter letter) {
        Letter editedLetter = editLetter(username, letterid, letter);
        if (editedLetter == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Letter>(HttpStatus.OK);
    }
}