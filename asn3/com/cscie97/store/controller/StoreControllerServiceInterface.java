package com.cscie97.store.controller;

/**
 * Interface for a Store Controller.
 *
 * @author Matthew Thomas
 */
public interface StoreControllerServiceInterface {

    public Command createCommand(String event);

}