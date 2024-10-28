package 软件体系结构实验三.两层CS模式2版;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager(String dbUrl, String username, String password) {
        try {
            connection = DriverManager.getConnection(dbUrl, username, password);
            System.out.println("数据库连接成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addContact(String name, String phoneNum) {
        String sql = "INSERT INTO address_book (name, phone_num) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, phoneNum);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet queryContactByName(String name) {
        String sql = "SELECT * FROM address_book WHERE name LIKE ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + name + "%"); // 使用 LIKE 进行模糊查询
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryContactByPhone(String phone) {
        String sql = "SELECT * FROM address_book WHERE phone_num = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, phone); // 精确匹配电话号码
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryAllContacts() {
        String sql = "SELECT * FROM address_book";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteContact(String name, String phoneNum) {
        String sql = "DELETE FROM address_book WHERE name = ? AND phone_num = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, phoneNum);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // 如果有行被删除，返回 true
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 如果发生错误，返回 false
        }
    }

    public ResultSet queryContactByNameOrPhone(String oldInfo) {
        String sql = "SELECT * FROM address_book WHERE name = ? OR phone_num = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, oldInfo);
            statement.setString(2, oldInfo);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean modifyContact(String oldInfo, String newName, String newPhoneNum) {
        String sql = "UPDATE address_book SET name = ?, phone_num = ? WHERE name = ? OR phone_num = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newName);
            statement.setString(2, newPhoneNum);
            statement.setString(3, oldInfo);
            statement.setString(4, oldInfo);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
