package Server;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mysql.jdbc.ResultSetMetaData;
import static java.lang.Integer.parseInt;

import Model.*;

public class Server implements Runnable {
    public static Socket connetionSocket;
    public static ObjectOutputStream outToClient;
    public static ObjectInputStream inFromClient;

    public void run() {

        try {
            ServerSocket listeningSocket = null;
            listeningSocket = new ServerSocket(59090);
            System.out.println("Server is up & running on port: " + listeningSocket.getLocalPort());

            while (true) {
                try {
                    connetionSocket = listeningSocket.accept();
                    System.out.println("CLIENT CONNECTED");

                    outToClient = new ObjectOutputStream(connetionSocket.getOutputStream()); // get the output stream of client.
                    inFromClient = new ObjectInputStream(connetionSocket.getInputStream());

                    String action = (String) inFromClient.readUTF();
                    switch (action) {
                        case "user_create":

                            User userSignUp = (User) inFromClient.readObject();
                            String signUpEmail = userSignUp.getEmailAddress();
                            String signUpAddress = userSignUp.getAddress();
                            String signUpFirstName = userSignUp.getFirstName();
                            String signUpLastName = userSignUp.getLastName();
                            String signUpPhoneNo = userSignUp.getPhoneNo();
                            String signUpPassword = userSignUp.getPassword();
                            try {
                                // create a mysql database connection
                                String myDriver = "org.gjt.mm.mysql.Driver";
                                String myUrl = "jdbc:mysql://localhost:3306/pizzalicious";
                                Class.forName(myDriver);
                                Connection conn = DriverManager.getConnection(myUrl, "root", "");

                                // create a sql date object so we can use it in our INSERT statement

                                // the mysql insert statement
                                String query = " insert into user (firstName, lastName, email, password, address, phoneNo)"
                                        + " values (?, ?, ?, ?, ?, ?)";

                                // create the mysql insert preparedstatement
                                PreparedStatement preparedStmt = conn.prepareStatement(query);
                                preparedStmt.setString(1, signUpFirstName);
                                preparedStmt.setString(2, signUpLastName);
                                preparedStmt.setString(3, signUpEmail);
                                preparedStmt.setString(4, signUpPassword);
                                preparedStmt.setString(5, signUpAddress);
                                preparedStmt.setString(6, signUpPhoneNo);

                                // execute the preparedstatement
                                preparedStmt.execute();
                                outToClient.writeUTF("sign_up_ok");
                                outToClient.flush();

                                conn.close();
                            } catch (Exception e) {
                                outToClient.writeUTF("sign_in_failure");
                                System.err.println("Got an exception!");
                                System.err.println(e.getMessage());
                            }// try for database

                            break; //user create
                        case "user_signin":
                            //System.out.println("ok");
                            String signInInfo = (String) inFromClient.readUTF();
                            String [] signInInfoArray = signInInfo.split("\t");
                            String signInEmail = signInInfoArray[0];
                            String signInPassword = signInInfoArray[1];
                            System.out.println(signInEmail + " " + signInPassword);
                            try {
                                // create a mysql database connection
                                String myDriver = "org.gjt.mm.mysql.Driver";
                                String myUrl = "jdbc:mysql://localhost:3306/pizzalicious";
                                Class.forName(myDriver);
                                Connection conn = DriverManager.getConnection(myUrl, "root", "");

                                // create a sql date object so we can use it in our INSERT statement

                                // the mysql insert statement
                                String query = "SELECT count(email) AS n FROM user WHERE email=? AND password=?";

                                // create the mysql insert preparedstatement
                                PreparedStatement preparedStmt = conn.prepareStatement(query);
                                preparedStmt.setString(1, signInEmail);
                                preparedStmt.setString(2, signInPassword);

                                // execute the preparedstatement


                                ResultSet RS = preparedStmt.executeQuery();
                                RS.next();
                                String countN = RS.getString("n");
                                int n = parseInt(countN);
                                if (n == 1){
                                    System.out.println("signed in");
                                    outToClient.writeUTF("sign_in_ok");
                                    outToClient.flush();
                                } else {
                                    outToClient.writeUTF("sign_in_failure");
                                    outToClient.flush();
                                    System.out.println("sign in failed");
                                }

                                conn.close();
                            } catch (Exception e) {
                                outToClient.writeUTF("sign_in_failure");
                                System.err.println("Got an exception!");
                                System.err.println(e.getMessage());
                            }// try for database
                            break;//user_signin
                        case "order_create":
                            PizzaOrder P1 = (PizzaOrder) inFromClient.readObject();
                            int orderid = P1.getOrderId();
                            double orderTotalPrice = P1.getOrderTotalPrice();
                            int orderedBy= P1.getOrderedBy();
                            int staffID =P1.getStaffID();
                            ArrayList<Pizza> orderContent = P1.getOrderContent();
                            String pizzaContent = orderContent.toString();
                            try {
                                // create a mysql database connection
                                String myDriver = "org.gjt.mm.mysql.Driver";
                                String myUrl = "jdbc:mysql://localhost:3306/pizzalicious";
                                Class.forName(myDriver);
                                Connection conn = DriverManager.getConnection(myUrl, "root", "");

                                // create a sql date object so we can use it in our INSERT statement

                                // the mysql insert statement
                                String query = " insert into pizzaorder (orderTotalPrice, orderBy, orderContent,staffId)"
                                        + " values (?, ?, ?, ?)";

                                // create the mysql insert preparedstatement
                                PreparedStatement preparedStmt = conn.prepareStatement(query);
                                preparedStmt.setDouble(1,orderTotalPrice );
                                preparedStmt.setInt(2,orderedBy);
                                preparedStmt.setString(3, pizzaContent);
                                preparedStmt.setInt(4, staffID);

                                // execute the preparedstatement
                                preparedStmt.execute();

                                conn.close();
                            } catch (Exception e) {
                                System.err.println("Got an exception!");
                                System.err.println(e.getMessage());
                            }// try for database

                            break;//order create
                        case "order_read":

                    }// switch
                } catch (IOException i) {

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } // end of while
        }catch (IOException e){

        } //
    }



    public static void main(String[] args) {
        Thread PizzaServer = new Thread(new Server());
        PizzaServer.start();
        System.out.println("Multi-threaded Server starting...");
    }
}
