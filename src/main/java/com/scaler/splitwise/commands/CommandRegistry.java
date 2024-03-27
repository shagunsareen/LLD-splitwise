package com.scaler.splitwise.commands;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommandRegistry {

    private List<Command> commands;

    public CommandRegistry(RegisterUserCommand registerUserCommand) {
        commands = new ArrayList<>();
        commands.add(registerUserCommand);
    }

    public void execute(String input) {
        for (Command command : commands) {
            if (command.matches(input)) {
                command.execute(input);
            }
        }
    }
}
