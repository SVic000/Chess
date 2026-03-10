package dataaccess.dbStorage;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlAuthDataAccess implements AuthDAO {

    public MySqlAuthDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auths";
        executeUpdate(statement);
    }

    @Override
    public Collection<AuthData> getAuthStorage() throws DataAccessException {
        Collection<AuthData> result = new ArrayList<>();
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT token FROM auths";
            try(PreparedStatement ps = conn.prepareStatement(statement)) {
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        result.add(readAuth(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auths (token, username) VALUES (?,?)";
        String token = generateToken();
        try {
            executeUpdate(statement, token, username);
            return  new AuthData(token, username);
        } catch (DataAccessException e) {
            throw new ForbiddenResponse("Error: already taken");
        }
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auths WHERE token = ?";
            try(PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        throw new UnauthorizedResponse("Error: AuthToken not Found");
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        getAuth(authData.token()); // throw not authorized error if not found
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auths WHERE token = ?";
            executeUpdate(statement,authData.token());
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getString(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var token = rs.getString("token");
        var username= rs.getString("username");
        return new AuthData(token, username);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `token` varchar(256) NOT NULL,
              `userName` varchar(256) NOT NULL,
              PRIMARY KEY (`token`)
            )
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
