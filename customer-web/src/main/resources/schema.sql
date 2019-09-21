/* create database if not exists bookshop;
 use bookshop;
*/

drop table if exists books;
CREATE TABLE IF NOT EXISTS books ( 
   item_number INT NOT NULL, 
   title VARCHAR(120) NOT NULL,
   author VARCHAR(30) NOT NULL, 
   description VARCHAR(100),
   publication_date INT,
   checked_out BOOLEAN,
   current_owner int not null
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
