package Model;

import java.io.Serializable;
import java.util.ArrayList;

public class PizzaOrder implements Serializable {
    private int orderId;
    private double orderTotalPrice;
    private int orderedBy;

    public PizzaOrder() {
    }

    public PizzaOrder(int orderId, double orderTotalPrice, int orderedBy, int staffID, ArrayList<Pizza> orderContent) {
        this.orderId = orderId;
        this.orderTotalPrice = orderTotalPrice;
        this.orderedBy = orderedBy;
        this.staffID = staffID;
        this.orderContent = orderContent;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(double orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    public int getOrderedBy() {
        return orderedBy;
    }

    public void setOrderedBy(int orderedBy) {
        this.orderedBy = orderedBy;
    }

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public ArrayList<Pizza> getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(ArrayList<Pizza> orderContent) {
        this.orderContent = orderContent;
    }

    private int staffID;
    private ArrayList<Pizza> orderContent;

}
