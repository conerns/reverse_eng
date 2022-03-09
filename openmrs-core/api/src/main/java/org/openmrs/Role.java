/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.util.RoleConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Role is just an aggregater of {@link Privilege}s. {@link User}s contain a number of roles
 * (Users DO NOT contain any privileges directly) Roles can be grouped by inheriting other roles. If
 * a user is given Role A that inherits from Role B, the user has all rights/abilities for both Role
 * A's privileges and for Role B's privileges.
 *
 * @see Privilege
 */
public class Role extends BaseChangeableOpenmrsMetadata {
	
	public static final long serialVersionUID = 1234233L;
	
	private static final Logger log = LoggerFactory.getLogger(Role.class);
	
	// Fields
	
	private String role;
	
	private RolePrivileges rolePrivileges;
	
	private Set<Role> inheritedRoles;
	
	private Set<Role> childRoles;
	
	// Constructors
	
	/** default constructor */
	public Role() {
		rolePrivileges = new RolePrivileges(role);
	}
	
	/** constructor with id */
	public Role(String role) {
		this.role = role;
		rolePrivileges = new RolePrivileges(role);
	}
	
	/** constructor with all database required properties */
	public Role(String role, String description) {
		this.role = role;
		setDescription(description);
		rolePrivileges = new RolePrivileges(role);
	}

	public RolePrivileges getRolePrivileges() {
		return rolePrivileges;
	}

	public void setRolePrivileges(RolePrivileges rolePrivileges) {
		this.rolePrivileges = rolePrivileges;
	}

	@Override
	public String getName() {
		return this.getRole();
	}
	
	
	/**
	 * @return Returns the role.
	 */
	public String getRole() {
		return role;
	}
	
	/**
	 * @param role The role to set.
	 */
	public void setRole(String role) {
		this.role = role;
		if(rolePrivileges == null)
			rolePrivileges = new RolePrivileges();
		rolePrivileges.setPriviledgesID(role);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.role;
	}
	
	
	/**
	 * @return Returns the inheritedRoles.
	 */
	public Set<Role> getInheritedRoles() {
		if (inheritedRoles == null) {
			inheritedRoles = new HashSet<>();
		}
		return inheritedRoles;
	}
	
	/**
	 * @param inheritedRoles The inheritedRoles to set.
	 */
	public void setInheritedRoles(Set<Role> inheritedRoles) {
		this.inheritedRoles = inheritedRoles;
	}
	
	/**
	 * Convenience method to test whether or not this role extends/ inherits from any other roles
	 *
	 * @return true/false whether this role inherits from other roles
	 */
	public boolean inheritsRoles() {
		return getInheritedRoles() != null && !getInheritedRoles().isEmpty();
	}
	
	/**
	 * Recursive (if need be) method to return all parent roles of this role
	 *
	 * <strong>Should</strong> only return parent roles
	 * @return Return this role's parents
	 */
	public Set<Role> getAllParentRoles() {
		Set<Role> parents = new HashSet<>();
		if (inheritsRoles()) {
			parents.addAll(this.recurseOverParents(parents));
		}
		return parents;
	}
	
	/**
	 * Returns the full set of roles be looping over inherited roles. Duplicate roles are dropped.
	 *
	 * @param total Roles already looped over
	 * @return Set&lt;Role&gt; Current and inherited roles
	 */
	public Set<Role> recurseOverParents(final Set<Role> total) {
		if (!this.inheritsRoles()) {
			return total;
		}

		Set<Role> allRoles = new HashSet<>(total);
		Set<Role> myRoles = new HashSet<>(this.getInheritedRoles());
		myRoles.removeAll(total);
		// prevent an obvious looping problem
		myRoles.remove(this); 
		allRoles.addAll(myRoles);
		
		for (Role r : myRoles) {
			if (r.inheritsRoles()) {
				allRoles.addAll(r.recurseOverParents(allRoles));
			}
		}
		
		log.debug("Total roles: {}", allRoles);
		
		return allRoles;
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @since 1.9
	 * @return immediate children
	 */
	public Set<Role> getChildRoles() {
		if (childRoles == null) {
			childRoles = new HashSet<>();
		}
		return childRoles;
	}
	
	/**
	 * @since 1.9
	 * @param childRoles the immediate children to set
	 */
	public void setChildRoles(Set<Role> childRoles) {
		this.childRoles = childRoles;
	}
	
	/**
	 * Convenience method to test whether or not this role is a parent of another role
	 *
	 * @return true/false whether this role is a parent of another role
	 * @since 1.9
	 */
	public boolean hasChildRoles() {
		return getChildRoles() != null && !getChildRoles().isEmpty();
	}
	
	/**
	 * Recursive (if need be) method to return all child roles of this role
	 *
	 * <strong>Should</strong> only return child roles
	 * @return this role's children
	 * @since 1.9
	 */
	public Set<Role> getAllChildRoles() {
		Set<Role> children = new HashSet<>();
		if (hasChildRoles()) {
			children.addAll(this.recurseOverChildren(children));
		}
		return children;
	}
	
	/**
	 * Returns the full set of child roles be looping over children. Duplicate roles are dropped.
	 *
	 * @param total Roles already looped over
	 * @return Set&lt;Role&gt; Current and child roles
	 * @since 1.9
	 */
	public Set<Role> recurseOverChildren(final Set<Role> total) {
		if (!this.hasChildRoles()) {
			return total;
		}

		Set<Role> allRoles = new HashSet<>(total);

		Set<Role> myRoles = new HashSet<>(this.getChildRoles());
		myRoles.removeAll(total);
		// prevent an obvious looping problem
		myRoles.remove(this); 
		allRoles.addAll(myRoles);
		
		for (Role r : myRoles) {
			if (r.hasChildRoles()) {
				allRoles.addAll(r.recurseOverChildren(allRoles));
			}
		}
		
		log.debug("Total roles: {}", allRoles);
		
		return allRoles;
	}
}
