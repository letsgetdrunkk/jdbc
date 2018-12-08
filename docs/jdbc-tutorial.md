## 概述  

---

通过这个引导，你可以了解到如何使用JDBC访问数据库，具体包括：  

* 使用JDBC连接MySQL  

* 使用JDBC插入数据  

* 使用JDBC查询数据  

* 使用JDBC删除数据  

你需要具备以下预备知识：  

* 了解SQL  

* 了解如何使用命令行工具/图形用户界面访问数据库  

* 了解如何使用IntelliJ IDEA进行Java程序开发  

**末尾部分提供了完整的Demo代码供你参考。**

## 前置步骤一：定义数据库模式    

---

在这个引导中，我们使用一个简化的银行数据库作为例子，里面只有用户和账户两张表。项目目录jdbc/sql下有数据库模式图和整个数据库的ddl

## 前置步骤二：在IntelliJ IDEA中建立Java项目并添加相关依赖  

---

当你已经在IntelliJ IDEA中创建了Java项目，你还需要添加相关依赖才能使JDBC程序顺利访问MySQL数据库。通过以下步骤添加依赖：  
1. 在项目目录jdbc下有个pom.xml文件，在该文件的<dependencies></dependencies>的标签（如果没有，手动添加）中添加

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.6</version>
</dependency>
```

至此，你已经完成了所有前置步骤，接下来就是写代码的时间啦！  

## 连接MySQL服务器  

---

JDBC使用**URL**描述数据库服务器在网络上的位置，不同的数据库产品URL规范有所差别。  
#### MySQL数据库的URL规范    

`jdbc:mysql://<host>:<port>/<database_name>`      
 
* host - MySQL服务器的主机名或IP地址，如果是在本机上，建议使用"localhost"   

* port - MySQL服务器的端口，默认为3306    

* database_name - 要连接的数据库名字    

#### SQL Server数据库的URL规范    

`jdbc:sqlserver://<host>\<instance>:<port>;databaseName=<database_name>`     
 
* host - MySQL服务器的主机名或IP地址，如果是在本机上，建议使用"localhost"   

* instance - SQL Server服务器实例名，这个名称可以在“SQL Server配置管理器”中查看，通常为"SQLEXPRESS"    

* port - MySQL服务器的端口，默认为1433    

* database_name - 要连接的数据库名字    

在本例中，由于我的数据库均安装在本机上，并且使用了默认的配置，数据库名为bank，因此MySQL对应的URL为`jdbc:mysql://localhost:3306/bank`；SQL Server对应的URL为`jdbc:sqlserver://localhost\SQLEXPRESS:1433;databaseName=bank`；   

确定了数据库的URL后，你就可以连接数据库了。JDBC将数据库连接封装为**Connection**对象，下面是获取Connection对象的步骤：     

1. 使用`Class.forName("com.mysql.jdbc.Driver")`加载依赖包中的类。    

2. 使用`DriverManager::getConnection(String url, String user, String password)`获取Connection对象。其中，url参数就是上述URL，user和password参数是数据库的用户名和密码。    

这是完整的获取数据库连接代码：    

```java
import java.sql.Connection;
import java.sql.DriverManager;


// 使用MySQL的同学
Class.forName("com.mysql.jdbc.Driver");
Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "123"); //数据库用户名为root,密码为123

// 使用SQL Server的同学
Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
Connection connection = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=bank", "root", "123"); 
```  

## 数据插入

---

现在你已经有Connection对象，为了执行SQL语句，你还需要得到**Statement**对象。数据库会为每个Statement分配充足的资源来执行SQL语句。Statement对象具体可分为两类：**Statement**和**PreparedStatement**，本节会使用Statement执行SQL语句，数据删除部分会讨论PreparedStatement的使用。   

使用Statement执行SQL分为两个步骤：  

1. 通过Connection获取Statement对象  

2. 通过`Statement::executeUpdate(String sql)`方法执行SQL语句  

就是这么简单...  

下面是完整的数据插入代码，executeUpdate方法返回受影响的Tuple数，对于INSERT语句，返回值总为1。   

```java
import java.sql.Statement;

Statement statement = connection.createStatement();
int updates = statement.executeUpdate("INSERT INTO customer(`id`,`name`,`age`,`sex`) values(1,'Tom',20,'M')");

```

>JDBC的设计理念是，只要没有抛出SQLException异常，就代表SQL语句被成功执行。  

##  

> 由于SQLException几乎无法在运行期处理，建议不用捕获它，直接将其抛出。  

## 数据查询  

---

数据查询的步骤与数据插入唯一的区别是，在第二步你需要使用`Statement::executeQuery(String sql)`方法执行SQL语句。  

executeQuery方法会打开一个**ResultSet**对象，并将其作为返回值。通常，你需要**遍历ResultSet获取查询结果**。你可以将ResultSet看做一个游标，每次指向查询结果的一条Tuple。你需要通过以下步骤遍历ResultSet：  

1. 使用`ResultSet::next()`方法将ResultSet指向下一条Tuple。next方法返回一个布尔值，当返回true时，表明存在下一条Tuple；返回false时，表明不存在下一条Tuple。  
在项目目录jdbc/docs/ResultSet.png下展示了ResultSet示意图

2. 每次调用next方法并返回true时，使用<code>ResultSet::get<em>XXX</em></code>方法获取当前元组的某个属性值，<em>XXX</em>代表该属性值的数据类型，比如Int，String等。get<em>XXX</em>方法可以传入该属性的名字或是该属性在SELECT子句中的次序。比如，对于`SELECT id, name FROM customer`查询，你可以使用`ResultSet.getInt(1)`或`ResultSet.getInt("id")`获得当前元组的id属性值。    
 
这是完整的数据查询代码：  

```java
Statement stat = connection.createStatement();
ResultSet rs = stat.executeQuery("SELECT `id`,`name`,`age`,`sex` FROM customer");
while (rs.next()) {
    int id = rs.getInt("id");
    String name = rs.getString(2);
    int age = rs.getInt("age");
    String sex = rs.getString(4);
    // 对Tuple的具体处理
}

```

> 一个Statement同时只能打开一个ResultSet，只有关闭了旧的ResultSet后才能打开新的ResultSet。  

# 

> next方法返回false时会自动关闭ResultSet，或者你可以使用`ResultSet::close()`方法手动关闭ResultSet。  

## 数据删除

---

你可以像数据插入那样进行数据删除，只要你传入的是DELETE语句。本节主要讨论使用PreparedStatement进行数据删除。  

在很多时候，当你写代码时，你只知道SQL语句的**模式(pattern)**，但不能确定具体的SQL语句。比如这样一个场景：工作人员输入一个余额值，你要删除余额大于该值的所有账户。你有两种做法：一种是根据工作人员的输入**动态拼接**需要执行的SQL语句并交给Statement执行；第二种，也是更推荐的，使用PreparedStatement。  

> 如果使用动态拼接SQL，强烈建议将拼接出的SQL打印到日志上，方便查错。

#  

> PreparedStatement通过**预编译技术**能更高效、安全地执行动态SQL。  

使用PreparedStatement的方法是：  


1. 通过SQL语句获取PreparedStatement对象，你需要为SQL语句中的每个动态参数分配一个**桩(stub)**，用'**?**'表示。比如针对上述场景的SQL语句为`DELETE FROM account WHERE balance > ?`  

2. 在每次执行前为每一个桩绑定具体的参数。你需要使用<code>PreparedStatement::set<em>XXX</em>(int parameterIndex, <em>XXX</em> value)</code>方法来绑定参数。parameterIndex表示桩在SQL语句中的次序，<em>XXX</em>表示桩的数据类型，value表示要绑定的值。

3. 使用`PreparedStatement::executeUpdate()`方法（如果是INSERT/UPDATE/DELETE语句）或`PreparedStatement::executeQuery()`方法（如果是SELECT语句）执行SQL  

这是完整的数据删除代码：    

```java
PreparedStatement prepStat = connection.prepareStatement("DELETE FROM account WHERE balance > ?");
prepStat.setInt(1, 0); // 相当于执行"DELETE FROM account WHERE balance > 0"
int updates = prepStat.executeUpdate(); // 同样地，返回值表示受影响的Tuple数
```

数据更新类似数据插入和数据删除，我就不再赘述了。  
代码详见jdbc/src/main/java/jdbcDemo/JDBCDemo.java

最后，预祝各位同学实验顺利:smile: