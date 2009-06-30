package gov.lanl.cnls.linkedprocess.os.errors;

/**
 * Author: josh
 * Date: Jun 30, 2009
 * Time: 3:44:43 PM
 */
public class VMAlreadyExistsException extends SchedulerException {
    public VMAlreadyExistsException(final String vmJID) {
        super("virtual machine with JID '" + vmJID + "' already exists");
    }
}