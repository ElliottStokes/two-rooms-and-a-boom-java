package com.elliott.tworoomsandaboom.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elliott.tworoomsandaboom.card.BasicCards;
import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.error.GameRuleException;
import com.elliott.tworoomsandaboom.player.Player;
import com.elliott.tworoomsandaboom.util.CardConstants;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameOperationsTest
{
    private static GameOperations gameOperations;
    private static BasicCards basicCards;

    @BeforeAll
    static void setUp()
    {
        gameOperations = new GameOperations();
        List<Card> cards = new ArrayList<>();
        cards.add(CardConstants.PRESIDENT_CARD);
        cards.add(CardConstants.BOMBER_CARD);
        cards.add(CardConstants.BLUE_TEAM_CARD);
        cards.add(CardConstants.RED_TEAM_CARD);
        cards.add(CardConstants.GAMBLER_CARD);
        basicCards = new BasicCards(cards);
    }

    @Test
    void shouldAssignBasicCards()
    {
        Player[] players = new Player[] {
                new Player(1, "testPlayer1"),
                new Player(2, "testPlayer2"),
                new Player(3, "testPlayer3"),
                new Player(4, "testPlayer4"),
                new Player(5, "testPlayer5"),
                new Player(6, "testPlayer6")
        };
        Map<Player, Card> assignedCards = gameOperations.dealCards(players, basicCards, new ArrayList<>());

        assertTrue(assignedCards.containsValue(CardConstants.PRESIDENT_CARD));
        assertTrue(assignedCards.containsValue(CardConstants.BOMBER_CARD));
        long blueTeamMembers = countCards(assignedCards, CardConstants.BLUE_TEAM_CARD);
        long redTeamMembers = countCards(assignedCards, CardConstants.RED_TEAM_CARD);
        assertEquals(2, blueTeamMembers);
        assertEquals(2, redTeamMembers);
        assertFalse(assignedCards.containsValue(CardConstants.GAMBLER_CARD));
    }

    @Test
    void shouldAssignBasicCardsWithGamblerForOddNumberOfPlayers()
    {
        Player[] players = new Player[] {
                new Player(1, "testPlayer1"),
                new Player(2, "testPlayer2"),
                new Player(3, "testPlayer3"),
                new Player(4, "testPlayer4"),
                new Player(5, "testPlayer5"),
                new Player(6, "testPlayer6"),
                new Player(7, "testPlayer7")
        };
        Map<Player, Card> assignedCards = gameOperations.dealCards(players, basicCards, new ArrayList<>());

        assertTrue(assignedCards.containsValue(CardConstants.PRESIDENT_CARD));
        assertTrue(assignedCards.containsValue(CardConstants.BOMBER_CARD));
        long blueTeamMembers = countCards(assignedCards, CardConstants.BLUE_TEAM_CARD);
        long redTeamMembers = countCards(assignedCards, CardConstants.RED_TEAM_CARD);
        assertEquals(2, blueTeamMembers);
        assertEquals(2, redTeamMembers);
        assertTrue(assignedCards.containsValue(CardConstants.GAMBLER_CARD));
    }

    @Test
    void shouldThrowGameRuleExceptionWhenLessThanSixPlayersInGame()
    {
        Player[] players = new Player[] {
                new Player(1, "testPlayer1"),
                new Player(2, "testPlayer2"),
                new Player(3, "testPlayer3"),
                new Player(4, "testPlayer4"),
                new Player(5, "testPlayer5")
        };
        List<Card> emptyCardList = new ArrayList<>(0);
        assertThrows(GameRuleException.class, () -> gameOperations.dealCards(players, basicCards, emptyCardList));
    }

    @Test
    void shouldThrowGameRuleExceptionWhenMoreThanThirtyPlayersInGame()
    {
        Player[] players = new Player[] {
                new Player(1, "testPlayer1"), new Player(2, "testPlayer2"), new Player(3, "testPlayer3"),
                new Player(4, "testPlayer4"), new Player(5, "testPlayer5"), new Player(6, "testPlayer6"),
                new Player(7, "testPlayer7"), new Player(8, "testPlayer8"), new Player(9, "testPlayer9"),
                new Player(10, "testPlayer10"), new Player(11, "testPlayer11"), new Player(12, "testPlayer12"),
                new Player(13, "testPlayer13"), new Player(14, "testPlayer14"), new Player(15, "testPlayer15"),
                new Player(16, "testPlayer16"), new Player(17, "testPlayer17"), new Player(18, "testPlayer18"),
                new Player(19, "testPlayer19"), new Player(20, "testPlayer20"), new Player(21, "testPlayer21"),
                new Player(22, "testPlayer22"), new Player(23, "testPlayer23"), new Player(24, "testPlayer24"),
                new Player(25, "testPlayer25"), new Player(26, "testPlayer26"), new Player(27, "testPlayer27"),
                new Player(28, "testPlayer28"), new Player(29, "testPlayer29"), new Player(30, "testPlayer30"),
                new Player(31, "testPlayer31")
        };
        List<Card> emptyCardList = new ArrayList<>(0);
        assertThrows(GameRuleException.class, () -> gameOperations.dealCards(players, basicCards, emptyCardList));
    }

    @Test
    void shouldAssignActiveCardForEachTeam()
    {
        Player[] players = new Player[] {
                new Player(1, "testPlayer1"),
                new Player(2, "testPlayer2"),
                new Player(3, "testPlayer3"),
                new Player(4, "testPlayer4"),
                new Player(5, "testPlayer5"),
                new Player(6, "testPlayer6")
        };

        Card blueBlind = new Card(101, 1, "Blind");
        Card redBlind = new Card(101, 2, "Blind");

        List<Card> activeCards = new ArrayList<>();
        activeCards.add(blueBlind);
        activeCards.add(redBlind);
        Map<Player, Card> assignedCards = gameOperations.dealCards(players, basicCards, activeCards);

        assertTrue(assignedCards.containsValue(CardConstants.PRESIDENT_CARD));
        assertTrue(assignedCards.containsValue(CardConstants.BOMBER_CARD));
        long blueTeamCards = countCards(assignedCards, CardConstants.BLUE_TEAM_CARD);
        long redTeamCards = countCards(assignedCards, CardConstants.RED_TEAM_CARD);
        long blueBlindCards = countCards(assignedCards, blueBlind);
        long redBlindCards = countCards(assignedCards, redBlind);
        assertEquals(1, blueTeamCards);
        assertEquals(1, redTeamCards);
        assertEquals(1, blueBlindCards);
        assertEquals(1, redBlindCards);
    }

    @Test
    void shouldAssignActiveGreyCardsInsteadOfGambler()
    {
        Player[] players = new Player[] {
                new Player(1, "testPlayer1"),
                new Player(2, "testPlayer2"),
                new Player(3, "testPlayer3"),
                new Player(4, "testPlayer4"),
                new Player(5, "testPlayer5"),
                new Player(6, "testPlayer6"),
                new Player(7, "testPlayer7")
        };

        Card agoraphobe = new Card(101, 3, "Agoraphobe");

        List<Card> activeCards = new ArrayList<>();
        activeCards.add(agoraphobe);
        Map<Player, Card> assignedCards = gameOperations.dealCards(players, basicCards, activeCards);

        assertTrue(assignedCards.containsValue(CardConstants.PRESIDENT_CARD));
        assertTrue(assignedCards.containsValue(CardConstants.BOMBER_CARD));
        long blueTeamCards = countCards(assignedCards, CardConstants.BLUE_TEAM_CARD);
        long redTeamCards = countCards(assignedCards, CardConstants.RED_TEAM_CARD);
        long agoraphobeCards = countCards(assignedCards, agoraphobe);
        assertEquals(2, blueTeamCards);
        assertEquals(2, redTeamCards);
        assertEquals(1, agoraphobeCards);
    }

    @Test
    void shouldThrowGameRuleExceptionWhenTooManyActiveCards()
    {
        Player[] players = new Player[] {
                new Player(1, "testPlayer1"),
                new Player(2, "testPlayer2"),
                new Player(3, "testPlayer3"),
                new Player(4, "testPlayer4"),
                new Player(5, "testPlayer5"),
                new Player(6, "testPlayer6")
        };

        List<Card> activeCards = new ArrayList<>();
        activeCards.add(new Card(101, 1, "customCard1"));
        activeCards.add(new Card(102, 2, "customCard1"));
        activeCards.add(new Card(103, 1, "customCard2"));
        activeCards.add(new Card(104, 2, "customCard2"));
        activeCards.add(new Card(105, 1, "customCard3"));
        activeCards.add(new Card(106, 2, "customCard3"));
        assertThrows(GameRuleException.class, () -> gameOperations.dealCards(players, basicCards, activeCards));
    }

    private long countCards(Map<Player, Card> assignedCards, Card card)
    {
        return assignedCards.entrySet()
                            .stream()
                            .filter(playerCardEntry -> playerCardEntry.getValue().equals(card))
                            .count();
    }
}
