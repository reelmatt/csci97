package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * Interface for a Command.
 *
 * @author Matthew Thomas
 */
public interface Command {

    public void execute() throws StoreControllerServiceException;
}