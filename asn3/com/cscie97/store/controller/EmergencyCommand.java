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
public class EmergencyCommand extends AbstractCommand {
    /** Emergency types */
    private enum Emergency {FIRE, FLOOD, EARTHQUAKE, ARMED_INTRUDER};

    /** The type of emergency detected. */
    private Emergency emergency;

    /** The Aisle where the emergency was detected. */
    private Aisle aisle;

    /**
     *
     * @param authToken
     * @param storeModel
     * @param source
     * @param emergencyType
     * @param aisle
     * @throws StoreControllerServiceException
     */
    public EmergencyCommand(String authToken,
                            StoreModelServiceInterface storeModel,
                            Device source,
                            String emergencyType, Aisle aisle) throws StoreControllerServiceException {
        super(authToken, storeModel, source);

        this.emergency = getType(emergencyType);

        if (this.emergency == null) {
            throw new StoreControllerServiceException(
                "create emergency command",
                "Emergency of type " + emergencyType + " is not recognized.");
        }
        this.aisle = aisle;
    }

    /**
     * {@inheritDoc}
     */
    public void execute() {
        try {
            // Open turnstiles
            List<Appliance> turnstiles = super.getAppliances(ApplianceType.TURNSTILE);
            super.sendCommands(turnstiles, "open");

            // Announce emergency
            List<Appliance> speakers = super.getAppliances(ApplianceType.SPEAKER);
            String emergencyAnnouncement = String.format(
                "There is a %s in aisle %s, please leave %s immediately",
                this.emergency, super.getSource().getStore(), this.aisle.getId()
            );

            super.sendCommands(speakers, emergencyAnnouncement);

            // Send robot to address
            List<Appliance> robots = super.getAppliances(ApplianceType.ROBOT);
            Appliance helperRobot = robots.remove(0);
            super.sendCommand(helperRobot, "Address " + this.emergency + " in aisle " + this.aisle.getId());

            // Send remaining robots to assist customers
            super.sendCommands(robots, "Assist customers leaving the store");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };

    /**
     * Helper method to convert String into Appliance type.
     * @param   type    The ApplianceType to look for.
     * @return          If the type matches an ApplianceType option, return
     *                  that value. Otherwise, null.
     */
    public static Emergency getType(String type) {
        for (Emergency emergency : Emergency.values()) {
            if (type.equals(emergency.toString().toLowerCase())) {
                return emergency;
            }
        }

        return null;
    }
}