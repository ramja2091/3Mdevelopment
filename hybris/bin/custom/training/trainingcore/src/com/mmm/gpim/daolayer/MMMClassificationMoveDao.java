/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.mmm.gpim.daolayer;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;


/**
 * Service to read and update {@link ProductModel ProductModel}s.
 *
 * @spring.bean productService
 */
public interface MMMClassificationMoveDao
{
	public void addFeatureToNewCategory(String classAttrCode, String classCatCode);

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
	 * @param attributeCode
	 * @return
	 */
	List<ClassificationAttributeModel> getClassifcationAttributePkList(String attributeCode);

	/**
	 * @param classificationClassCode
	 * @param attributeCode
	 * @return
	 */
	List<ClassAttributeAssignmentModel> getClassAttributeAssignments(String classificationClass, String attribute);

	/**
	 * @param attribute
	 * @param sourceClass
	 * @param targetClass
	 * @param product
	 */
	void productMoveOnly(String attribute, String sourceClass, String targetClass, String product);


}
