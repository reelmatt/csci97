package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * FetchProductCommand.
 *
 * @author Matthew Thomas
 */
public class FetchProductCommand implements Command {
    public Device source;

    public FetchProductCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        return;
    };
}