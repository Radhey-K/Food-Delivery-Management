import java.sql.*;
import java.util.*;

public class Main {
    static Connection con = null;
    static PreparedStatement pst = null;
     public static void connect() {
         try {
             String url = "jdbc:mysql://localhost:3306/food_delivery";
             String uname = "root";
             String pass = "toor";
             Class.forName("com.mysql.cj.jdbc.Driver");
             con = DriverManager.getConnection(url, uname, pass);
         } catch(Exception e) {
             e.printStackTrace();
         }
     }

     public static void printAll(ResultSet rs) {
         try {
             ResultSetMetaData rsm = rs.getMetaData();
             for (int i = 0; i < rsm.getColumnCount(); i++) {
                 System.out.printf("%-20s", rsm.getColumnName(i + 1));
             }
             System.out.println();
             while (rs.next()) {
                 for (int i = 0; i < rsm.getColumnCount(); i++) {
                     System.out.printf("%-20s", rs.getString(i + 1));
                 }
                 System.out.println();
             }
             System.out.println();
         } catch(Exception e) {
             e.printStackTrace();
         }
     }


//     Add restaurant to DB
     public static void addRestaurant(String name, String address, long phoneNo, int pincode) {
         try {
             connect();
             String query = "insert into restaurant(restName, address, r_phoneNo, pincode) values (?,?,?,?)";
             pst = con.prepareStatement(query);
             pst.setString(1, name);
             pst.setString(2, address);
             pst.setLong(3, phoneNo);
             pst.setInt(4, pincode);
             int a = pst.executeUpdate();
             System.out.println("Restaurant added successfully");
         } catch(Exception e) {
             System.out.println("Couldn't add restaurant");
         }
     }


//  Add item to restaurant's menu
    public static void addToMenu(String restName, String itemName, int price) {
         try{
             connect();
             String query = "insert into offers(r_id, i_id, price) values ((select r_id from restaurant where restName like ?), (select i_id from item where itemName like ?), ?)";
             pst = con.prepareStatement(query);
             pst.setString(1, restName);
             pst.setString(2, itemName);
             pst.setInt(3, price);
             pst.executeUpdate();
             System.out.println("Inserted " + itemName + " into menu of " + restName + " for rupees " + price);
         } catch (Exception e) {
             System.out.println("Couldn't add " + itemName);
         }
    }


//    Change price of item on menu
    public static void changePrice(String restName, String itemName, int newPrice) {
         try {
             connect();
             String query = "update offers set price = ? where r_id = (select r_id from restaurant where restName like ?) and i_id = (select i_id from item where itemName like ?)";
             pst = con.prepareStatement(query);
             pst.setInt(1, newPrice);
             pst.setString(2, restName);
             pst.setString(3, itemName);
             pst.executeUpdate();
             System.out.println("Updated successfully");
         } catch (Exception e) {
             System.out.println("Couldn't update");
         }
    }


//    Signup new user and creating new profile
    public static void signUp(String username, String fname, String lname, String address, long phoneNo, String email, String pass) {
        try {
            connect();
            String query = "INSERT INTO customer VALUES (?, ?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, fname);
            pst.setString(3, lname);
            pst.setString(4, address);
            pst.setLong(5, phoneNo);
            pst.setString(6, email);
            pst.setString(7, pass);
            pst.executeUpdate();
            System.out.println("Signed up successfully");
        } catch(Exception e) {
            System.out.println("Could not sign up user.");
        }
    }


    public static int signIn(String username, String pass) {
        try {
            connect();
            String query = "select exists (select * from customer where username=? and password=?)";
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, pass);
            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static void updateCustomerPassword(String username, long phoneNo, String password) {
        try {
            connect();
            String query = "UPDATE customer SET password = ? WHERE username = ? AND phoneNo = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, password);
            pst.setString(2, username);
            pst.setLong(3, phoneNo);

            int a = pst.executeUpdate();
            if (a == 1) System.out.println("Password changed successfully");
            else System.out.println("Password not changed");
        } catch (SQLException e) {
            System.out.println("Password not changed");
        }
    }


    public static void searchRestaurantByName(String name) {
        try {
            connect();
            String query = "SELECT restName, address, r_phoneNo, pincode FROM restaurant WHERE restName like ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, "%" + name + "%");
            ResultSet rs = pst.executeQuery();
            printAll(rs);
        } catch (SQLException e) {
            System.out.println("No restaurant found");
        }
    }


    public static void searchRestaurantByPincode(int pincode) {
        try {
            connect();
            String query = "SELECT restName, address, r_phoneNo, pincode FROM restaurant WHERE pincode=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, pincode);
            ResultSet rs = pst.executeQuery();
            printAll(rs);
        } catch (SQLException e) {
            System.out.println("No restaurant found");
        }
    }


    public static void searchRestaurantByItem(String itemName) {
        try {
            connect();
            String query = "select restName, address, r_phoneNo, pincode from offers natural join restaurant natural join item where item.itemName like ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, itemName);
            ResultSet rs = pst.executeQuery();
            printAll(rs);
        } catch (SQLException e) {
            System.out.println("No restaurant found");
        }
    }


    public static void getMenu(String restName) {
        try {
            connect();
            PreparedStatement pst = Main.con.prepareStatement("select itemName, price from restaurant natural join offers natural join item where restName like ?");
            pst.setString(1, restName + "%");
            ResultSet rs = pst.executeQuery();
            printAll(rs);
        } catch(Exception e) {
            System.out.println("Could not get menu for " + restName);
        }
    }


    public static void viewOrderStatus(String username) {
        try {
            connect();
            PreparedStatement pst = Main.con.prepareStatement("select o_id, o_date, o_time, o_status from orders natural join places where username = ?");
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            printAll(rs);
        } catch(Exception e) {
            System.out.println("Couldn't retrieve order status");
        }
    }


    public static void addStaff(String restName, String staffName, int salary) {
        try {
            connect();
            String query = "INSERT INTO employs VALUES ((SELECT r_id FROM restaurant WHERE restName LIKE ?), (SELECT s_id FROM staff WHERE staffName LIKE ?), ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, restName + "%");
            pst.setString(2, staffName + "%");
            pst.setInt(3, salary);
            pst.executeUpdate();
            System.out.println(staffName + " added successfully");
        } catch (SQLException e) {
            System.out.println("Could not add staff");
        }
    }

    public static void placeOrder(String username, String[] items, String restName) {
        try {
            connect();
            String query;

            // insert into orders table
            query = "INSERT INTO orders (o_date, o_time, o_status) VALUES (CURDATE(), NOW(), 'Processing')";
            pst = con.prepareStatement(query);
            pst.executeUpdate();
            pst.close();

            // create view most_recent
            query = "DROP VIEW IF EXISTS most_recent";
            pst = con.prepareStatement(query);
            pst.executeUpdate();
            pst.close();

            query = "CREATE VIEW most_recent AS (SELECT MAX(o_id) FROM orders)";
            pst = con.prepareStatement(query);
            pst.executeUpdate();
            pst.close();

            // insert into places table
            query = "INSERT INTO places VALUES (?, (SELECT o_id FROM orders WHERE o_id = (SELECT * FROM most_recent)))";
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.executeUpdate();
            pst.close();

            // insert into containss table for each item
            query = "INSERT INTO containss VALUES ";
            for (String item : items) {
                query += "((SELECT o_id FROM orders WHERE o_id = (SELECT * FROM most_recent)), (SELECT i_id FROM item WHERE itemName LIKE ?)), ";
            }
            query = query.substring(0, query.length() - 2); // remove the last comma and space
            pst = con.prepareStatement(query);
            int i = 1;
            for (String item : items) {
                pst.setString(i++, item + "%");
            }
            pst.executeUpdate();
            pst.close();

            // Check if restaurant has available staff
            query = "select exists(select s_id from restaurant natural join employs natural join staff where restName like ? and available like 'yes')";
            pst = con.prepareStatement(query);
            pst.setString(1, restName);
            ResultSet rs = pst.executeQuery();
            rs.next();
            int a = rs.getInt(1);
            if (a == 0) {   // If restaurant has no staff, set order status to rejected
                query = "update orders set o_status = 'Rejected' where o_id = (select * from most_recent)";
                pst = con.prepareStatement(query);
                pst.executeUpdate();
                return;
            }

            // insert into prepares table
            query = "INSERT INTO prepares VALUES ((SELECT r_id FROM restaurant WHERE restName LIKE ?), (SELECT o_id FROM orders WHERE o_id = (SELECT * FROM most_recent)))";
            pst = con.prepareStatement(query);
            pst.setString(1, restName + "%");
            pst.executeUpdate();
            pst.close();

            // insert into delivers table
            query = "INSERT INTO delivers VALUES ((SELECT s_id FROM employs NATURAL JOIN staff NATURAL JOIN restaurant WHERE restName LIKE ? AND available LIKE 'yes' LIMIT 1), (SELECT o_id FROM orders WHERE o_id = (SELECT * FROM most_recent)), TIMESTAMP(DATE_ADD(NOW(), INTERVAL FLOOR(RAND()*(20-10+1)+10) MINUTE)))";
            pst = con.prepareStatement(query);
            pst.setString(1, restName + "%");
            pst.executeUpdate();
            pst.close();

            query = "Update staff set available = 'no' where s_id = (select s_id from delivers natural join orders where o_id = (SELECT * FROM most_recent))";
            pst = con.prepareStatement(query);
            pst.executeUpdate();
            pst.close();

            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            query = "Update staff set available = 'yes' where s_id = (select s_id from delivers natural join orders where o_id = (SELECT * FROM most_recent))";
            pst = con.prepareStatement(query);
            pst.executeUpdate();
            pst.close();

            query = "update orders set o_status = 'Delivered' where o_id = (select * from most_recent)";
            pst = con.prepareStatement(query);
            pst.executeUpdate();
            pst.close();


            System.out.println("Order placed successfully!");

            con.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void viewStaff(String restName) {
        try {
            connect();
            String query = "select r_id from restaurant where restName like ?";
            pst = con.prepareStatement(query);
            pst.setString(1, restName);
            ResultSet rs = pst.executeQuery();
            rs.next();
            int r_id = rs.getInt(1);

            query = "select staffName, s_phoneNo, available from employs natural join staff where r_id = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, r_id);
            rs = pst.executeQuery();
            printAll(rs);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public static void orderReport(String restName) {
//        Avergae delivery time
        try {
            connect();
            String query = "select (avg(timestampdiff(minute, o_time, delivery_time))) as 'Average Delivery time' from prepares natural join delivers natural join orders natural join restaurant where restName like ?";
            pst = con.prepareStatement(query);
            pst.setString(1, restName);
            ResultSet rs = pst.executeQuery();
            printAll(rs);

            query = "select case\n" +
                    "when hour(o_time) >= 6 and hour(o_time) < 12 then 'Morning'\n" +
                    "when hour(o_time) >= 12 and hour(o_time) < 18 then 'Afternoon'\n" +
                    "else 'Evening'\n" +
                    "end as TimeOfDay,\n" +
                    "count(*) as Count\n" +
                    "from prepares natural join delivers natural join orders natural join restaurant where restName like ? group by TimeOfDay";
            pst = con.prepareStatement(query);
            pst.setString(1, restName);
            rs = pst.executeQuery();
            printAll(rs);


            query = "select (order_count * total_staff) / Average_Delivery_time as 'Productivity' from (select count(*) as 'order_count', (avg(timestampdiff(minute, o_time, delivery_time))) as 'Average_Delivery_time', (select count(*) from restaurant natural join employs natural join staff where restName like ?) as 'total_staff' from prepares natural join delivers natural join orders natural join restaurant where restName like ?) as temp";
            pst = con.prepareStatement(query);
            pst.setString(1, restName);
            pst.setString(2, restName);
            rs = pst.executeQuery();
            printAll(rs);

            query = "select sum(price) as 'Total Revenue' from restaurant natural join offers natural join item where i_id in (select i_id from orders natural join containss natural join item natural join prepares natural join restaurant where restName like ?) and restName like ?";
            pst = con.prepareStatement(query);
            pst.setString(1, restName);
            pst.setString(2, restName);
            rs = pst.executeQuery();
            printAll(rs);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void non_emp_staff(String restname){
         try{
             connect();
             String query = "select staffname from staff where staffname not in (select staffname from restaurant natural join employs natural join staff where restname = ?)";
             pst = con.prepareStatement(query);
             pst.setString(1, restname);
             ResultSet rs = pst.executeQuery();
             System.out.println("Available staff to add:");
             printAll(rs);
         }catch(Exception e){
             e.printStackTrace();
         }
    }


    public static void main(String[] args) {
//        addRestaurant("Annapurna", "C'not right middle", 7885422369L, 411011);
//        addToMenu("Annapurna", "Pizza", 200);
//        changePrice("Annapurna", "Pizza", 300);
//        signUp("V_Kabra", "Vedant", "Kabra", "VY 2121, Pilani", 7788996655L, "vk@gmail.com", "vk1234");
//        updateCustomerPassword("V_Kabra", 7788996655L, "vk1234");
//        searchRestaurantByName("Pizzeria");
//        searchRestaurantByPincode(411001);
//        searchRestaurantByItem("Sushi");
//        getMenu("BBQ Green");
//        viewOrderStatus("A_Waikar");
//        addStaff("Pizzeria", "Vikas Shah", 9000);
//        String[] items = {"Sushi", "Tacos"};
//        placeOrder("A_Sawant", items, "BBQ Green");
//        viewStaff("BBQ Green");
//        orderReport("Pizzeria");
//        System.out.println(signIn("A_Sawant", "as1234"));
//        non_emp_staff("Pizzeria");

        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("Enter 1 if you are a Customer\nEnter 2 if you are a restaurant owner\nEnter 3 to exit");
            int a;
            try {
                a = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Incorrect option entered");
                return;
            }
            if (a == 3) {
                System.out.println("logging off");
                break;
            }
            if (a == 1) {
                String username, pass;
                System.out.println("Enter 1 to sign in as existing customer\nEnter 2 to sign up as new customer");
                a = Integer.parseInt(sc.nextLine());
                if (a == 2) {
                    String fname, lname, address, email;
                    Long phoneNo;
                    System.out.print("Username: ");
                    username = sc.nextLine();
                    System.out.print("first name: ");
                    fname = sc.nextLine();
                    System.out.print("last name: ");
                    lname = sc.nextLine();
                    System.out.print("Address: ");
                    address = sc.nextLine();
                    System.out.print("phone number: ");
                    phoneNo = sc.nextLong();
                    sc.nextLine();
                    System.out.print("email: ");
                    email = sc.nextLine();
                    System.out.print("password: ");
                    pass = sc.nextLine();
                    signUp(username, fname, lname, address, phoneNo, email, pass);
                    continue;
                } else if (a == 1) {
                    System.out.print("Username: ");
                    username = sc.nextLine();
                    System.out.print("password: ");
                    pass = sc.nextLine();
                    int exists = signIn(username, pass);
                    if (exists == 0) {
                        System.out.println("sign in failed");
                    } else if (exists == 1) {
                        while (true) {
                            System.out.println("Enter 1 to browse restaurants\nEnter 2 to browse menu\nEnter 3 to change password\nEnter 4 to view order history\nEnter 5 to sign out");
                            a = Integer.parseInt(sc.nextLine());
                            if (a == 5) break;
                            if (a == 1) {
                                System.out.println("Enter 1 to browse by name\nenter 2 to browse by pincode\nenter 3 to browse by food item");
                                a = Integer.parseInt(sc.nextLine());
                                if (a == 1) {
                                    System.out.print("Restaurant Name: ");
                                    String restname = sc.nextLine();
                                    searchRestaurantByName(restname);
                                } else if (a == 2) {
                                    System.out.print("Pincode: ");
                                    int pincode = Integer.parseInt(sc.nextLine());
                                    searchRestaurantByPincode(pincode);
                                } else if (a == 3) {
                                    System.out.print("item Name: ");
                                    String item = sc.nextLine();
                                    searchRestaurantByItem(item);
                                }
                            } else if (a == 2) {
                                System.out.print("Restaurant Name: ");
                                String restname = sc.nextLine();
                                getMenu(restname);
                                System.out.println("Enter 1 to order\nEnter 2 to exit");
                                a = Integer.parseInt(sc.nextLine());
                                if (a == 2) continue;
                                if (a == 1) {

                                    System.out.print("Enter items separated by comma, no spaces; ");
                                    String items = sc.nextLine();
                                    String[] itemsarr = items.split(",");
                                    placeOrder(username, itemsarr, restname);
                                }
                            } else if (a == 3) {
                                System.out.print("Enter new password: ");
                                String newpass = sc.nextLine();
                                System.out.print("Enter phone number as security question: ");
                                Long phoneno = Long.parseLong(sc.nextLine());
                                updateCustomerPassword(username, phoneno, newpass);
                            } else if (a == 4) {
                                viewOrderStatus(username);
                            }
                        }
                    }
                }
            } else if (a == 2) {
                while(true){
                    System.out.println("Enter 1 to add new restaurant\nEnter 2 to sign in to existing restaurant\nEnter 3 to exit");
                    a = Integer.parseInt(sc.nextLine());
                    if(a==3)break;
                    if (a == 1) {
                        String restname, address;
                        Long phoneno;
                        int pincode;
                        System.out.print("Restaurant name: ");
                        restname = sc.nextLine();
                        System.out.println("address: ");
                        address = sc.nextLine();
                        System.out.println("phone number: ");
                        phoneno = Long.parseLong(sc.nextLine());
                        System.out.println("pincode : ");
                        pincode = Integer.parseInt(sc.nextLine());
                        addRestaurant(restname, address, phoneno, pincode);
                    } else if (a == 2) {
                        String restname;
                        System.out.print("Restaurant name:");
                        restname = sc.nextLine();
                        while(true){
                            System.out.println("Enter 1 to add item to menu\nEnter 2 to change price of an item\nEnter 3 to add staff\nEnter 4 to view employees\nEnter 5 to generate order report\nEnter 6 to view menu\nEnter 7 to sign out");
                            a = Integer.parseInt(sc.nextLine());
                            if(a==7)break;
                            if(a==1){
                                getMenu(restname);
                                System.out.print("Enter new item to be added:");
                                String item = sc.nextLine();
                                System.out.print("Enter Price:");
                                int price = Integer.parseInt(sc.nextLine());
                                addToMenu(restname, item, price);
                            }else if(a==2){
                                getMenu(restname);
                                System.out.print("item: ");
                                String item = sc.nextLine();
                                System.out.print("Enter new price: ");
                                int price = Integer.parseInt(sc.nextLine());
                                changePrice(restname, item, price);
                            }else if(a==3){
                                non_emp_staff(restname);
                                System.out.print("Staffname to be added:");
                                String staffname = sc.nextLine();
                                System.out.print("Salary:");
                                int salary = Integer.parseInt(sc.nextLine());
                                addStaff(restname, staffname, salary);
                            }else if(a==4){
                                viewStaff(restname);
                            }else if(a==5){
                                orderReport(restname);
                            } else if (a == 6) {
                                getMenu(restname);
                            }
                        }
                    }
                }
            }
        }
    }
}