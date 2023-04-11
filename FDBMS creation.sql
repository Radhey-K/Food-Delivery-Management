create database if not exists food_delivery;
use food_delivery;

drop table if exists Containss;
drop table if exists Employs;
drop table if exists Delivers;
drop table if exists Offers;
drop table if exists Prepares;
drop table if exists Places;
drop table if exists Customer;
drop table if exists Staff;
drop table if exists Restaurant;
drop table if exists Item;
drop table if exists Orders;

create table Customer (
	username varchar(15) not null,
    fname varchar(15) not null default '',
    lname varchar(15) not null default '',
    address varchar(50) not null,
    phoneNo numeric(10,0) not null,
    emailId varchar(30) not null default '',
    password varchar(20) not null,
    primary key (username)
);

create table Orders (
	o_id int unsigned not null AUTO_INCREMENT,
    o_date date not null,
    o_time datetime not null,
    o_status enum('Processing', 'Delivered', 'Rejected'),
    primary key(o_id)
);

create table Item(
	i_id int unsigned not null AUTO_INCREMENT,
    itemName varchar(20) not null,
    primary key(i_id)
);

create table Restaurant(
	r_id int unsigned not null AUTO_INCREMENT,
    restName varchar(30) not null unique,
    address varchar(50) not null,
    r_phoneNo numeric(10,0) not null,
    pincode numeric(6,0) not null,
    primary key(r_id)
);

create table Staff(
	s_id int unsigned not null AUTO_INCREMENT,
    staffName varchar(30) not null,
    s_phoneNo numeric(10,0) not null,
    available enum('yes', 'no') default 'yes',
    primary key(s_id)
);

create table Places(
    username varchar(15),
    o_id int unsigned,
    CONSTRAINT orderFK foreign key (o_id) references orders(o_id),
    constraint custFK foreign key (username) REFERENCES customer(username),
    primary key (o_id)
);

create table Prepares(
    r_id int unsigned,
    o_id int unsigned,
    CONSTRAINT order_rest_FK foreign key (o_id) references orders(o_id),
    constraint restFK foreign key (r_id) REFERENCES restaurant(r_id),
    primary key (o_id)
);

create table Offers(
	r_id int unsigned,
	i_id int unsigned,
    price numeric(7,2) not null default 0.0,
    CONSTRAINT itemFK foreign key (i_id) references item(i_id),
    constraint rest_item_FK foreign key (r_id) REFERENCES restaurant(r_id),
    primary key (i_id, r_id)
);

create table Delivers(
	s_id int unsigned,
    o_id int unsigned,
    delivery_time timestamp not null,
    CONSTRAINT staffFK foreign key (s_id) references staff(s_id),
    constraint order_staff_FK foreign key (o_id) REFERENCES orders(o_id),
    primary key (o_id)
);

create table Employs(
	r_id int unsigned,
	s_id int unsigned,
    salary numeric(8,2),
    CONSTRAINT staff_rest_FK foreign key (s_id) references staff(s_id),
    constraint rest_staff_FK foreign key (r_id) REFERENCES restaurant(r_id),
    PRIMARY KEY (r_id, s_id)
);

create table Containss(
	o_id int unsigned,
	i_id int unsigned,
    CONSTRAINT item_order_FK foreign key (i_id) references item(i_id),
    constraint order_item_FK foreign key (o_id) REFERENCES orders(o_id),
    primary key (o_id, i_id)
);