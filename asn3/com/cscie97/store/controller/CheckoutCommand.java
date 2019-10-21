package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * CheckoutCommand.
 *
 * @author Matthew Thomas
 */
public class CheckoutCommand implements Command {
    public Device source;

    public CheckoutCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        return;
    };
}