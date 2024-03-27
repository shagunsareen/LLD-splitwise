package com.scaler.splitwise.strategies.settleupstrategy;

import com.scaler.splitwise.models.Expense;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("bruteForceSettleUpStrategy")
public class BruteForceSettleUpStrategy implements SettleUpStrategy{
    @Override
    public List<Transaction> settle(List<Expense> expenseList) {
        return null;
    }
}
