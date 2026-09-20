package com.mithra.mithramart.model;

import java.math.BigDecimal;

public class Order {
    private long id;
    private long buyerId;
    private String status;
    private BigDecimal totalAmount;

    public Order() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getBuyerId() { return buyerId; }
    public void setBuyerId(long buyerId) { this.buyerId = buyerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}