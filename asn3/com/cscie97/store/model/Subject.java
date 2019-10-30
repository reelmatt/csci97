package com.cscie97.store.model;

/**
 * Interface for a Subject.
 *
 * @author Matthew Thomas
 */
public interface Subject {
    /**
     * Add Observer to list to be notified of events.
     * @param observer The Observer to be notified.
     */
    public void register(Observer observer);

    /**
     * Remove Observer from list to be notified of events.
     *
     * @param observer The Observer unsubscribing from notifications.
     */
    public void deregister(Observer observer);

    /**
     * Notify all registered Observers that an event occurred.
     *
     * @param device    The Device which detected the event.
     * @param event     The event the occured.
     */
    public void notifyObservers(Device device, String event);
}