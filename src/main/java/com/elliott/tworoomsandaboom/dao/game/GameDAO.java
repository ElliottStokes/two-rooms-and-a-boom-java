package com.elliott.tworoomsandaboom.dao.game;

import com.elliott.tworoomsandaboom.card.*;
import com.elliott.tworoomsandaboom.controller.game.GameState;
import com.elliott.tworoomsandaboom.db.DatabaseConnectionManager;
import com.elliott.tworoomsandaboom.error.DatabaseException;
import com.elliott.tworoomsandaboom.game.Room;
import com.elliott.tworoomsandaboom.player.AssignedPlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GameDAO {
    private static final String SET_ACTIVE_CARDS_BY_TITLE = "UPDATE card SET isActive = 1 WHERE cardTitle IN (%s);";
    private static final String SET_ACTIVE_CARDS_BY_ID = "UPDATE card SET isActive = 1 WHERE cardId IN (%s);";
    private static final String RESET_ACTIVE_CARDS = "UPDATE card SET isActive = 0 WHERE isActive = 1 AND isBasic = 0;";
    private static final String ADD_ASSIGNED_PLAYERS = "INSERT INTO game (gameId, playerId, cardId, room) VALUES %s;";
    private static final String CLEAR_ASSIGNED_PLAYERS = "DELETE FROM game WHERE gameId = ?;";
    private static final String SET_GAME_STATE = "UPDATE gameState SET gameState = ? WHERE gameId = ?;";
    private static final String GET_ACTIVE_CARDS = "SELECT cardId, teamId, cardTitle FROM card WHERE isActive = 1 AND isBasic = 0;";
    private static final String GET_BASIC_CARDS = "SELECT cardId, teamId, cardTitle FROM card WHERE isActive = 1 AND isBasic = 1;";
    private static final String GET_CARD_BY_NAME_AND_TEAM = "SELECT c.cardId, c.cardTitle, t.teamId, c.isActive FROM card c LEFT JOIN team t ON c.teamId = t.teamId WHERE c.cardTitle = ? AND t.colour = ?;";
    private static final String SELECT_ROOM_BY_PLAYER_ID = "SELECT room FROM game WHERE playerId = ?";
    private static final String SET_REVEAL_TIME = "UPDATE gameState SET matchEndTime = NOW() + INTERVAL 10 SECOND WHERE gameId = ?;";
    private static final String GET_GAME_STATE = "SELECT gameState FROM gameState WHERE gameId = ?;";
    private static final String GET_REVEAL_TIME = "SELECT matchEndTime FROM gameState WHERE gameId = ?;";

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

    public void setGameState(GameState gameState, int gameId)
    {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SET_GAME_STATE))
        {
            statement.setString(1, gameState.toString());
            statement.setInt(2, gameId);

            statement.execute();
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void saveAssignedPlayers(List<AssignedPlayer> assignedPlayers) {
        StringBuilder assignedPlayerStrings = new StringBuilder();
        for (AssignedPlayer assignedPlayer : assignedPlayers) {
            assignedPlayerStrings.append(assignedPlayer.toDatabaseInputString()).append(',');
        }
        String addAssignedPlayersFormatted =
            String.format(ADD_ASSIGNED_PLAYERS, assignedPlayerStrings.substring(0, assignedPlayerStrings.length()-1));
        log.info(addAssignedPlayersFormatted);
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(addAssignedPlayersFormatted))
        {
            statement.execute();
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void clearAssignedPlayers(int gameId)
    {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(CLEAR_ASSIGNED_PLAYERS))
        {
            statement.setInt(1, gameId);
            statement.execute();
            log.info("!GAME CARDS HAVE BEEN CLEARED!");
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public Room getRoom(int playerId) {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ROOM_BY_PLAYER_ID))
        {
            statement.setInt(1, playerId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return Room.valueOf(resultSet.getString("room"));
            else
                throw new DatabaseException("No room found");
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void setRevealTime(int gameId) {
        try (Connection connection = databaseConnectionManager.getConnection();
         PreparedStatement statement = connection.prepareStatement(SET_REVEAL_TIME))
        {
            statement.setInt(1, gameId);
            statement.execute();
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public GameState getGameState(int gameId) {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_GAME_STATE))
        {
            statement.setInt(1, gameId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return GameState.valueOf(resultSet.getString("gameState"));
            else
                throw new DatabaseException("No game found");
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public String getRevealTime(int gameId) {
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_REVEAL_TIME))
        {
            statement.setInt(1, gameId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return resultSet.getString("matchEndTime");
            else
                throw new DatabaseException("No game found");
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }
}
