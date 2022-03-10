package org.openmrs.api.impl;

import org.openmrs.Address;
import org.openmrs.api.APIException;
import org.openmrs.api.AddressService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

public class AddressServiceImpl implements AddressService {
	/**
	 * @see org.openmrs.api.LocationService#getPossibleAddressValues(Address, String)
	 */
	@Override
	public List<String> getPossibleAddressValues(Address incomplete, String fieldName) throws APIException {
		// not implemented by default
		return null;
	}


	/**
	 * @see org.openmrs.api.LocationService#getAddressTemplate()
	 */
	@Override
	@Transactional(readOnly = true)
	public String getAddressTemplate() throws APIException {
		String addressTemplate = Context.getAdministrationService().getGlobalProperty(
			OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE);
		if (!StringUtils.hasLength(addressTemplate)) {
			addressTemplate = OpenmrsConstants.DEFAULT_ADDRESS_TEMPLATE;
		}

		return addressTemplate;
	}

	/**
	 * @see org.openmrs.api.LocationService#saveAddressTemplate(String)
	 */
	@Override
	public void saveAddressTemplate(String xml) throws APIException {
		Context.getAdministrationService().setGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE, xml);

	}

	@Override
	public void onStartup() {
		
	}

	@Override
	public void onShutdown() {

	}
}
