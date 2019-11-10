package com.cscie97.store.model;

/**
 * Interface for an Observer.
 *
 * The interface is one part of the Observer pattern  (as described in Head
 * First Design, Chapter 2). The Observer contains a single method, update(),
 * which is called by the Subject whenever an event occurs. This then creates a
 * CommandInterface object to execute based on the event passed through.
 *
 * @author Matthew Thomas
 */
public interface Observer {

    /**
     * Create a Command object that performs a set of actions.
     *
     * Called by the Subject each time an event occurs. It passes the device and
     * event through to the Object. The event is parsed and used to create a
     * Command object.
     *
     * @param device    The Device which detected an event.
     * @param event     The event that was detected.
     */
    public void update(Device device, String event);
}