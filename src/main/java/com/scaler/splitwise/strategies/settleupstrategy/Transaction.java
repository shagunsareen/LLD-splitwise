package com.scaler.splitwise.strategies.settleupstrategy;

import com.scaler.splitwise.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
    private User from;
    private User to;
    private Long amount;
}
