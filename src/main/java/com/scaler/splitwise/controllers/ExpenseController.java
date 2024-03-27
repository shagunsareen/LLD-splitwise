package com.scaler.splitwise.controllers;

import com.scaler.splitwise.dtos.SettleUpGroupRequestDto;
import com.scaler.splitwise.dtos.SettleUpGroupResponseDto;
import com.scaler.splitwise.dtos.SettleUpUserRequestDto;
import com.scaler.splitwise.dtos.SettleUpUserResponseDto;
import com.scaler.splitwise.services.ExpenseService;
import com.scaler.splitwise.strategies.settleupstrategy.Transaction;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ExpenseController {

    private ExpenseService expenseService;

    ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    public SettleUpUserResponseDto settleUpUser(SettleUpUserRequestDto requestDto) {
        List<Transaction> transactions = expenseService.settleUpUser(requestDto.getUserId());

        SettleUpUserResponseDto response = new SettleUpUserResponseDto();
        response.setStatus("SUCCESS");
        response.setTransactions(transactions);

        return response;
    }

    public SettleUpGroupResponseDto settleUpGroup(SettleUpGroupRequestDto request) {
        List<Transaction> transactions = expenseService.settleUpGroup(request.getGroupId());

        SettleUpGroupResponseDto response = new SettleUpGroupResponseDto();
        response.setStatus("SUCCESS");
        response.setTransactions(transactions);

        return response;
    }
}
