package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * BasketEventCommand.
 *
 * @author Matthew Thomas
 */
public class BasketEventCommand implements Command {
    public Device source;

    public BasketEventCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        return;
    };
}