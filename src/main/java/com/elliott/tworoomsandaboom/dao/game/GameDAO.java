package com.elliott.tworoomsandaboom.dao.game;

import com.elliott.tworoomsandaboom.card.*;
import com.elliott.tworoomsandaboom.db.DatabaseConnectionManager;
import com.elliott.tworoomsandaboom.error.DatabaseException;
import com.elliott.tworoomsandaboom.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GameDAO {
    private static final String SET_ACTIVE_CARDS_BY_TITLE = "UPDATE card SET isActive = 1 WHERE cardTitle IN (%s);";
    private static final String SET_ACTIVE_CARDS_BY_ID = "UPDATE card SET isActive = 1 WHERE cardId IN (%s);";
    private static final String RESET_ACTIVE_CARDS = "UPDATE card SET isActive = 0 WHERE isActive = 1 AND isBasic = 0;";
    private static final String ADD_ASSIGNED_CARDS = "INSERT INTO game (playerId, cardId) VALUES %s;";
    private static final String CLEAR_ASSIGNED_CARDS = "DELETE FROM game";
    private static final String GET_ACTIVE_CARDS = "SELECT cardId, teamId, cardTitle FROM card WHERE isActive = 1 AND isBasic = 0;";
    private static final String GET_BASIC_CARDS = "SELECT cardId, teamId, cardTitle FROM card WHERE isActive = 1 AND isBasic = 1;";
    private static final String GET_CARD_BY_NAME_AND_TEAM = "SELECT c.cardId, c.cardTitle, t.teamId, c.isActive FROM card c LEFT JOIN team t ON c.teamId = t.teamId WHERE c.cardTitle = ? AND t.colour = ?;";


    private final DatabaseConnectionManager databaseConnectionManager;

    @Autowired
    public GameDAO(DatabaseConnectionManager databaseConnectionManager)
    {
        this.databaseConnectionManager = databaseConnectionManager;
    }

    public void setActiveCards(ActiveCardNames activeCardNames)
    {
        setActiveCards(activeCardNames, SET_ACTIVE_CARDS_BY_TITLE);
    }

    public void setActiveCards(ActiveCardIds activeCardIds)
    {
        setActiveCards(activeCardIds, SET_ACTIVE_CARDS_BY_ID);
    }

    private void setActiveCards(ActiveCards activeCards, String query)
    {
        removeActiveCards();
        String formattedSql = String.format(query, activeCards.getDatabaseInput());
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(formattedSql))
        {
            log.info(statement.toString());
            statement.execute();
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void removeActiveCards()
    {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(RESET_ACTIVE_CARDS))
        {
            statement.execute();
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public List<Card> getActiveCards()
    {
        List<Card> cardsList = new ArrayList<>();
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ACTIVE_CARDS))
        {
            log.info(statement.toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
                cardsList.add(new Card(resultSet.getInt("cardId"), resultSet.getInt("teamId"), resultSet.getString("cardTitle")));
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
        return cardsList;
    }

    public BasicCards getBasicCards()
    {
        List<Card> cardsList = new ArrayList<>();
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BASIC_CARDS))
        {
            log.info(statement.toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
                cardsList.add(new Card(resultSet.getInt("cardId"), resultSet.getInt("teamId"), resultSet.getString("cardTitle")));
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
        return new BasicCards(cardsList);
    }

    public Card getCard(String cardTitle, String team)
    {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_CARD_BY_NAME_AND_TEAM))
        {
            log.info(statement.toString());
            statement.setString(1, cardTitle);
            statement.setString(2, team);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return new Card(resultSet.getInt("cardId"), resultSet.getInt("teamId"), resultSet.getString("cardTitle"));
            else
                throw new DatabaseException("No card found");
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void clearAssignedCards()
    {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(CLEAR_ASSIGNED_CARDS))
        {
            statement.execute();
            log.info("!GAME CARDS HAVE BEEN CLEARED!");
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void saveAssignedCards(Map<Player, Card> assignedCards)
    {
        this.clearAssignedCards();

        StringBuilder assignedCardIds = new StringBuilder();
        for (Map.Entry<Player, Card> assignedCard : assignedCards.entrySet())
        {
            assignedCardIds.append("(")
                    .append(assignedCard.getKey().getPlayerId())
                    .append(", ")
                    .append(assignedCard.getValue().getCardId())
                    .append("),");
        }
        log.info(assignedCardIds.substring(0, assignedCardIds.length()-1));
        String addAssignedCardsFormatted = String.format(ADD_ASSIGNED_CARDS, assignedCardIds.substring(0, assignedCardIds.length()-1));
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(addAssignedCardsFormatted))
        {
            statement.execute();
            log.info("!GAME CARDS HAVE BEEN ASSIGNED!");
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }
}
