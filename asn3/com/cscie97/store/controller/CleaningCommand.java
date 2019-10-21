package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * CleaningCommand.
 *
 * @author Matthew Thomas
 */
public class CleaningCommand implements Command {
    public Device source;

    public CleaningCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        return;
    };
}