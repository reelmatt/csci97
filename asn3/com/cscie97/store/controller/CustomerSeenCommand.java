package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * CustomerSeenCommand.
 *
 * @author Matthew Thomas
 */
public class CustomerSeenCommand implements Command {
    public Device source;

    public CustomerSeenCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        return;
    };
}