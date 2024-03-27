package com.scaler.splitwise.services;

import com.scaler.splitwise.models.Expense;
import com.scaler.splitwise.models.Group;
import com.scaler.splitwise.models.User;
import com.scaler.splitwise.models.UserExpense;
import com.scaler.splitwise.repositories.ExpenseRepository;
import com.scaler.splitwise.repositories.GroupRepository;
import com.scaler.splitwise.repositories.UserExpenseRepository;
import com.scaler.splitwise.repositories.UserRepository;
import com.scaler.splitwise.strategies.settleupstrategy.SettleUpStrategy;
import com.scaler.splitwise.strategies.settleupstrategy.Transaction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private UserRepository userRepository;
    private UserExpenseRepository userExpenseRepository;

    private SettleUpStrategy settleUpStrategy;

    private GroupRepository groupRepository;

    private ExpenseRepository expenseRepository;

    public ExpenseService(UserRepository userRepository,
                          UserExpenseRepository userExpenseRepository,
                          @Qualifier("twoSetsSettleUpStrategy")
                          SettleUpStrategy settleUpStrategy,
                          GroupRepository groupRepository,
                          ExpenseRepository expenseRepository) {
        this.userRepository = userRepository;
        this.userExpenseRepository = userExpenseRepository;
        this.settleUpStrategy = settleUpStrategy;
        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
    }

    public List<Transaction> settleUpUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            //throw exception
            return null;
        }

        //get the user expenses for the user
        List<UserExpense> expenses = userExpenseRepository.findAllByUser(user.get());

        //filter the expenses in a list
        List<Expense> expensesInvolvingUser = new ArrayList<>();

        for (UserExpense userExpense : expenses) {
            expensesInvolvingUser.add(userExpense.getExpense());
        }

        //now that we have all expenses of a user id let's settle & get the transactions
        List<Transaction> transactions = settleUpStrategy.settle(expensesInvolvingUser);

        List<Transaction> filteredTransactions = new ArrayList<>();

        //filter out the transactions of the user
        for (Transaction transaction : transactions) {
            if (transaction.getFrom().equals(user.get()) || transaction.getTo().equals(user.get())) {
                filteredTransactions.add(transaction);
            }
        }

        return filteredTransactions;
    }

    public List<Transaction> settleUpGroup(Long groupId) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isEmpty()) {
            //throw exception
            return null;
        }

        //get the expenses from group id
        List<Expense> expenses = expenseRepository.findAllByGroup(groupOptional.get());

        List<Transaction> transactions = settleUpStrategy.settle(expenses);
        return transactions;
    }
}
