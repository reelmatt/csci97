package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * CheckAccountBalanceCommand.
 *
 * @author Matthew Thomas
 */
public class CheckAccountBalanceCommand implements Command {
    public Device source;

    public CheckAccountBalanceCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        return;
    };
}