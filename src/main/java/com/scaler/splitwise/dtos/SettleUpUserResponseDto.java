package com.scaler.splitwise.dtos;

import com.scaler.splitwise.strategies.settleupstrategy.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SettleUpUserResponseDto {
    String message;
    String status;
    List<Transaction> transactions;
}
