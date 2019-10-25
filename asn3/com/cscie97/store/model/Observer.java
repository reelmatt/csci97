package com.cscie97.store.model;

/**
 * Interface for an Observer.
 *
 * @author Matthew Thomas
 */
public interface Observer {
    public void update(Device device, String event);
}