package ru.xfneo.testgis.dao;

import lombok.Setter;
import org.h2.tools.RunScript;
import ru.xfneo.testgis.model.User;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.Objects;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    @Setter
    private Connection conn;
    private static boolean isDbCreated = false;

    public UserDaoImpl() {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
            if (!isDbCreated) {
                intiDb();
                isDbCreated = true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void update(User user) {
        String sql = "UPDATE users SET last_name=? WHERE LOWER(name)=?";
        PreparedStatement statement;
        try {
            statement = conn.prepareStatement(sql);
            statement.setString(1, user.getLastName());
            statement.setString(2, user.getName().toLowerCase());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> find(String name) {
        String sql = "SELECT * FROM users WHERE LOWER(name) = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, name.toLowerCase());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = getUser(resultSet);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("name");
        String lastName = resultSet.getString("last_name");
        return new User(name, lastName);
    }

    private void intiDb() {
        try {
            RunScript.execute(conn, new FileReader(Objects.requireNonNull(getClass().getClassLoader().getResource("create_db.sql")).getFile()));
            RunScript.execute(conn, new FileReader(Objects.requireNonNull(getClass().getClassLoader().getResource("insert_data.sql")).getFile()));
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
