use food_delivery;

-- To add new restaurant to database
insert into restaurant(restName, address, r_phoneNo, pincode) values 
('Annapurna', 'C\'not right middle', 7885422369, 411011);

-- To add a new item to a restaurant's menu. Eg: TOTT(id 3) now offers Salmon(id 10) for 150 rupees
insert into offers(r_id, i_id, price) values
((select r_id from restaurant where restName like 'TOTT'), (select i_id from item where itemName like 'Salmon'), 150);

-- To change the price of an item on a restaurant's menu. Eg Salmon at TOTT is now 200 rupees
update offers set price = 200 where r_id = (select r_id from restaurant where restName like 'TOTT') and i_id = (select i_id from item where itemName like 'Salmon');

-- signup a new user and create new profile
insert into customer values
('V_Kabra', 'Vedant', 'Kabra', 'VY 2121, Pilani', 7788888552, 'vk@gmail.com', 'vk1234');

-- Signin authentication. Eg. someone wants to sign in with username A_Sawant and the right password
select if((select count(*) from customer where username='A_Sawant' and password='as123'), 'Authenticated Successfully', 'Authentication failed') as Authentication;

-- To reset password with phone number as security question. Eg. A_Sawant wants to change password to as12345. If wrong phone number is entered, no changes are made to database
update customer set password='as12345' where username='A_Sawant' and phoneNo='4444444444';

-- To update user address. Eg. A_Sawant shifts to Meera 314.
update customer set address='MR 314, Pilani' where username='A_Sawant' and password='as1234';

-- To update user phone number
update customer set phoneNo='8899665544' where username='A_Sawant' and password='as1234';

-- To search restaurants by name.
select restName, address, r_phoneNo, pincode from restaurant where restName like 'Da Cafe Crunch';

-- To search restaurants by pincode
select restName, address, r_phoneNo, pincode from restaurant where pincode=411001;

-- To search restaurants based on food items offered. Eg: search restaurants offering pizzas(id 1)
select restName, address, r_phoneNo, pincode from offers natural join restaurant natural join item where item.itemName like 'Pizza';

-- To diplay a restaurant's menu. Eg: View Pizzeria's menu
select itemName, price from restaurant natural join offers natural join item where restName='Pizzeria';

-- If a user places an order. Eg: A_Sawant orders sushi(id 3) and tacos(id 4) from BBQ Green(id 9)
insert into orders(o_date, o_time, o_status) values (curdate(), now(), 'Processing');
drop view if exists most_recent;
create view most_recent as (select max(o_id) from orders);       -- most recent refers to the above inserted order ID 
insert into places values('A_Sawant', (select o_id from orders where o_id = (select * from most_recent)));
insert into containss values ((select o_id from orders where o_id = (select * from most_recent)), (select i_id from item where itemName like 'sushi')), ((select o_id from orders where o_id = (select * from most_recent)), (select i_id from item where itemName like 'tacos'));
insert into prepares values ((select r_id from restaurant where restName like 'BBQ%'), (select o_id from orders where o_id = (select * from most_recent)));
insert into delivers values ((select s_id from employs natural join staff natural join restaurant where restName like 'BBQ%' and available like 'yes' limit 1), (select o_id from orders where o_id = (select * from most_recent)), TIMESTAMP(DATE_ADD(NOW(), INTERVAL FLOOR(RAND()*(20-10+1)+10) MINUTE)));

-- To retrieve order status. Eg: A_Sawant wants to view order status of his most recent order
select o_date, o_time, o_status from orders natural join places where username = 'A_Sawant' and o_id = (select max(o_id) from places where username = 'A_Sawant');

-- Restaurants to insert new delivery personnel. Eg: Da Cafe Crunch(id 1) wants to employ Vikas Shah(id 6) for salary 9000
insert into employs values ((select r_id from restaurant where restName like 'Da%'), (select s_id from staff where staffName like 'Vikas%'), 9000);

-- Restaurants to view currently employed staff. Pizzeria to view staff
select staffName, s_phoneNo, available from restaurant natural join employs natural join staff where restName like 'Pizz%';

-- For restaurant to view average delivery time
select (avg(timestampdiff(minute, o_time, delivery_time))) as 'Average Delivery time' from prepares natural join delivers natural join orders natural join restaurant where restName like 'Pizz%';

-- For restaurants to view productivity
select (order_count * total_staff) / Average_Delivery_time as 'Productivity' from (select count(*) as 'order_count', (avg(timestampdiff(minute, o_time, delivery_time))) as 'Average_Delivery_time', (select count(*) from restaurant natural join employs natural join staff where restName like 'Pizz%') as 'total_staff' from prepares natural join delivers natural join orders natural join restaurant where restName like 'Pizz%') as temp;

-- For restaurant to view total revenue 
select sum(price) as 'Total Revenue' from restaurant natural join offers natural join item where i_id in (select i_id from orders natural join containss natural join item natural join prepares natural join restaurant where restName like 'Pizz%') and restName like 'Pizz%';
