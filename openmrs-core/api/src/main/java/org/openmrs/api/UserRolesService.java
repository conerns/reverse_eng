package org.openmrs.api;

import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface UserRolesService extends OpenmrsService {

	/**
	 * Returns all privileges currently possible for any User
	 *
	 * @return Global list of privileges
	 * @throws APIException
	 * <strong>Should</strong> return all privileges in the system
	 */
	public List<Privilege> getAllPrivileges() throws APIException;

	/**
	 * Returns all roles currently possible for any User
	 *
	 * @return Global list of roles
	 * @throws APIException
	 * <strong>Should</strong> return all roles in the system
	 */
	public List<Role> getAllRoles() throws APIException;

	/**
	 * Save the given role in the database
	 *
	 * @param role Role to update
	 * @return the saved role
	 * @throws APIException
	 * <strong>Should</strong> throw error if role inherits from itself
	 * <strong>Should</strong> save given role to the database
	 */
	@Authorized( { PrivilegeConstants.MANAGE_ROLES })
	public Role saveRole(Role role) throws APIException;

	/**
	 * Complete remove a role from the database
	 *
	 * @param role Role to delete from the database
	 * @throws APIException
	 * <strong>Should</strong> throw error when role is a core role
	 * <strong>Should</strong> return if role is null
	 * <strong>Should</strong> delete given role from database
	 */
	@Authorized( { PrivilegeConstants.PURGE_ROLES })
	public void purgeRole(Role role) throws APIException;

	/**
	 * Save the given privilege in the database
	 *
	 * @param privilege Privilege to update
	 * @return the saved privilege
	 * @throws APIException
	 * <strong>Should</strong> save given privilege to the database
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PRIVILEGES })
	public Privilege savePrivilege(Privilege privilege) throws APIException;

	/**
	 * Completely remove a privilege from the database
	 *
	 * @param privilege Privilege to delete
	 * @throws APIException
	 * <strong>Should</strong> delete given privilege from the database
	 * <strong>Should</strong> throw error when privilege is core privilege
	 */
	@Authorized( { PrivilegeConstants.PURGE_PRIVILEGES })
	public void purgePrivilege(Privilege privilege) throws APIException;

	/**
	 * Returns role object with given string role
	 *
	 * @return Role object for specified string
	 * @throws APIException
	 * <strong>Should</strong> fetch role for given role name
	 */
	public Role getRole(String r) throws APIException;

	/**
	 * Get Role by its UUID
	 *
	 * @param uuid
	 * @return role or null
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	public Role getRoleByUuid(String uuid) throws APIException;

	/**
	 * Returns Privilege in the system with given String privilege
	 *
	 * @return Privilege
	 * @throws APIException
	 * <strong>Should</strong> fetch privilege for given name
	 */
	public Privilege getPrivilege(String p) throws APIException;

	/**
	 * Get Privilege by its UUID
	 *
	 * @param uuid
	 * @return privilege or null
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 * <strong>Should</strong> fetch privilege for given uuid
	 */
	public Privilege getPrivilegeByUuid(String uuid) throws APIException;
	
	public void checkPrivileges(User user);
}
