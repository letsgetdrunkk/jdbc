CREATE DATABASE IF NOT EXISTS bank DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE TABLE IF NOT EXISTS bank.customer (
  id INT NOT NULL AUTO_INCREMENT ,
  name VARCHAR(20) NOT NULL ,
  age TINYINT NULL ,
  sex CHAR(1) NULL ,
  PRIMARY KEY (id) ,
  CHECK ( sex in ('F', 'M')) );

CREATE TABLE IF NOT EXISTS bank.account (
  id INT NOT NULL AUTO_INCREMENT ,
  account_num VARCHAR(19) NOT NULL ,
  balance DECIMAL(10,2) DEFAULT 0 ,
  customer_id INT NOT NULL ,
  PRIMARY KEY (id) ,
  INDEX customer_id (customer_id ASC) ,
  CONSTRAINT customer_id
    FOREIGN KEY (id)
    REFERENCES bank.customer (id )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
