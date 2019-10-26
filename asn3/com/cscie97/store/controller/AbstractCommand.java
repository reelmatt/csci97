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
 * EmergencyCommand.
 *
 * @author Matthew Thomas
 */
public abstract class AbstractCommand implements Command {

    private String authToken;

    private StoreModelServiceInterface storeModel;

    private Device source;


    public AbstractCommand(String authToken,
                           StoreModelServiceInterface storeModel,
                           Device source) {
        this.authToken = authToken;
        this.storeModel = storeModel;
        this.source = source;
    }

    public abstract void execute() throws StoreControllerServiceException;

    public void sendCommands(List<Appliance> appliances, String commandMessage) throws StoreModelServiceException {
        for (Appliance appliance : appliances) {
            this.storeModel.receiveCommand(this.authToken, appliance.getId(), commandMessage);
        }
    }

    public void sendCommand(Appliance appliance, String commandMessage) throws StoreModelServiceException {
        this.storeModel.receiveCommand(this.authToken, appliance.getId(), commandMessage);
    }

    public Appliance getOneAppliance(ApplianceType type) throws StoreModelServiceException {
        List<Appliance> appliances = getAppliances(type);

        return appliances.remove(0);
    }

    public List<Appliance> getAppliances(ApplianceType type) throws StoreModelServiceException {
        return this.storeModel.getAppliances(this.authToken, type, this.source.getStore());
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public StoreModelServiceInterface getStoreModel() {
        return storeModel;
    }

    public Device getSource() {
        return source;
    }
}