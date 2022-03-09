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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.openmrs.util.RoleConstants;

/**
 * This class tests all methods that are not getter or setters in the Role java object TODO: finish
 * this test class for Role
 * 
 * @see Role
 */
public class RoleTest {
	
	/**
	 * Test the adding and removing of privileges to a role
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldAddRemovePrivilege() {
		Role role = new Role();
		
		// test the null parameter cases
		role.getRolePrivileges().addPrivilege(null);
		role.getRolePrivileges().removePrivilege(null);
		
		Privilege priv1 = new Privilege("priv1");
		role.getRolePrivileges().addPrivilege(priv1);
		assertEquals(role.getRolePrivileges().getPrivileges().size(), 1, "Incorrect number of privileges");
		
		// adding the same privilege should not be allowed
		role.getRolePrivileges().addPrivilege(priv1);
		assertEquals(role.getRolePrivileges().getPrivileges().size(), 1, "Incorrect number of privileges");
		
		// adding a different privilege with the same name should not be allowed
		Privilege priv2 = new Privilege(priv1.getPrivilege());
		role.getRolePrivileges().addPrivilege(priv2);
		assertEquals(role.getRolePrivileges().getPrivileges().size(), 1, "Incorrect number of privileges");
		
		Privilege priv3 = new Privilege("priv3");
		
		// removing a fake privilege shouldn't do anything
		role.getRolePrivileges().removePrivilege(priv3);
		assertEquals(role.getRolePrivileges().getPrivileges().size(), 1, "Incorrect number of privileges");
		
		// removing the first privilege
		role.getRolePrivileges().removePrivilege(priv1);
		assertEquals(role.getRolePrivileges().getPrivileges().size(), 0, "Incorrect number of privileges");
	}
	
	/**
	 * Simple test to check the hasPrivilege method
	 * 
	 * @see Role#hasPrivilege(String)
	 */
	@Test
	public void hasPrivilege_shouldNotFailGivenNullParameter() {
		Role role = new Role();
		
		// test the null case
		role.getRolePrivileges().hasPrivilege(null, role.getRole());
	}
	
	/**
	 * @see Role#hasPrivilege(String)
	 */
	@Test
	public void hasPrivilege_shouldReturnTrueIfFound() {
		Role role = new Role();
		
		// very basic privilege adding and checking
		Privilege p1 = new Privilege("priv1");
		role.getRolePrivileges().addPrivilege(p1);
		assertTrue(role.getRolePrivileges().hasPrivilege("priv1", role.getRole()), "This roles should have the privilege");
	}
	
	/**
	 * @see Role#hasPrivilege(String)
	 */
	@Test
	public void hasPrivilege_shouldReturnFalseIfNotFound() {
		Role role = new Role();
		assertFalse(role.getRolePrivileges().hasPrivilege("some other privilege name", role.getRole()), "This roles should not have the privilege");
	}

	@Test
	public void hasPrivilege_shouldBeCaseInsensitive() {
		Role role = new Role();

		// very basic privilege adding and checking
		Privilege p1 = new Privilege("PrIv1");
		role.getRolePrivileges().addPrivilege(p1);
		assertTrue(role.getRolePrivileges().hasPrivilege("priv1", role.getRole()), "This roles should have the privilege");
	}
	
	/**
	 * @see Role#hasPrivilege(String)
	 */
	@Test
	public void hasPrivilege_shouldReturnTrueForAnyPrivilegeNameIfSuperUser() {
		// check super user "super" status
		Role role = new Role(RoleConstants.SUPERUSER);
		
		assertTrue(role.getRolePrivileges().hasPrivilege("Some weird privilege name that shouldn't be there", role.getRole()), "Super users are super special and should have all privileges");
		assertNotNull(role.getName());
		assertEquals(role.getName(), RoleConstants.SUPERUSER);
	}
	
	/**
	 * @see Role#getAllParentRoles()
	 */
	@Test
	public void getAllParentRoles_shouldOnlyReturnParentRoles() {
		Role grandparent = new Role("Grandparent");
		Role aunt = new Role("Aunt");
		Role uncle = new Role("Uncle");
		Role parent = new Role("Parent");
		Role child1 = new Role("Child 1");
		Role child2 = new Role("Child 2");
		Role niece = new Role("Niece");
		
		Set<Role> inheritedRoles = new HashSet<>();
		
		// grandparent -> aunt, uncle, parent
		inheritedRoles.add(grandparent);
		parent.setInheritedRoles(new HashSet<>(inheritedRoles));
		aunt.setInheritedRoles(new HashSet<>(inheritedRoles));
		uncle.setInheritedRoles(new HashSet<>(inheritedRoles));
		
		// aunt, uncle -> niece
		inheritedRoles.clear();
		inheritedRoles.add(uncle);
		inheritedRoles.add(aunt);
		niece.setInheritedRoles(new HashSet<>(inheritedRoles));
		
		// parent -> child1, child2
		inheritedRoles.clear();
		inheritedRoles.add(parent);
		child1.setInheritedRoles(new HashSet<>(inheritedRoles));
		child2.setInheritedRoles(new HashSet<>(inheritedRoles));
		
		// ensure only inherited roles are found
		assertEquals(3, niece.getAllParentRoles().size());
		assertEquals(2, child1.getAllParentRoles().size());
		assertEquals(2, child2.getAllParentRoles().size());
		assertEquals(1, parent.getAllParentRoles().size());
		assertEquals(1, aunt.getAllParentRoles().size());
		assertEquals(1, uncle.getAllParentRoles().size());
		assertEquals(0, grandparent.getAllParentRoles().size());
	}
	
	/**
	 * @see Role#getAllChildRoles()
	 */
	@Test
	public void getAllChildRoles_shouldOnlyReturnChildRoles() {
		Role grandparent = new Role("Grandparent");
		Role aunt = new Role("Aunt");
		Role uncle = new Role("Uncle");
		Role parent = new Role("Parent");
		Role child1 = new Role("Child 1");
		Role child2 = new Role("Child 2");
		Role niece = new Role("Niece");
		
		Set<Role> childRoles = new HashSet<>();
		
		// grandparent -> aunt, uncle, parent
		childRoles.add(aunt);
		childRoles.add(uncle);
		childRoles.add(parent);
		grandparent.setChildRoles(new HashSet<>(childRoles));
		
		// aunt, uncle -> niece
		childRoles.clear();
		childRoles.add(niece);
		aunt.setChildRoles(new HashSet<>(childRoles));
		uncle.setChildRoles(new HashSet<>(childRoles));
		
		// parent -> child1, child2
		childRoles.clear();
		childRoles.add(child1);
		childRoles.add(child2);
		parent.setChildRoles(new HashSet<>(childRoles));
		
		// ensure only child roles are found
		assertEquals(0, niece.getAllChildRoles().size());
		assertEquals(0, child1.getAllChildRoles().size());
		assertEquals(0, child2.getAllChildRoles().size());
		assertEquals(2, parent.getAllChildRoles().size());
		assertEquals(1, aunt.getAllChildRoles().size());
		assertEquals(1, uncle.getAllChildRoles().size());
		assertEquals(6, grandparent.getAllChildRoles().size());
	}
	
}
