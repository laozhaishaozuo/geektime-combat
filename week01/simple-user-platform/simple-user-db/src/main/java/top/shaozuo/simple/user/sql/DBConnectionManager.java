package top.shaozuo.simple.user.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnectionManager {

    private static final String CONNECTION_URL = "jdbc:derby:./db/user-platform";
    private static final String USER = "spring-boot";
    private static final String PASSWORD = "123456";
    private static final String DROP_USERS_TABLE_DDL_SQL = "DROP TABLE users";

    private static final String CREATE_USERS_TABLE_DDL_SQL = "CREATE TABLE users("
            + "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
            + "name VARCHAR(16) NOT NULL, " + "password VARCHAR(64) NOT NULL, "
            + "email VARCHAR(64) NOT NULL, " + "phoneNumber VARCHAR(64) NOT NULL" + ")";
    static {
        //        通过 ClassLoader 加载 java.sql.DriverManager -> static 模块 {}
        //        DriverManager.setLogWriter(new PrintWriter(System.out));
        //
        //        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        //        Driver driver = DriverManager.getDriver("jdbc:derby:/db/user-platform;create=true");
        //        Connection connection = driver.connect("jdbc:derby:/db/user-platform;create=true", new Properties());
        //创建表
        DriverManager.setLogWriter(new PrintWriter(System.out));
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection connection = DriverManager.getConnection(CONNECTION_URL + ";create=true");
            Statement statement = connection.createStatement();
            statement.execute(CREATE_USERS_TABLE_DDL_SQL);
        } catch (SQLException e) {
            //忽略
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL);
    }

    public static void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}