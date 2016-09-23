package sample.datacollection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mina on 7/31/2016.
 */
public class Tools {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/stackexchange";
    private Connection connection;
    private PreparedStatement preparedStatement;

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "123";

    private static Tools tools = new Tools();

    private Tools() {

        //STEP1: Initialization
        connection = null;
        preparedStatement = null;

        //STEP 2: Register JDBC driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //STEP 3: Open a connection
        System.out.println("Connecting to database...");
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int isEmptyTable(String tableName) {

        String sql = "SELECT count(*) " +
                " FROM " + tableName;
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isExistTable(String tableName) {

        try {
            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet resultSet = metadata.getTables(null, null, tableName, null);
            if (resultSet.next())

                return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getSiteNames() {

        String sql = "select distinct siteName from users;";
        List<String> tempList = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tempList.add(resultSet.getString("siteName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tempList;
    }

    public void report(String tableName) {

        String sql = "SELECT count(*) " +
                " FROM " + tableName;
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                System.out.println("Number of " + tableName + " table is: \n"+ resultSet.getInt(1) + "\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // *** Getter & Setters ***

    public static Tools getTools() {
        return tools;
    }

    public static void setTools(Tools tools) {
        Tools.tools = tools;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public void setPreparedStatement(PreparedStatement preparedStatement) { this.preparedStatement = preparedStatement; }
}
