package dataaccess.dbStorage;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySqlAuthDataAccess implements AuthDAO {

    public MySqlAuthDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() {

    }

    @Override
    public Collection<AuthData> getAuthStorage() {
        return List.of();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(String token) {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `id` int NOT NULL AUTO_INCREMENT,
              `token` varchar(256) NOT NULL,
              `userName` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(token)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
