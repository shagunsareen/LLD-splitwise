package com.scaler.splitwise.strategies.settleupstrategy;

import com.scaler.splitwise.models.Expense;
import com.scaler.splitwise.models.User;
import com.scaler.splitwise.models.UserExpense;
import com.scaler.splitwise.models.UserExpenseType;
import com.scaler.splitwise.repositories.UserExpenseRepository;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("twoSetsSettleUpStrategy")
public class TwoSetsSettleUpStrategy implements SettleUpStrategy {

    private UserExpenseRepository userExpenseRepository;

    public TwoSetsSettleUpStrategy(UserExpenseRepository userExpenseRepository) {
        this.userExpenseRepository = userExpenseRepository;
    }

    @Override
    public List<Transaction> settle(List<Expense> expenseList) {

        //Get the userExpense from this expense list first
        List<UserExpense> userExpenses = userExpenseRepository.findAllByExpenseIn(expenseList);

        Map<User, Long> moneyPaidExtra = new HashMap<>();

        //Go through all of the expenses and find out who has paid how much extra or less
        for (UserExpense userExpense : userExpenses) {
            User user = userExpense.getUser();

            long currentPaidExtra = 0L;

            if (moneyPaidExtra.containsKey(user)) {
                currentPaidExtra = moneyPaidExtra.get(user);
            }

            //check whether userexpensetype is paid or had to pay
            if (userExpense.getUserExpenseType().equals(UserExpenseType.PAID)) {
                currentPaidExtra += userExpense.getExpense().getAmount();
            } else {
                currentPaidExtra -= userExpense.getExpense().getAmount();
            }

            moneyPaidExtra.put(user, currentPaidExtra);
        }


        //Now divide it into 2 segments who paidextra and who paid less
        TreeSet<Pair<User, Long>> extraPaid = new TreeSet<>();
        TreeSet<Pair<User, Long>> lessPaid = new TreeSet<>();

        for (Map.Entry<User, Long> userAmount : moneyPaidExtra.entrySet()) {
            if (userAmount.getValue() < 0) {
                lessPaid.add(new Pair<>(userAmount.getKey(), userAmount.getValue()));
            } else {
                extraPaid.add(new Pair<>(userAmount.getKey(), userAmount.getValue()));
            }
        }

        //now i have 2 separate sections, one with extra paid values and one with less paid values
        List<Transaction> transactions = new ArrayList<>();

        //until all the less payers are done paying we will continue to process transactions
        while (!lessPaid.isEmpty()) {
            Pair<User, Long> lessPaidPair = lessPaid.pollFirst();
            Pair<User, Long> extraPaidPair = extraPaid.pollFirst();

            Transaction t = new Transaction();
            t.setFrom(lessPaidPair.a);
            t.setTo(extraPaidPair.a);

            if (Math.abs(lessPaidPair.b) < extraPaidPair.b) { //settle the lesser value
                t.setAmount(Math.abs(lessPaidPair.b));

                long amountLeft = extraPaidPair.b - Math.abs(lessPaidPair.b);
                if (!(amountLeft == 0)) { // if even after lesser one paid the entire amount still diff is not 0 then more money has to be paid to extra paid
                    extraPaid.add(new Pair<>(extraPaidPair.a, amountLeft));
                }
            } else {
                t.setAmount(extraPaidPair.b);
                if (!(lessPaidPair.b + extraPaidPair.b == 0)) {
                    lessPaid.add(new Pair<>(lessPaidPair.a, lessPaidPair.b + extraPaidPair.b));
                }
            }
            transactions.add(t);
        }
        return transactions;
    }
}
