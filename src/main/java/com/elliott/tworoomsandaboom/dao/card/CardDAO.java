package com.elliott.tworoomsandaboom.dao.card;

import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.db.DatabaseConnectionManager;
import com.elliott.tworoomsandaboom.error.DatabaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class CardDAO {
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
    public CardDAO(DatabaseConnectionManager databaseConnectionManager)
    {
        this.databaseConnectionManager = databaseConnectionManager;
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
}
