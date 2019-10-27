package com.cscie97.store.model;

/**
 * Interface for a Subject.
 *
 * @author Matthew Thomas
 */
public interface Subject {
    /**
     *
     * @param observer
     */
    public void register(Observer observer);

    /**
     *
     * @param observer
     */
    public void deregister(Observer observer);

    /**
     *
     * @param device
     * @param event
     */
    public void notifyObservers(Device device, String event);
}