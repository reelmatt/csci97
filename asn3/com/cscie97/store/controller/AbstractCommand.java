package com.cscie97.store.controller;

import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Aisle;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.StoreModelServiceException;
import java.util.List;
import java.util.ArrayList;

/**
 * AbstractCommand.
 *
 * An abstract command class that defines properties and methods for all
 * Command classes. Each command contains an authentication token, a Store
 * Model Service, and the source Device. Each command comes with methods to
 * retrieve Appliances and send command(s) to Appliance(s).
 *
 * @author Matthew Thomas
 */
public abstract class AbstractCommand implements Command {
    /** Token to authenticate with StoreModel API. */
    private String authToken;

    /** The Store Model Service to interact with. */
    private StoreModelServiceInterface storeModel;

    /** The Device which detected the event. */
    private Device source;

    /**
     * AbstractCommand Constructor.
     *
     * @param   authToken   Token to authenticate with StoreModel API
     * @param   storeModel  StoreModel to get/update state.
     * @param   source      The Device which detected the event.
     */
    public AbstractCommand(String authToken,
                           StoreModelServiceInterface storeModel,
                           Device source) {
        this.authToken = authToken;
        this.storeModel = storeModel;
        this.source = source;
    }

    /** {@inheritDoc} */
    public abstract void execute() throws StoreControllerServiceException;

    /**
     * Send a command to a list of Appliances.
     *
     * @param   appliances                  The list of appliances to command.
     * @param   commandMessage              The message to send.
     * @throws  StoreModelServiceException  The call to the StoreModel API fails.
     */
    public void sendCommands(List<Appliance> appliances, String commandMessage) throws StoreModelServiceException {
        for (Appliance appliance : appliances) {
            this.storeModel.receiveCommand(this.authToken, appliance.getId(), commandMessage);
        }
    }

    /**
     * Send a command to a single Appliance.
     *
     * @param   appliance                   The appliance to command.
     * @param   commandMessage              The message to send.
     * @throws  StoreModelServiceException  The call to the StoreModel API fails.
     */
    public void sendCommand(Appliance appliance, String commandMessage) throws StoreModelServiceException {
        this.storeModel.receiveCommand(this.authToken, appliance.getId(), commandMessage);
    }

    /**
     * Send a command to a single Appliance.
     *
     * @param   applianceId                 The ID of the appliance to command.
     * @param   commandMessage              The message to send.
     * @throws  StoreModelServiceException  The call to the StoreModel API fails.
     */
    public void sendCommand(String applianceId, String commandMessage) throws StoreModelServiceException {
        this.storeModel.receiveCommand(this.authToken, applianceId, commandMessage);
    }

    /**
     * Retrieve a single Appliance of a given type.
     *
     * @param   type                        The type of Appliance to retrieve.
     * @return                              An appliance matching the type.
     * @throws  StoreModelServiceException  The call to the StoreModel API fails.
     */
    public Appliance getOneAppliance(ApplianceType type) throws StoreModelServiceException {
        List<Appliance> appliances = getAppliances(type);

        return appliances.remove(0);
    }

    /**
     * Retrieve all Appliances of a given type.
     *
     * @param   type                        The type of Appliance to retrieve.
     * @return                              An list of Appliances matching the type.
     * @throws  StoreModelServiceException  The call to the StoreModel API fails.
     */
    public List<Appliance> getAppliances(ApplianceType type) throws StoreModelServiceException {
        return this.storeModel.getAppliances(this.authToken, type, this.source.getStore());
    }

    /** Returns the authentication token. */
    public String getAuthToken() {
        return this.authToken;
    }

    /** Returns the StoreModelService associated with the command. */
    public StoreModelServiceInterface getStoreModel() {
        return storeModel;
    }

    /** Returns the Device which emitted the event. */
    public Device getSource() {
        return source;
    }
}