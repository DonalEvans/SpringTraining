/* create database if not exists bookshop;
 use bookshop;
*/

drop table if exists books;
CREATE TABLE IF NOT EXISTS books ( 
   item_number INT NOT NULL, 
   title VARCHAR(120) NOT NULL,
   author VARCHAR(30) NOT NULL, 
   description VARCHAR(100), 
   retail_cost FLOAT NOT NULL,
   publication_date INT
);
alter table books add constraint book_pk primary key (item_number);

drop table if exists customers;
create table if not exists customers (
   customer_number int not null,
   first_name varchar(20) not null,
   last_name varchar(20) not null,
   telephone_number varchar(15),
   address_line1 varchar(30),
   address_line2 varchar(30),
   address_line3 varchar(30),
   city varchar(30),
   state varchar(25),
   postalcode varchar(10),
   country varchar(15)
);
alter table customers add constraint customer_pk primary key(customer_number);

drop table if exists book_orders;
create table if not exists book_orders (
   order_number int not null,
   order_date date not null,
   shipping_cost float not null,
   ship_date date,
   customer_number int not null,
   total_price float
);
alter table book_orders add constraint book_order_pk primary key (order_number);

drop table if exists order_items;
create table if not exists order_items (
  order_line int not null,
  order_number int not null,
  item_number int not null,
  quantity int,
  discount float

);
alter table order_items add constraint order_items_pk primary key (order_number, order_line);
