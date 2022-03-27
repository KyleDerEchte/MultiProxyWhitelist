package de.kyleonaut.multiproxywhitelist.repository;

import de.kyleonaut.multiproxywhitelist.model.PlayerModel;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author kyleonaut
 */
public class WhitelistRepository {
    private final Connection connection;


    public WhitelistRepository(Connection connection) {
        this.connection = connection;
        try {
            final PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS players(uuid VARCHAR(36),name VARCHAR(16),isWhitelisted TINYINT,PRIMARY KEY (uuid));"
            );
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PlayerModel> getWhitelistedPlayers() {
        try {
            @Cleanup final PreparedStatement ps = this.connection.prepareStatement(
                    "SELECT * FROM players WHERE isWhitelisted = 1;"
            );
            final List<PlayerModel> playerModels = new CopyOnWriteArrayList<>();
            @Cleanup final ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                final PlayerModel playerModel = new PlayerModel();
                playerModel.setName(resultSet.getString("name"));
                playerModel.setUuid(UUID.fromString(resultSet.getString("uuid")));
                playerModels.add(playerModel);
            }
            return playerModels;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addPlayerToWhitelist(PlayerModel player) {
        try {
            @Cleanup final PreparedStatement ps = this.connection.prepareStatement(
                    "REPLACE INTO players VALUES(?,?,1);"
            );
            ps.setString(1, player.getUuid().toString());
            ps.setString(2, player.getName());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void removePlayerFromWhitelist(PlayerModel player) {
        try {
            @Cleanup final PreparedStatement ps = this.connection.prepareStatement(
                    "REPLACE INTO players VALUES(?,?,0);"
            );
            ps.setString(1, player.getUuid().toString());
            ps.setString(2, player.getName());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
