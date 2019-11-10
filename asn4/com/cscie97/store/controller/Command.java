package com.cscie97.store.controller;

/**
 * Interface for a Command.
 *
 * The interface follows the Command pattern as described in Head First Design,
 * chapter 6. A Command is created by the Store Controller Service (through a
 * CommandFactory) in response to being notified of a Store event. Once the
 * Command is created, its execute() method is called to run the set of actions
 * defined within.
 *
 * @author Matthew Thomas
 */
public interface Command {
    /**
     * Runs the set of actions that are defined within the Command class.
     * The actions vary depending on the particular subclass of Command that
     * is instantiated.
     *
     * @throws StoreControllerServiceException  If Store Model objects are missing
     *                                          or throw Exceptions, such that the
     *                                          command cannot fully execute, a
     *                                          Controller exception is thrown.
     */
    public void execute() throws StoreControllerServiceException;
}