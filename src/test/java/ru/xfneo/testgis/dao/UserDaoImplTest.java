package ru.xfneo.testgis.dao;

import org.junit.Before;
import org.junit.Test;
import ru.xfneo.testgis.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserDaoImplTest {

    private UserDaoImpl userDao;

    @Before
    public void init() {
        userDao = new UserDaoImpl();
    }

    @Test
    public void findWhiteBoxTest() throws SQLException {
        String sql = "SELECT * FROM users WHERE LOWER(name) = ?";
        String userName = "Maksim";
        Connection connectionMock = mock(Connection.class);
        PreparedStatement statementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(sql)).thenReturn(statementMock);
        when(statementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString("name")).thenReturn("Maksim");
        when(resultSetMock.getString("last_name")).thenReturn("Tikhonov");

        userDao.setConn(connectionMock);
        Optional<User> optionalUser = userDao.find("Maksim");

        assertThat(optionalUser.get()).isNotNull();
        assertThat(optionalUser.get()).isInstanceOf(User.class);
        verify(connectionMock, times(1)).prepareStatement(sql);
        verify(statementMock, times(1)).setString(1, userName.toLowerCase());
        verify(statementMock, times(1)).executeQuery();
        verify(resultSetMock, times(1)).next();
        verify(resultSetMock, times(1)).getString("name");
        verify(resultSetMock, times(1)).getString("last_name");
        verifyNoMoreInteractions(connectionMock, statementMock, resultSetMock);
    }

    @Test
    public void updateWhiteBoxTest() throws SQLException {
        User user = new User("Maksim", "Tikhonov");
        String sql = "UPDATE users SET last_name=? WHERE LOWER(name)=?";
        Connection connectionMock = mock(Connection.class);
        PreparedStatement statementMock = mock(PreparedStatement.class);
        when(connectionMock.prepareStatement(sql)).thenReturn(statementMock);

        userDao.setConn(connectionMock);
        userDao.update(user);

        verify(connectionMock, times(1)).prepareStatement(sql);
        verify(statementMock, times(1)).setString(1, user.getLastName());
        verify(statementMock, times(1)).setString(2, user.getName().toLowerCase());
        verify(statementMock, times(1)).executeUpdate();
        verifyNoMoreInteractions(connectionMock, statementMock);
    }

    @Test
    public void updateTest() {
        Optional<User> optionalUser = userDao.find("Maksim");
        User user = optionalUser.get();
        user.setLastName("NewLastName");
        userDao.update(user);

        Optional<User> optionalUpdatedUser = userDao.find("Maksim");
        User updatedUser = optionalUpdatedUser.get();
        assertThat(updatedUser.getLastName()).isEqualTo("NewLastName");
    }

    @Test
    public void findTest() {
        Optional<User> optionalUser = userDao.find("Maksim");
        User user = optionalUser.get();
        assertThat(user).isNotNull();
        assertThat(user).isExactlyInstanceOf(User.class);
        assertThat(user.getName()).isEqualTo("Maksim");
    }

    @Test(expected = NoSuchElementException.class)
    public void findNullTest() {
        Optional<User> nullUser = userDao.find("NullName");
        nullUser.get();
    }
}