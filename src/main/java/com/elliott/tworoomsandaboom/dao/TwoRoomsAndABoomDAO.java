package com.elliott.tworoomsandaboom.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.elliott.tworoomsandaboom.card.ActiveCardIds;
import com.elliott.tworoomsandaboom.card.ActiveCardNames;
import com.elliott.tworoomsandaboom.card.ActiveCards;
import com.elliott.tworoomsandaboom.card.BasicCards;
import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.db.DatabaseConnectionManager;
import com.elliott.tworoomsandaboom.error.DatabaseException;
import com.elliott.tworoomsandaboom.player.Player;
import com.elliott.tworoomsandaboom.player.RegisterPlayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TwoRoomsAndABoomDAO
{
    private static final String CREATE_NEW_PLAYER = "INSERT INTO player (username) VALUES (?);";
    private static final String DELETE_PLAYER_BY_PLAYER_ID = "DELETE FROM player WHERE playerId = ?;";
    private static final String SELECT_PLAYER_BY_USERNAME = "SELECT playerId, username FROM player WHERE username = ?;";
    private static final String SELECT_PLAYER_BY_PLAYER_ID = "SELECT playerId, username FROM player WHERE playerId = ?;";
    private static final String SELECT_ALL_PLAYERS = "SELECT playerId, username FROM player;";
    private static final String RESET_ACTIVE_CARDS = "UPDATE card SET isActive = 0 WHERE isActive = 1 AND isBasic = 0;";
    private static final String ADD_ASSIGNED_CARDS = "INSERT INTO game (playerId, cardId) VALUES %s;";
    private static final String CLEAR_ASSIGNED_CARDS = "DELETE FROM game";
    private static final String SET_ACTIVE_CARDS_BY_TITLE = "UPDATE card SET isActive = 1 WHERE cardTitle IN (%s);";
    private static final String SET_ACTIVE_CARDS_BY_ID = "UPDATE card SET isActive = 1 WHERE cardId IN (%s);";
    private static final String GET_ACTIVE_CARDS = "SELECT cardId, teamId, cardTitle FROM card WHERE isActive = 1 AND isBasic = 0;";
    private static final String GET_BASIC_CARDS = "SELECT cardId, teamId, cardTitle FROM card WHERE isActive = 1 AND isBasic = 1;";
    private static final String GET_CARD_BY_NAME_AND_TEAM = "SELECT c.cardId, c.cardTitle, t.teamId, c.isActive FROM card c LEFT JOIN team t ON c.teamId = t.teamId WHERE c.cardTitle = ? AND t.colour = ?;";
    private static final String GET_CARD_BY_PLAYER_ID = "SELECT c.cardId, c.cardTitle, t.teamId, c.isActive FROM game g "
            + "LEFT JOIN player p ON g.playerId = p.playerId "
            + "LEFT JOIN card c ON g.cardId = c.cardId "
            + "LEFT JOIN team t ON c.teamId = t.teamId "
            + "WHERE p.playerId = ?;";
    private static final String GET_CARD_FILE_DETAILS = "SELECT c.fileName, t.colour FROM game g "
            + "LEFT JOIN card c ON g.cardId = c.cardId "
            + "LEFT JOIN team t ON t.teamId = c.teamId "
            + "LEFT JOIN player p ON p.playerId = g.playerId "
            + "WHERE p.playerId = ?;";

    private final DatabaseConnectionManager databaseConnectionManager;

    @Autowired
    public TwoRoomsAndABoomDAO(DatabaseConnectionManager databaseConnectionManager)
    {
        this.databaseConnectionManager = databaseConnectionManager;
    }

    public Player createNewPlayer(RegisterPlayer newPlayer)
    {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_NEW_PLAYER))
        {
            statement.setString(1, newPlayer.getUsername());
            statement.execute();
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }

        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_PLAYER_BY_USERNAME))
        {
            statement.setString(1, newPlayer.getUsername());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return new Player(resultSet.getInt("playerId"), resultSet.getString("username"));
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
        return null;
    }

    public void deletePlayer(int playerId)
    {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_PLAYER_BY_PLAYER_ID))
        {
            statement.setInt(1, playerId);
            statement.execute();
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public Optional<Player> getPlayerDetailsFromUsername(String username) {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_PLAYER_BY_USERNAME))
        {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return Optional.of(new Player(
                        resultSet.getInt("playerId"),
                        resultSet.getString("username")
                ));
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
        return Optional.empty();
    }

    public Player[] getPlayers()
    {
        List<Player> playersList = new ArrayList<>();
        try (Connection connection = databaseConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_ALL_PLAYERS))
        {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
                playersList.add(new Player(resultSet.getInt("playerId"), resultSet.getString("username")));
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
        Player[] players = new Player[playersList.size()];
        return playersList.toArray(players);
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


    public void readyUpPlayer(int playerId)
    {

    }

    public Card getCard(int playerId)
    {
        try (Connection connection = databaseConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_CARD_BY_PLAYER_ID))
        {
            statement.setInt(1, playerId);
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

    public String getCardFilePath(int playerId)
    {
        try (Connection connection = databaseConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_CARD_FILE_DETAILS))
        {
            log.info(statement.toString());
            statement.setInt(1, playerId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return String.format("%s/%s", resultSet.getString("colour"), resultSet.getString("fileName"));
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

    public boolean checkCredentials(int playerId, String username) {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_PLAYER_BY_PLAYER_ID))
        {
            statement.setInt(1, playerId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return resultSet.getString("username").equals(username);
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
        return false;
    }
}
