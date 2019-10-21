package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * BrokenGlassCommand.
 *
 * @author Matthew Thomas
 */
public class BrokenGlassCommand implements Command {
    public Device source;

    public BrokenGlassCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        return;
    };
}