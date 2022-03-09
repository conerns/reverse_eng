package org.openmrs.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.CannotDeleteRoleWithChildrenException;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.UserRolesDAO;
import org.openmrs.api.UserRolesService;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.util.RoleConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserRolesServiceImpl implements UserRolesService {
	
	protected UserRolesDAO dao;


	/**
	 * Convenience method to check if the authenticated user has all privileges they are giving out
	 * to the new role
	 *
	 * @param role
	 */
	private void checkPrivileges(Role role) {
		Optional.ofNullable(role.getRolePrivileges().getPrivileges())
			.map(p -> p.stream().filter(pr -> !Context.hasPrivilege(pr.getPrivilege())).map(Privilege::getPrivilege)
				.distinct().collect(Collectors.joining(", ")))
			.ifPresent(missing -> {
				if (StringUtils.isNotBlank(missing)) {
					throw new APIException("Role.you.must.have.privileges", new Object[] { missing });
				}
			});
	}

	/**
	 * @see org.openmrs.api.UserService#getPrivilegeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Privilege getPrivilegeByUuid(String uuid) throws APIException {
		return dao.getPrivilegeByUuid(uuid);
	}
	
	public void setUserRolesDAO(UserRolesDAO dao){
		this.dao = dao;		
	}

	/**
	 * @see org.openmrs.api.UserService#getRoleByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Role getRoleByUuid(String uuid) throws APIException {
		return dao.getRoleByUuid(uuid);
	}


	/**
	 * @see org.openmrs.api.UserService#getAllPrivileges()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Privilege> getAllPrivileges() throws APIException {
		return dao.getAllPrivileges();
	}

	/**
	 * @see org.openmrs.api.UserService#getPrivilege(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Privilege getPrivilege(String p) throws APIException {
		return dao.getPrivilege(p);
	}

	/**
	 * @see org.openmrs.api.UserService#purgePrivilege(org.openmrs.Privilege)
	 */
	@Override
	public void purgePrivilege(Privilege privilege) throws APIException {
		if (OpenmrsUtil.getCorePrivileges().keySet().contains(privilege.getPrivilege())) {
			throw new APIException("Privilege.cannot.delete.core", (Object[]) null);
		}

		dao.deletePrivilege(privilege);
	}

	/**
	 * @see org.openmrs.api.UserService#savePrivilege(org.openmrs.Privilege)
	 */
	@Override
	public Privilege savePrivilege(Privilege privilege) throws APIException {
		return dao.savePrivilege(privilege);
	}

	/**
	 * @see org.openmrs.api.UserService#getAllRoles()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Role> getAllRoles() throws APIException {
		return dao.getAllRoles();
	}

	/**
	 * @see org.openmrs.api.UserService#getRole(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Role getRole(String r) throws APIException {
		return dao.getRole(r);
	}

	/**
	 * @see org.openmrs.api.UserService#purgeRole(org.openmrs.Role)
	 */
	@Override
	public void purgeRole(Role role) throws APIException {
		if (role == null || role.getRole() == null) {
			return;
		}

		if (OpenmrsUtil.getCoreRoles().keySet().contains(role.getRole())) {
			throw new APIException("Role.cannot.delete.core", (Object[]) null);
		}

		if (role.hasChildRoles()) {
			throw new CannotDeleteRoleWithChildrenException();
		}

		dao.deleteRole(role);
	}

	/**
	 * @see org.openmrs.api.UserService#saveRole(org.openmrs.Role)
	 */
	@Override
	public Role saveRole(Role role) throws APIException {
		// make sure one of the parents of this role isn't itself...this would
		// cause an infinite loop
		if (role.getAllParentRoles().contains(role)) {
			throw new APIException("Role.cannot.inherit.descendant", (Object[]) null);
		}

		checkPrivileges(role);

		return dao.saveRole(role);
	}

	/**
	 * Convenience method to check if the authenticated user has all privileges they are giving out
	 *
	 * @param user user that has privileges
	 */
	public void checkPrivileges(User user) {
		List<String> requiredPrivs = user.getAllRoles().stream().peek(this::checkSuperUserPrivilege)
			.map(Role::getRolePrivileges).map(RolePrivileges::getPrivileges).filter(Objects::nonNull).flatMap(Collection::stream)
			.map(Privilege::getPrivilege).filter(p -> !Context.hasPrivilege(p)).sorted().collect(Collectors.toList());
		if (requiredPrivs.size() == 1) {
			throw new APIException("User.you.must.have.privilege", new Object[] { requiredPrivs.get(0) });
		} else if (requiredPrivs.size() > 1) {
			throw new APIException("User.you.must.have.privileges", new Object[] { String.join(", ", requiredPrivs) });
		}
	}

	private void checkSuperUserPrivilege(Role r) {
		if (r.getRole().equals(RoleConstants.SUPERUSER)
			&& !Context.hasPrivilege(PrivilegeConstants.ASSIGN_SYSTEM_DEVELOPER_ROLE)) {
			throw new APIException("User.you.must.have.role", new Object[] { RoleConstants.SUPERUSER });
		}
	}

	@Override
	public void onStartup() {
		
	}

	@Override
	public void onShutdown() {

	}
}
