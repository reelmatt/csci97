package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * MissingPersonCommand.
 *
 * @author Matthew Thomas
 */
public class MissingPersonCommand implements Command {
    public Device source;

    public MissingPersonCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        return;
    };
}