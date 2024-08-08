package com.elliott.tworoomsandaboom.controller.card;

import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.dao.card.CardDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/two-rooms-and-a-boom/card")
public class CardController {
    private final CardDAO cardDAO;

    @Autowired
    public CardController(CardDAO cardDAO)
    {
        this.cardDAO = cardDAO;
    }

    @GetMapping()
    public ResponseEntity<Card> getCard(
            @RequestParam("playerId")
            int playerId
    )
    {
        log.info("Get Card: {}", playerId);
        Card card = cardDAO.getCard(playerId);
        return ResponseEntity.ok().body(card);
    }

    @GetMapping(value = "/cardImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getCardImage(
            @RequestParam("playerId")
            int playerId
    ) throws IOException
    {
        String filePath = cardDAO.getCardFilePath(playerId);
        final ByteArrayResource image = new ByteArrayResource(Files.readAllBytes(Paths.get(
                "src/main/resources/cards/" + filePath
        )));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(image.contentLength())
                .body(image);
    }
}
