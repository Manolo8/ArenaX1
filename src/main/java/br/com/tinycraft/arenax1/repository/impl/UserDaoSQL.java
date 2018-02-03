package br.com.tinycraft.arenax1.repository.impl;

import br.com.tinycraft.arenax1.build.DataBaseBuild;
import br.com.tinycraft.arenax1.entity.User;
import br.com.tinycraft.arenax1.exception.DataBaseException;
import br.com.tinycraft.arenax1.repository.UserDao;
import br.com.tinycraft.arenax1.utils.replace.Replace;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDaoSQL implements UserDao {

    private final DataBaseBuild build;
    private final Replace updateQuery;

    public UserDaoSQL(DataBaseBuild build) throws DataBaseException {
        try {
            this.build = build;

            Connection connection = build.getConnection();
            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS users (\n" +
                    "  uuid VARCHAR(256) PRIMARY KEY,\n" +
                    "  lastName VARCHAR(64),\n" +
                    "  wins INT(11),\n" +
                    "  loses INT(11),\n" +
                    "  rate DECIMAL(10,2)\n" +
                    ");");

            statement.close();

            this.updateQuery = new Replace("UPDATE users\n" +
                    "SET lastName = '´lastName´', wins = ´wins´, loses = ´loses´, rate = ´rate´ \n" +
                    "WHERE uuid ='´uuid´';").compile();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<User> findAll() throws DataBaseException {
        try {
            List<User> users = new ArrayList<>();

            Connection connection = build.getConnection();
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT * FROM users");

            while (result.next()) users.add(fromResultSet(result));

            statement.close();

            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public User findOne(UUID uuid) throws DataBaseException {
        try {
            Connection connection = build.getConnection();
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT * FROM users WHERE uuid='" + uuid + "'");

            if (!result.next()) return null;

            User user = fromResultSet(result);

            statement.close();

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public void create(User user) throws DataBaseException {
        try {
            user.setNew(false);

            Connection connection = build.getConnection();

            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO users (uuid) VALUES ('" + user.getUuid() + "')");

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public void save(User user) throws DataBaseException {
        if (user.isNew()) create(user);

        try {
            Connection connection = build.getConnection();
            Statement statement = connection.createStatement();

            String sql = updateQuery.setValue("uuid", user.getUuid())
                    .setValue("lastName", user.getLastName())
                    .setValue("wins", user.getWins())
                    .setValue("loses", user.getLoses())
                    .setValue("rate", user.getRate()).build();

            statement.executeUpdate(sql);

            user.setSave(false);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID uuid) throws DataBaseException {
        try {
            Connection connection = build.getConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate("DELETE FROM users WHERE uuid='" + uuid + "'");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<User> ranking() {
        try {
            Connection connection = build.getConnection();
            Statement statement = connection.createStatement();
            List<User> users = new ArrayList<>();

            ResultSet result = statement.executeQuery("SELECT * FROM users WHERE wins >5 ORDER BY rate DESC LIMIT 0,100");

            while (result.next()) {
                users.add(fromResultSet(result));
            }

            return users;
        } catch (SQLException | DataBaseException e) {
            return new ArrayList<>();
        }
    }

    private User fromResultSet(ResultSet result) throws DataBaseException {
        try {
            User user = new User();

            user.setLastName(result.getString("lastName"));
            user.setUuid(UUID.fromString(result.getString("uuid")));
            user.setWins(result.getInt("wins"));
            user.setLoses(result.getInt("loses"));
            user.setNew(false);
            user.setSave(false);

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e.getMessage());
        }
    }
}
