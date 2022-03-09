package org.openmrs;

import org.openmrs.util.RoleConstants;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RolePrivileges extends BaseChangeableOpenmrsMetadata {
	
	private String priviledgesID;

	private Set<Privilege> privileges;
	
	public RolePrivileges(){}

	public RolePrivileges(String id){
		this.priviledgesID = id;
	}

	public String getPriviledgesID() {
		return priviledgesID;
	}

	public void setPriviledgesID(String priviledgesID) {
		this.priviledgesID = priviledgesID;
	}

	public Set<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<Privilege> privileges) {
		this.privileges = privileges;
	}

	@Override
	public Integer getId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setId(Integer id) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds the given Privilege to the list of privileges
	 *
	 * @param privilege Privilege to add
	 */
	public void addPrivilege(Privilege privilege) {
		if (privileges == null) {
			privileges = new HashSet<>();
		}
		if (privilege != null && !containsPrivilege(privileges, privilege.getPrivilege())) {
			privileges.add(privilege);
		}
	}

	/**
	 * Removes the given Privilege from the list of privileges
	 *
	 * @param privilege Privilege to remove
	 */
	public void removePrivilege(Privilege privilege) {
		if (privileges != null) {
			privileges.remove(privilege);
		}
	}

	/**
	 * Looks for the given <code>privilegeName</code> privilege name in this roles privileges. This
	 * method does not recurse through the inherited roles
	 *
	 * @param privilegeName String name of a privilege
	 * @return true/false whether this role has the given privilege
	 * <strong>Should</strong> return false if not found
	 * <strong>Should</strong> return true if found
	 * <strong>Should</strong> not fail given null parameter
	 * <strong>Should</strong> return true for any privilegeName if super user
	 */
	public boolean hasPrivilege(String privilegeName, String role) {

		if (RoleConstants.SUPERUSER.equals(role)) {
			return true;
		}

		if (privileges != null) {
			for (Privilege p : privileges) {
				if (p.getPrivilege().equalsIgnoreCase(privilegeName)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean containsPrivilege(Collection<Privilege> privileges, String privilegeName) {
		for (Privilege privilege : privileges) {
			if (privilege.getPrivilege().equals(privilegeName)) {
				return true;
			}
		}
		return false;
	}
}
