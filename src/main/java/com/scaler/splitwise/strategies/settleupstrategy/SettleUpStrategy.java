package com.scaler.splitwise.strategies.settleupstrategy;

import com.scaler.splitwise.models.Expense;

import java.util.List;

public interface SettleUpStrategy {
    List<Transaction> settle(List<Expense> expenseList);
}
