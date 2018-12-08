package jdbcDemo;

import java.sql.*;

public class JDBCDemo {

    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DATABASE = "bank";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private static final String CUSTOMER_INSERT_TEMPLATE = "INSERT INTO customer(`id`,`name`,`age`,`sex`) values(%d,'%s',%d,'%s')";
    private static final String ACCOUNT_INSERT_TEMPLATE = "INSERT INTO account(`id`,`account_num`,`balance`,`customer_id`) values(%d,'%s',%.2f,%d)";

    public static void main(String[] args) throws Exception {
        // preparation for JDBC, this is the only difference between MySQL and SQL Server
	/* For SQL Server, your code will be like: 
         * Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
         * Connection connection = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=bank", "root", "123"); 
         * */
	    Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;
        println("JDBC URL: " + url);
        Connection connection = DriverManager.getConnection(url, USER, PASSWORD);

        // insert example
        /** After insertion, the snapshot of database:
         * Table customer:
         * +----------+------------+-----------+-----------+
         * |    id    |    name    |    age    |    sex    |
         * +----------+------------+-----------+-----------+
         * |     1    |    Tom     |     20    |     M     |
         * +----------+------------+-----------+-----------+
         * |     2    |    Mary    |     20    |     F     |       
         * +----------+------------+-----------+-----------+
         * 
         * Table account:
         * +----------+------------+-----------+-----------+
         * |    id    |account_num |  balance  |customer_id|
         * +----------+------------+-----------+-----------+
         * |     1    |    12345   |   100.00  |     2     |
         * +----------+------------+-----------+-----------+
         * |     2    |    54321   |   50.00   |     1     |       
         * +----------+------------+-----------+-----------+
         */
        {
            Statement stat = connection.createStatement();
            String sql = String.format(CUSTOMER_INSERT_TEMPLATE, 1, "Tom", 20, "M");
            println("insert sql: " + sql);
            stat.executeUpdate(sql);
            sql = String.format(CUSTOMER_INSERT_TEMPLATE, 2, "Mary", 20, "F");
            println("insert sql: " + sql);
            stat.executeUpdate(sql);
            sql = String.format(ACCOUNT_INSERT_TEMPLATE, 1, "12345", 100.0, 2);
            println("insert sql: " + sql);
            stat.executeUpdate(sql);
            sql = String.format(ACCOUNT_INSERT_TEMPLATE, 2, "54321", 50.0, 1);
            println("insert sql: " + sql);
            stat.executeUpdate(sql);
        }

        // select example
        {
            Statement stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("SELECT `id`,`name`,`age`,`sex` FROM customer");
            println("all customers:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString(2);
                int age = rs.getInt("age");
                String sex = rs.getString(4);
                println("id: " + id + ", name: " + name + ", age: " + age + ", sex: " + sex);
            }
            rs = stat.executeQuery(
                    "SELECT * FROM customer JOIN account ON customer.id=account.customer_id WHERE balance > 70");
            println("customers whose account's balance is more than 70:");
            while (rs.next()) {
                int id = rs.getInt("customer.id");
                String name = rs.getString("name");
                double balance = rs.getDouble("balance");
                println("id: " + id + ", name: " + name + ", balance: " + balance);
            }
        }

        // delete example
        {
            PreparedStatement prepStat = connection.prepareStatement("DELETE FROM account WHERE balance > ?");
            prepStat.setInt(1, 0);
            int updates = prepStat.executeUpdate();
            println("number of deleted accounts:" + updates);
            Statement stat = connection.createStatement();
            updates = stat.executeUpdate("DELETE FROM customer");
            println("number of deleted customers:" + updates);
        }

        /** close connection to exit */
        connection.close();
    }

    private static void println(Object o) {
        System.out.println(o);
    }
}
