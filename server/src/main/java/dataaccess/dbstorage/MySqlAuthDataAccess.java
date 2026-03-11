package dataaccess.dbstorage;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MySqlAuthDataAccess implements AuthDAO {
    private final ConfigureAndExecute configureAndExecute = new ConfigureAndExecute();

    public MySqlAuthDataAccess() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  auths (
              `token` varchar(256) NOT NULL,
              `userName` varchar(256) NOT NULL,
              PRIMARY KEY (`token`)
            )
            """
        };
        configureAndExecute.configureDatabase(createStatements);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auths";
        configureAndExecute.executeUpdate(statement);
    }

    @Override
    public Collection<AuthData> getAuthStorage() throws DataAccessException {
        Collection<AuthData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auths";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readAuth(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auths (token, username) VALUES (?,?)";
        String token = generateToken();
        configureAndExecute.executeUpdate(statement, token, username);
        return new AuthData(token, username);
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auths WHERE token = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()));
        }
        throw new UnauthorizedResponse("Error: AuthToken not Found");
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        getAuth(authData.token()); // throw not authorized error if not found
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auths WHERE token = ?";
            configureAndExecute.executeUpdate(statement, authData.token());
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()));
        }
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var token = rs.getString("token");
        var username = rs.getString("username");
        return new AuthData(token, username);
    }

}
