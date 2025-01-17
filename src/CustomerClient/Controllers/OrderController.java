package CustomerClient.Controllers;

import Model.Context;
import Model.Pizza;
import Model.PizzaOrder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

import static CustomerClient.Controllers.SignUpController.*;

public class OrderController implements Initializable {
    @FXML private ComboBox<String> orderTypeDropdown;
    @FXML private ComboBox<String> orderSizeDropdown;
    @FXML private TextField orderQuantityTxt;
    @FXML private ListView<VBox> ordersListView;
    @FXML private Label itemsInOrderQueue;
    @FXML private Label itemsInOrderTotal;
    public static String serverHostName = "localhost";
    public static int serverPortNumber = 59090;
    private Socket connectionSocket;
    public static ObjectInputStream objInStream;
    public static ObjectOutput objOutStream;
    int totalOrderPice = 0;
    PizzaOrder pizzaOrder = new PizzaOrder();
    Pizza singlePizzaOrder = new Pizza();
    ArrayList<Pizza> pizzaArrayList = new ArrayList<Pizza>();

    public void initialize(URL location, ResourceBundle resources) {
        String currentUserEmail = Context.getInstance().currentUser().getEmailAddress();
        orderTypeDropdown.getItems().removeAll(orderTypeDropdown.getItems());
        orderTypeDropdown.getItems().addAll("Hawaiian Craze", "Spicy Bacon Deluxe", "Spicy 3 Cheese", "Mushroom Cream Light");
        orderTypeDropdown.getSelectionModel().select("Hawaiian Craze");

        orderSizeDropdown.getItems().removeAll(orderSizeDropdown.getItems());
        orderSizeDropdown.getItems().addAll("Regular", "Medium", "Large");
        orderSizeDropdown.getSelectionModel().select("Regular");

        orderQuantityTxt.setText("1");
    }

    public void viewMenu() throws IOException {
        Parent pizzaMenu = FXMLLoader.load(getClass().getResource("../FXMLUserInterfaces/PizzaMenu.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Pizza Menu");
        stage.setScene(new Scene(pizzaMenu, 1350, 700));
        stage.show();
    }

    public void connect() {
        try {
            this.connectionSocket = new Socket(serverHostName, serverPortNumber);
            InputStream responseFromServer = this.connectionSocket.getInputStream();
            objInStream = new ObjectInputStream(responseFromServer);

            OutputStream requestToServer = this.connectionSocket.getOutputStream();
            objOutStream = new ObjectOutputStream(requestToServer);

        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateOrder() throws FileNotFoundException {
        int pizzaPice = 0;
        // Get order values
        String orderSize =  orderSizeDropdown.getValue();
        int orderQuantity =  Integer.parseInt(orderQuantityTxt.getText());
        String orderType = orderTypeDropdown.getValue();

        // Get order Label Values
        String orderTypeLabel = orderTypeDropdown.getValue();
        String orderSizeLabel = "Pizza Size: " + orderSizeDropdown.getValue();
        String orderQuantityLabel = "Ordered Quantity: " + orderQuantityTxt.getText();

        // Set Order Values
        Label orderListViewNo = new Label("Item #" + (ordersListView.getItems().size() + 1));
        Label orderListViewTitle = new Label(orderTypeLabel);
        Label orderListViewQuantity = new Label(String.valueOf(orderQuantityLabel));
        Label orderListViewSize = new Label(orderSizeLabel);

        ImageView orderListViewThumbnail;
        switch (orderType) {
            case "Hawaiian Craze":
                switch (orderSize) {
                    case "Regular":
                        totalOrderPice += orderQuantity * 199;
                        pizzaPice = 199;
                        break;
                    case "Medium":
                        totalOrderPice += orderQuantity * 299;
                        pizzaPice = 299;
                        break;
                    case "Large":
                        totalOrderPice += orderQuantity * 399;
                        pizzaPice = 399;
                        break;
                }
                orderListViewThumbnail = new ImageView(new Image("CustomerClient/FXMLUserInterfaces/Images/4.png"));
                break;
            case "Spicy Bacon Deluxe":
                switch (orderSize) {
                    case "Regular":
                        totalOrderPice += orderQuantity * 249;
                        pizzaPice = 249;
                        break;
                    case "Medium":
                        totalOrderPice += orderQuantity * 349;
                        pizzaPice = 349;
                        break;
                    case "Large":
                        totalOrderPice += orderQuantity * 449;
                        pizzaPice = 449;
                        break;
                }
                orderListViewThumbnail = new ImageView(new Image("CustomerClient/FXMLUserInterfaces/Images/6.png"));
                break;
            case "Spicy 3 Cheese":
                switch (orderSize) {
                    case "Regular":
                        totalOrderPice += orderQuantity * 149;
                        pizzaPice = 149;
                        break;
                    case "Medium":
                        totalOrderPice += orderQuantity * 249;
                        pizzaPice = 249;
                        break;
                    case "Large":
                        totalOrderPice += orderQuantity * 349;
                        pizzaPice = 349;
                        break;
                }
                orderListViewThumbnail = new ImageView(new Image("CustomerClient/FXMLUserInterfaces/Images/3.png"));
                break;
            default:
                switch (orderSize) {
                    case "Regular":
                        totalOrderPice += orderQuantity * 139;
                        pizzaPice = 139;
                        break;
                    case "Medium":
                        totalOrderPice += orderQuantity * 239;
                        pizzaPice = 239;
                        break;
                    case "Large":
                        totalOrderPice += orderQuantity * 339;
                        pizzaPice = 339;
                        break;
                }
                orderListViewThumbnail = new ImageView(new Image("CustomerClient/FXMLUserInterfaces/Images/5.png"));
                break;
        }
        String currentUserEmail = Context.getInstance().currentUser().getEmailAddress();
        int currentUserId = 0;
        try {
            // create a mysql database connection
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/Pizzalicious";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "");

            // create a sql date object so we can use it in our INSERT statement

            // the mysql insert statement
            String query = "SELECT userId FROM user WHERE email=?";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, currentUserEmail);
            ResultSet RS = preparedStmt.executeQuery();
            RS.next();
            currentUserId = RS.getInt("userId");

        } catch (Exception e) {
            System.err.println("Got an exception!");
            e.printStackTrace();
        }
        // Create a Pizza Object
        singlePizzaOrder = new Pizza(orderType, pizzaPice, orderQuantity, orderSize);
        pizzaArrayList.add(singlePizzaOrder);
        pizzaOrder = new PizzaOrder(1, totalOrderPice, currentUserId, 1, pizzaArrayList);

        orderListViewNo.setTextFill(Color.web("#FFFFFF"));
        orderListViewNo.setFont(new Font("Dubai", 14));

        orderListViewTitle.setTextFill(Color.web("#0076a3"));
        orderListViewQuantity.setTextFill(Color.web("#0076a3"));
        orderListViewSize.setTextFill(Color.web("#0076a3"));

        orderListViewThumbnail.setFitHeight(100);
        orderListViewThumbnail.setFitWidth(100);
        orderListViewThumbnail.preserveRatioProperty();

        VBox verticalNode = new VBox(orderListViewThumbnail, orderListViewTitle, orderListViewQuantity, orderListViewSize, orderListViewNo);
        verticalNode.setAlignment(Pos.BASELINE_CENTER);
        verticalNode.setPadding(new Insets(20 ,10 , 0, 10));
        ordersListView.setOrientation(Orientation.HORIZONTAL);
        ordersListView.getItems().add(verticalNode);
        ordersListView.setStyle("-fx-background-color:  #202124;");

        itemsInOrderQueue.setText(String.valueOf(ordersListView.getItems().size()));
        itemsInOrderTotal.setText(String.valueOf(totalOrderPice));
    }

    public void clearOrderList() {
        ordersListView.getItems().clear();
        itemsInOrderQueue.setText("0");
        itemsInOrderTotal.setText("0");
        totalOrderPice = 0;
    }

    public void confirmOrderList() throws IOException {
        connect();
        System.out.println(pizzaOrder.toString());
        objOutStream.writeUTF("order_create");
        objOutStream.flush();

        objOutStream.writeObject(pizzaOrder);
        objOutStream.flush();

        String svrStatus = (String) objInStream.readUTF();
        if (svrStatus.equals("created_ok")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pizzalicious Order Status");
            alert.setHeaderText("Order Created");
            alert.setContentText("Press OK to continue");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

}
