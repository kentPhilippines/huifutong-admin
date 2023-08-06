package com.ruoyi.common.core.domain;

import java.io.Serializable;

public class WitStatisticsEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 5547968516186055805L;
    private String userId;
    private String totalSuccessOrders;
    private String totalPendingOrders;
    private String totalFailureOrders;
    private String totalTransactions;
    private String totalPendingAmount;
    private String totalSuccessAmount;
    private String totalFailureAmount;
    private String totalFee;
    private String totalPendingActualAmount;
    private String totalSuccessActualAmount;
    private String totalFailureActualAmount;

    public String getTotalSuccessOrders() {
        return totalSuccessOrders;
    }

    public void setTotalSuccessOrders(String totalSuccessOrders) {
        this.totalSuccessOrders = totalSuccessOrders;
    }

    public String getTotalPendingOrders() {
        return totalPendingOrders;
    }

    public void setTotalPendingOrders(String totalPendingOrders) {
        this.totalPendingOrders = totalPendingOrders;
    }

    public String getTotalFailureOrders() {
        return totalFailureOrders;
    }

    public void setTotalFailureOrders(String totalFailureOrders) {
        this.totalFailureOrders = totalFailureOrders;
    }

    public String getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(String totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public String getTotalPendingAmount() {
        return totalPendingAmount;
    }

    public void setTotalPendingAmount(String totalPendingAmount) {
        this.totalPendingAmount = totalPendingAmount;
    }

    public String getTotalSuccessAmount() {
        return totalSuccessAmount;
    }

    public void setTotalSuccessAmount(String totalSuccessAmount) {
        this.totalSuccessAmount = totalSuccessAmount;
    }

    public String getTotalFailureAmount() {
        return totalFailureAmount;
    }

    public void setTotalFailureAmount(String totalFailureAmount) {
        this.totalFailureAmount = totalFailureAmount;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getTotalPendingActualAmount() {
        return totalPendingActualAmount;
    }

    public void setTotalPendingActualAmount(String totalPendingActualAmount) {
        this.totalPendingActualAmount = totalPendingActualAmount;
    }

    public String getTotalSuccessActualAmount() {
        return totalSuccessActualAmount;
    }

    public void setTotalSuccessActualAmount(String totalSuccessActualAmount) {
        this.totalSuccessActualAmount = totalSuccessActualAmount;
    }

    public String getTotalFailureActualAmount() {
        return totalFailureActualAmount;
    }

    public void setTotalFailureActualAmount(String totalFailureActualAmount) {
        this.totalFailureActualAmount = totalFailureActualAmount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "WitStatisticsEntity{" +
                "totalSuccessOrders='" + totalSuccessOrders + '\'' +
                ", totalPendingOrders='" + totalPendingOrders + '\'' +
                ", totalFailureOrders='" + totalFailureOrders + '\'' +
                ", totalTransactions='" + totalTransactions + '\'' +
                ", totalPendingAmount='" + totalPendingAmount + '\'' +
                ", totalSuccessAmount='" + totalSuccessAmount + '\'' +
                ", totalFailureAmount='" + totalFailureAmount + '\'' +
                ", totalFee='" + totalFee + '\'' +
                ", totalPendingActualAmount='" + totalPendingActualAmount + '\'' +
                ", totalSuccessActualAmount='" + totalSuccessActualAmount + '\'' +
                ", totalFailureActualAmount='" + totalFailureActualAmount + '\'' +
                '}';
    }
}
