package dataaccess.dbStorage;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlUserDataAccess implements UserDAO {

    public MySqlUserDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?,?,?)";
        try {
            executeUpdate(statement, user.username(), hashUserPassword(user.password()), user.email());
        } catch (DataAccessException e) {
            if(e.getMessage().contains("Duplicate entry")) {
                throw new ForbiddenResponse("Error: already taken");
            }
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public Collection<UserData> getUserStorage() throws DataAccessException{
        List result = new ArrayList();
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "Select * FROM users";
            try(PreparedStatement ps = conn.prepareStatement(statement)) {
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        result.add(readUser(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public UserData getUser(String userName) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "Select * FROM users WHERE userName = ?";
            try(PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, userName);
                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()));
        }
        throw new UnauthorizedResponse("Error: User not found");
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username,password,email);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `userName` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`userName`)
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
            throw new DataAccessException(String.format("Error: unable to configure database: %s", ex.getMessage()));
        }
    }

    private String executeUpdate(String statement, Object... params) throws DataAccessException {
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
                    return rs.getString(1);
                }
                return "";
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    String hashUserPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    @Override
    public boolean verifyUserPassword(String username, String providedClearTextPassword) throws DataAccessException {
        UserData dbUser = getUser(username);
        return BCrypt.checkpw(providedClearTextPassword, dbUser.password());
    }
}
