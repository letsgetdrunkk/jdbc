package jdbcDemo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class BankDB {
    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DATABASE = "bank";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private static final String CUSTOMER_INSERT_TEMPLATE = "INSERT INTO customer(`id`,`name`,`age`,`sex`) values(%d,'%s',%d,'%s')";
    private static final String ACCOUNT_INSERT_TEMPLATE = "INSERT INTO account(`id`,`account_num`,`balance`,`customer_id`) values(%d,'%s',%.2f,%d)";
    private static final String QUERY_BALANCE = "SELECT * FROM customer JOIN account ON customer.id=account.customer_id WHERE account.account_num =?";
    private static final String ADD_BALANCE = "UPDATE account SET balance = ? WHERE account_num = ?";
    private static final String DELETE_ACCOUNT="DELETE FROM account WHERE account_num = ?";
    private Connection connection;

    public void connectDB() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;
        println("JDBC URL: " + url);
        connection = DriverManager.getConnection(url, USER, PASSWORD);
    }
    public void insertToCustomer(String[] content) throws SQLException {
        Object[] data = new Object[content.length];
        data[0] = Integer.parseInt(content[0]);
        data[1] = content[1];
        data[2] = Integer.parseInt(content[2]);
        data[3] = content[3];
        Statement stat = connection.createStatement();
        String sql = String.format(CUSTOMER_INSERT_TEMPLATE, data);
        println("insert sql: " + sql);
        stat.executeUpdate(sql);
    }
    public void insertToAccount(String[] content) throws SQLException {
        Object[] data = new Object[content.length];
        data[0] = Integer.parseInt(content[0]);
        data[1] = content[1];
        data[3] = Integer.parseInt(content[3]);
        data[2] = Double.parseDouble(content[2]);
        Statement stat = connection.createStatement();
        String sql = String.format(ACCOUNT_INSERT_TEMPLATE, data);
        println("insert sql: " + sql);
        stat.executeUpdate(sql);
    }
    public void addAccountBalance(String[] content) throws SQLException {
        String account_num = content[0];
        Double old_balance = queryBalance(account_num);
        Double new_balance = old_balance + Double.parseDouble(content[1]);
        PreparedStatement prepStat = connection.prepareStatement(ADD_BALANCE);
        prepStat.setDouble(1,new_balance);
        prepStat.setString(2,account_num);
        prepStat.executeUpdate();
    }
    public double queryBalance(String account_num) throws SQLException {
        PreparedStatement prepStat = connection.prepareStatement(QUERY_BALANCE);
        prepStat.setString(1,account_num);
        ResultSet rs =  prepStat.executeQuery();
        if(rs.next()){
            String id = rs.getString("account_num");
            String name = rs.getString("name");
            double balance = rs.getDouble("balance");
            println("customer account info:");
            println("id: " + id + ", name: " + name + ", balance: " + balance);

            return balance;
        }
        else {
            println("account does not exist");
            return 0;
        }
    }
    public void deleteAccount(String account_num) throws SQLException {
        PreparedStatement prepStat = connection.prepareStatement(DELETE_ACCOUNT);
        prepStat.setString(1,account_num);
        prepStat.executeUpdate();
    }

    public void closeDB() throws SQLException {
        connection.close();
    }
    private static void println(Object o) {
        System.out.println(o);
    }


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        BankDB bankDB = new BankDB();
        bankDB.connectDB();
        Scanner sc = new Scanner(System.in);
        println("欢迎使用bank demo，您可以办理以下业务");
        println("1.创建账户     2.存款    3.查询余额    4.删除账户    0.退出");
        while (sc.hasNext()){
            int code = Integer.parseInt(sc.nextLine());
            switch (code){
                case 1:
                    println("请在接下来一行输入您的个人信息，以一个空格符分割：id name age sex");
                    String[] customerData = sc.nextLine().split(" ");
                    println("请在接下来一行输入具体信息，以一个空格符分割：id account_num balance customer_id");
                    String[] accountData = sc.nextLine().split(" ");
                    bankDB.insertToCustomer(customerData);
                    bankDB.insertToAccount(accountData);
                    println("操作完成，您可以继续办理业务或退出");
                    println("1.创建账户     2.存款    3.查询余额    4.删除账户    0.退出");
                    continue;
                case 2:
                    println("请在接下来一行输入具体信息，以一个空格符分割：account_num amount");
                    String[] updateData = sc.nextLine().split(" ");
                    bankDB.addAccountBalance(updateData);
                    println("操作完成，您可以继续办理业务或退出");
                    println("1.创建账户     2.存款    3.查询余额    4.删除账户    0.退出");
                    continue;
                case 3:
                    println("请在接下来一行输入账户账号：account_num");
                    String[] queryData = sc.nextLine().split(" ");
                    bankDB.queryBalance(queryData[0]);
                    println("操作完成，您可以继续办理业务或退出");
                    println("1.创建账户     2.存款    3.查询余额    4.删除账户    0.退出");
                    continue;
                case 4:
                    println("请在接下来一行输入账户账号：account_num");
                    String[] delData = sc.nextLine().split(" ");
                    bankDB.deleteAccount(delData[0]);
                    println("操作完成，您可以继续办理业务或退出");
                    println("1.创建账户     2.存款    3.查询余额    4.删除账户    0.退出");
                    continue;
                case 0:
                    bankDB.closeDB();
                    System.exit(0);
            }
        }
    }

}
