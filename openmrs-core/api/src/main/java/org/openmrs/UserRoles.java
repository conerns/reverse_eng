package org.openmrs;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.util.RoleConstants;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserRoles implements java.io.Serializable {

	private Set<Role> roles;
	
	public UserRoles(){
		roles = new HashSet<>();
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public boolean hasRole(String r, boolean ignoreSuperUser) {
		if (!ignoreSuperUser && isSuperUser()) {
			return true;
		}

		if (roles == null) {
			return false;
		}

		Set<Role> tmproles = getAllRoles();

		//log.debug("User # {} has roles: {}", userId, tmproles);

		return containsRole(r);
	}

	/**
	 * Returns all roles attributed to this user by expanding the role list to include the parents of
	 * the assigned roles
	 *
	 * @return all roles (inherited from parents and given) for this user
	 */
	public Set<Role> getAllRoles() {
		// the user's immediate roles
		Set<Role> baseRoles = new HashSet<>();

		// the user's complete list of roles including
		// the parent roles of their immediate roles
		Set<Role> totalRoles = new HashSet<>();
		if (getRoles() != null) {
			baseRoles.addAll(getRoles());
			totalRoles.addAll(getRoles());
		}

		//log.debug("User's base roles: {}", baseRoles);

		try {
			for (Role r : baseRoles) {
				totalRoles.addAll(r.getAllParentRoles());
			}
		}
		catch (ClassCastException e) {
			//log.error("Error converting roles for user: " + this);
			//log.error("baseRoles.class: " + baseRoles.getClass().getName());
			//log.error("baseRoles: " + baseRoles.toString());
			//for (Role baseRole : baseRoles) {
			//	log.error("baseRole: '" + baseRole + "'");
			//}
		}
		return totalRoles;
	}
	
	public UserRoles addRole(Role role) {
		if (roles == null) {
			roles = new HashSet<>();
		}
		if (!roles.contains(role) && role != null) {
			roles.add(role);
		}

		return this;
	}

	public UserRoles removeRole(Role role) {
		if (roles != null) {
			roles.remove(role);
		}

		return this;
	}

	public boolean containsRole(String roleName) {
		for (Role role : getAllRoles()) {
			if (role.getRole().equalsIgnoreCase(roleName)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPrivilege(String privilege) {

		// All authenticated users have the "" (empty) privilege
		if (StringUtils.isEmpty(privilege)) {
			return true;
		}

		if (isSuperUser()) {
			return true;
		}

		Set<Role> tmproles = getAllRoles();

		// loop over the roles and check each for the privilege
		for (Role tmprole : tmproles) {
			if (tmprole.hasPrivilege(privilege)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get <i>all</i> privileges this user has. This delves into all of the roles that a person has,
	 * appending unique privileges
	 *
	 * @return Collection of complete Privileges this user has
	 */
	public Collection<Privilege> getPrivileges() {
		Set<Privilege> privileges = new HashSet<>();
		Set<Role> tmproles = getAllRoles();

		Role role;
		for (Role tmprole : tmproles) {
			role = tmprole;
			Collection<Privilege> privs = role.getPrivileges();
			if (privs != null) {
				privileges.addAll(privs);
			}
		}

		return privileges;
	}

	public boolean isSuperUser() {
		return containsRole(RoleConstants.SUPERUSER);
	}
}
