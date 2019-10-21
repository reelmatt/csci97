package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * EmergencyCommand.
 *
 * @author Matthew Thomas
 */
public class EmergencyCommand implements Command {
    public Device source;

    public EmergencyCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        return;
    };
}