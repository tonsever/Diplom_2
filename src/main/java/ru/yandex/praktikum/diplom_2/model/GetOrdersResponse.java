package ru.yandex.praktikum.diplom_2.model;

import java.util.List;

public class GetOrdersResponse {
    private boolean success;
    private int total;
    private int totalToday;
    private List<OrderForGetOrdersResponse> orders;

    public GetOrdersResponse(boolean success, int total, int totalToday, List<OrderForGetOrdersResponse> orders) {
        this.success = success;
        this.total = total;
        this.totalToday = totalToday;
        this.orders = orders;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalToday() {
        return totalToday;
    }

    public void setTotalToday(int totalToday) {
        this.totalToday = totalToday;
    }

    public List<OrderForGetOrdersResponse> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderForGetOrdersResponse> orders) {
        this.orders = orders;
    }
}
