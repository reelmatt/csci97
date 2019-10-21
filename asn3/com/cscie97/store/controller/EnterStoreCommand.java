package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * EnterStoreCommand.
 *
 * @author Matthew Thomas
 */
public class EnterStoreCommand implements Command {
    public Device source;

    public EnterStoreCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        return;
    };
}