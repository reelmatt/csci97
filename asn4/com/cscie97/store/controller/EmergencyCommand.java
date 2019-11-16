package com.cscie97.store.controller;

import java.util.List;
import com.cscie97.store.model.Aisle;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.authentication.AuthToken;
import com.cscie97.store.authentication.AuthenticationException;
import com.cscie97.store.authentication.AccessDeniedException;
import com.cscie97.store.authentication.InvalidAuthTokenException;

/**
 * EmergencyCommand.
 *
 * An EmergencyCommand is created when a Camera detects an emergency in a store
 * Aisle. When execute() is called, the Command opens all turnstiles, announces
 * the emergency through all all speakers in the Store, selects the first
 * unoccupied Robot to send to the Aisle the emergency was detected, and
 * commands all other Robots to assist Customers.
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
     * EmergencyCommand Constructor.
     *
     * @param authToken
     * @param storeModel
     * @param source
     * @param emergencyType
     * @param aisle
     * @throws StoreControllerServiceException
     */
    public EmergencyCommand(AuthToken authToken,
                            StoreModelServiceInterface storeModel,
                            Device source,
                            String emergencyType,
                            Aisle aisle) throws StoreControllerServiceException {
        super(authToken, storeModel, source);

        this.aisle = aisle;
        this.emergency = getType(emergencyType);

        // Check the emergency is recognized by the Controller
        if (this.emergency == null) {
            throw new StoreControllerServiceException(
                "create emergency command",
                "Emergency of type " + emergencyType + " is not recognized.");
        }
    }

    /**
     * {@inheritDoc}
     *
     * Open all turnstiles and announce the emergency over all speakers. Task
     * one Robot to handle the emergency and task all others to assist Customers
     * out of the Store.
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
                this.emergency, this.aisle.getId(), super.getSource().getStore()
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