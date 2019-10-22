package com.cscie97.store.model;

/**
 * Interface for a Subject.
 *
 * @author Matthew Thomas
 */
public interface Subject {

    public void register(Observer observer);
    public void deregister(Observer observer);
    public void notifyObservers(String event);
}