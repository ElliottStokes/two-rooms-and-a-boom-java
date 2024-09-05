package com.elliott.tworoomsandaboom.dao.player;

import com.elliott.tworoomsandaboom.db.DatabaseConnectionManager;
import com.elliott.tworoomsandaboom.error.DatabaseException;
import com.elliott.tworoomsandaboom.player.Player;
import com.elliott.tworoomsandaboom.player.RegisterPlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class PlayerDAO {
    private static final String SELECT_PLAYER_BY_PLAYER_ID = "SELECT playerId, username FROM player WHERE playerId = ?;";
    private static final String CREATE_NEW_PLAYER = "INSERT INTO player (username) VALUES (?);";
    private static final String DELETE_PLAYER_BY_PLAYER_ID = "DELETE FROM player WHERE playerId = ?;";
    private static final String SELECT_PLAYER_BY_USERNAME = "SELECT playerId, username FROM player WHERE username = ?;";
    private static final String SELECT_ALL_PLAYERS = "SELECT playerId, username FROM player;";

    private final DatabaseConnectionManager databaseConnectionManager;

    @Autowired
    public PlayerDAO(DatabaseConnectionManager databaseConnectionManager)
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

    public void readyUpPlayer(int playerId)
    {

    }
}
