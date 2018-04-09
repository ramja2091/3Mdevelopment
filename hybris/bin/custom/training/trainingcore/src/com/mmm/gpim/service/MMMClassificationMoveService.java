/**
 *
 */
package com.mmm.gpim.service;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

import java.util.List;


/**
 * @author rjo3959
 *
 */
public interface MMMClassificationMoveService
{

	/**
	 * @param classificationClass
	 * @return
	 */
	ClassificationClassModel getClassifcationClassPk(String classificationClass);

	/**
	 * @param attributeCode
	 * @return
	 */
	ClassificationAttributeModel getClassifcationAttributePk(String attributeCode);

	/**
	 * @param classificationClassCode
	 * @param attributeCode
	 * @return
	 */
	List<ClassAttributeAssignmentModel> getClassAttributeAssignments(String classificationClassCode, String attributeCode);

	/**
	 * @param attribute
	 * @param targetClassficationClass
	 */
	void addFeatureToNewCategory(String attribute, String targetClassficationClass);


}
