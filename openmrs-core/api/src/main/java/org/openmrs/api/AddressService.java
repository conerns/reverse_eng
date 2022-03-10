package org.openmrs.api;

import org.openmrs.Address;
import org.openmrs.annotation.Authorized;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface AddressService extends OpenmrsService {

	/**
	 * Given an Address object, returns all the possible values for the specified AddressField. This
	 * method is not implemented in core, but is meant to overridden by implementing modules such as
	 * the Address Hierarchy module.
	 *
	 * @param incomplete the incomplete address
	 * @param fieldName the address field we are looking for possible values for
	 * @return a list of possible address values for the specified field
	 * <strong>Should</strong> return empty list if no possible address matches
	 * <strong>Should</strong> return null if method not implemented
	 * <strong>Should</strong> return null by default
	 * @since 1.7.2
	 */
	public List<String> getPossibleAddressValues(Address incomplete, String fieldName) throws APIException;

	/**
	 * Returns the xml of default address template.
	 *
	 * @return a string value of the default address template. If the GP is empty, the default
	 *         template is returned
	 * @see OpenmrsConstants#GLOBAL_PROPERTY_ADDRESS_TEMPLATE
	 * @see OpenmrsConstants#DEFAULT_ADDRESS_TEMPLATE
	 * @since 1.9
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public String getAddressTemplate() throws APIException;

	/**
	 * Saved default address template to global properties
	 *
	 * @param xml is a string to be saved as address template
	 * <strong>Should</strong> throw APIException if the string is empty
	 * <strong>Should</strong> update default address template successfully
	 * <strong>Should</strong> create default address template successfully
	 * @since 1.9
	 */
	@Authorized( { PrivilegeConstants.MANAGE_ADDRESS_TEMPLATES })
	public void saveAddressTemplate(String xml) throws APIException;
	
}
