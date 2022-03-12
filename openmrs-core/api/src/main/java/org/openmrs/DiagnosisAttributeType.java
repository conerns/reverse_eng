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

import org.openmrs.attribute.Attribute;
import org.openmrs.attribute.AttributeType;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.attribute.BaseAttributeType;

/**
 * The DiagnosisAttributeType extension to the {@link Diagnosis} class.
 * @see AttributeType
 * @since 2.5.0
 */
public class DiagnosisAttributeType extends BaseAttributeType<Diagnosis> implements AttributeType<Diagnosis> {
	
	public class DiagnosisAttribute extends BaseAttribute<DiagnosisAttributeType, Diagnosis> implements Attribute<DiagnosisAttributeType, Diagnosis> {
		
		private Integer diagnosisAttributeId;

		/**
		 * @return the diagnosisAttributeId
		 */
		public Integer getDiagnosisAttributeId() {
			return diagnosisAttributeId;
		}
		
		/**
		 * @param diagnosisAttributeId the DiagnosisAttributeId to set
		 */
		public void setDiagnosisAttributeId(Integer diagnosisAttributeId) {
			this.diagnosisAttributeId = diagnosisAttributeId;
		}
		
		/**
		 * @return the diagnosis
		 */
		public Diagnosis getDiagnosis() {
			return getOwner();
		}
		
		/**
		 * @param diagnosis the diagnosis to set
		 */
		public void setDiagnosis(Diagnosis diagnosis) {
			setOwner(diagnosis);
		}
		
		/**
		 * @see org.openmrs.OpenmrsObject#getId()
		 */
		@Override
		public Integer getId() {
			return getDiagnosisAttributeId();
		}
		
		/**
		 * @see org.openmrs.OpenmrsObject#setId(Integer)
		 */
		@Override
		public void setId(Integer id) {
			setDiagnosisAttributeId(id);
		}
	}

	private Integer diagnosisAttributeTypeId;

	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getDiagnosisAttributeTypeId();
	}

	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		setDiagnosisAttributeTypeId(id);
	}

	/**
	 * @return the diagnosisAttributeTypeId
	 */
	public Integer getDiagnosisAttributeTypeId() {
		return diagnosisAttributeTypeId;
	}

	/**
	 * @param diagnosisAttributeTypeId the diagnosisAttributeTypeId to set
	 */
	public void setDiagnosisAttributeTypeId(Integer diagnosisAttributeTypeId) {
		this.diagnosisAttributeTypeId = diagnosisAttributeTypeId;
	}
}

