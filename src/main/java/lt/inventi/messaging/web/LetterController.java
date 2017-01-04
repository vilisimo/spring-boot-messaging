package lt.inventi.messaging.web;

import java.math.BigInteger;
import java.util.HashMap;

import lt.inventi.messaging.domain.Letter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class LetterController {
    private static BigInteger letterId = BigInteger.ONE;
    private static BigInteger userId = BigInteger.ONE;
    private static HashMap<BigInteger, HashMap<BigInteger, Letter>> userMap =
            new HashMap<BigInteger, HashMap<BigInteger, Letter>>();  // HashMap of users. Each user has a HM of letters.

    private static Letter saveLetter(BigInteger userId, Letter letter) {
        HashMap<BigInteger, Letter> userLetters = userMap.get(userId);
        if (userLetters == null) {
            userLetters = new HashMap<BigInteger, Letter>();
            userMap.put(userId, userLetters);
        }

        // Creation of letters
        letter.setId(letterId);
        letterId = letterId.add(BigInteger.ONE);
        userLetters.put(letter.getId(), letter);

        return letter;
    }

    private static Letter editLetter(BigInteger userid, BigInteger letterid, Letter letter) {
        HashMap<BigInteger, Letter> userLetters = userMap.get(userid);
        if(userLetters == null) {
            return null;
        }

        Letter oldLetter = userLetters.get(letterid);
        if(oldLetter != null) {
            userLetters.remove(letterid);
            letter.setId(letterid);
            userLetters.put(letterid, letter);

            return letter;
        }
        saveLetter(userid, letter);

        return letter;
    }

    private static boolean deleteLetter(BigInteger userId, BigInteger letterId) {
        HashMap<BigInteger, Letter> userLetters = userMap.get(userId);
        if(userLetters == null) {
            return false;
        }

        Letter deletedLetter = userLetters.remove(letterId);

        return deletedLetter != null;
    }

    static {
        Letter initial = new Letter();
        initial.setContent("initial content");
        saveLetter(userId, initial);

        Letter initial2 = new Letter();
        initial2.setContent("initial content 2");
        saveLetter(userId, initial2);
    }

    // View all messages
    @RequestMapping(
            value="/users/{userid}/letters",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<HashMap<BigInteger, Letter>> viewAllLetters(@PathVariable("userid") BigInteger userid) {
        HashMap<BigInteger, Letter> userLetters = userMap.get(userid);
        if(userLetters == null) {
            return new ResponseEntity<HashMap<BigInteger, Letter>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<HashMap<BigInteger, Letter>>(userLetters, HttpStatus.OK);
    }

    // View a single message
    @RequestMapping(
            value="/users/{userid}/letters/{letterid}",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> viewUsereLetter(@PathVariable("userid") BigInteger userid,
                                                   @PathVariable("letterid") BigInteger letterid) {
        HashMap<BigInteger, Letter> userLetters = userMap.get(userid);
        if(userLetters == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        Letter letter = userMap.get(userid).get(letterid);
        if(letter == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Letter>(letter, HttpStatus.OK);
    }

    // Send message
    @RequestMapping(
            value="/users/{userid}/letters",
            method=RequestMethod.POST,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> sendLetter(@PathVariable("userid") BigInteger userid,
                                              @RequestBody Letter letter) {
        Letter sentLetter = saveLetter(userid, letter);

        return new ResponseEntity<Letter>(sentLetter, HttpStatus.OK);
    }

    // Delete message
    @RequestMapping(
            value="/users/{userid}/letters/{letterid}",
            method=RequestMethod.DELETE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> deleteUserLetter(@PathVariable("userid") BigInteger userid,
                                                    @PathVariable("letterid") BigInteger letterid) {
        boolean letterDeleted = deleteLetter(userid, letterid);
        if(letterDeleted) {
            return new ResponseEntity<Letter>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(
            value="/users/{userid}/letters/{letterid}",
            method=RequestMethod.PUT,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> editUserLetter(@PathVariable("userid") BigInteger userid,
                                                  @PathVariable("letterid") BigInteger letterid,
                                                  @RequestBody Letter letter) {
        Letter editedLetter = editLetter(userid, letterid, letter);
        if(editedLetter == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Letter>(editedLetter, HttpStatus.OK);
    }

    // Fill in
    private ResponseEntity<Letter> replyToLetter() {
        return null;
    }

    // Fill in
    private ResponseEntity<Letter> createLetter() {
        return null;
    }
}