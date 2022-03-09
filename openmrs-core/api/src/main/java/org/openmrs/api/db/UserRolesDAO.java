package org.openmrs.api.db;

import org.openmrs.Privilege;
import org.openmrs.Role;

import java.util.List;

public interface UserRolesDAO {
	
	public Role saveRole(Role role) throws DAOException;

	public void deleteRole(Role role) throws DAOException;

	public Role getRole(String r) throws DAOException;

	public List<Role> getAllRoles() throws DAOException;

	// Privilege stuff

	public Privilege savePrivilege(Privilege privilege) throws DAOException;

	public Privilege getPrivilege(String p) throws DAOException;

	public List<Privilege> getAllPrivileges() throws DAOException;

	public void deletePrivilege(Privilege privilege) throws DAOException;

	public Privilege getPrivilegeByUuid(String uuid);

	/**
	 * @param uuid
	 * @return role or null
	 */
	public Role getRoleByUuid(String uuid);
}
