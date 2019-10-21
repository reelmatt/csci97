package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * AssistCustomerCommand.
 *
 * @author Matthew Thomas
 */
public class AssistCustomerCommand implements Command {
    public Device source;

    public AssistCustomerCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        return;
    };
}