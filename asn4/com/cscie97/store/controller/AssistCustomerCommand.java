package com.cscie97.store.controller;

import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.authentication.AuthToken;
import com.cscie97.store.authentication.AuthenticationException;
import com.cscie97.store.authentication.AccessDeniedException;
import com.cscie97.store.authentication.InvalidAuthTokenException;

/**
 * AssistCustomerCommand.
 *
 * An AssistCustomerCommand is created by a Turnstile during the CheckoutCommand
 * process if the weight of Products in the basket exceeds a certain limit, in
 * this case, 10 lbs. When triggered, the first available Robot is commanded to
 * assist the Customer bring the items to their car.
 *
 * @author Matthew Thomas
 */
public class AssistCustomerCommand extends AbstractCommand {
    /** The Customer that needs assistance. */
    private Customer customer;

    /** Formatted command string to send to Appliance. */
    private static final String FORMATTED_COMMAND = "Basket weights %f lbs. Assist customer %s to car";

    /**
     * AssistCustomerCommand Constructor.
     *
     * @param authToken     Token to authenticate with StoreModel API.
     * @param storeModel    Store Model Service to get/update state.
     * @param source        The Device which detected the event.
     * @param customer      The Customer that needs assistance.
     */
    public AssistCustomerCommand(AuthToken authToken,
                                 StoreModelServiceInterface storeModel,
                                 Device source,
                                 Customer customer) {
        super(authToken, storeModel, source);
        this.customer = customer;
    }

    /**
     * {@inheritDoc}
     *
     * Obtain the current basket weight and get an available robot to command.
     * Format the command message with basket weight and customer name and send
     * to robot.
     */
    public void execute() {
        try {
            Double basketWeight = this.customer.calculateBasketWeight();

            Appliance robot = super.getOneAppliance(ApplianceType.ROBOT);
            String message = String.format(FORMATTED_COMMAND, basketWeight, this.customer.customerName());
            super.sendCommand(robot, message);
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        } catch (AccessDeniedException e) {
            System.err.println(e);
        } catch (AuthenticationException e) {
            System.err.println(e);
        } catch (InvalidAuthTokenException e) {
            System.err.println(e);
        }

        super.logoutToken();
        return;
    };
}