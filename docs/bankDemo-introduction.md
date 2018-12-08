## 概述  
该项目实现了一个简化的银行系统的开户、存款、查询操作。
具体代码详见jdbc/src/main/java/jdbcDemo/BankDB.java  
以该项目为例，大家可以实现自己的数据库应用系统，大致框架可以借鉴本项目。
下面介绍如何在Intellij IDEA里面以本项目为基础，进行开发。

### 创建账户
通过接收用户的输入，处理后，作为参数，调用bank的`insertToCustomer`和`insertToAccount`方法，
更新customer和account两张表
* `insertToCustomer`方法  
参数为（id,name,age,sex）
以上述参数为value作为一条新的纪录插入到customer表中
* `insertToAccount`方法  
参数为（id,account_num,balance,customer_id）
以上述参数为value作为一条新的纪录插入到account表中

### 存款
接收用户的输入，处理后，调用bank的`addAccountBalance`方法，进行余额的更新  
* `addAccountBalance`方法的参数为(account_num,amount)  
首先查询对应账户的余额，然后加上amout作为新的balance更新account表

### 查询余额
接收用户的输入，处理后，调用bank的`queryBalance`方法，进行余额的查询，打印输出到屏幕  

然后大家可以参考本部分，实现自己设计的具体的应用系统。


