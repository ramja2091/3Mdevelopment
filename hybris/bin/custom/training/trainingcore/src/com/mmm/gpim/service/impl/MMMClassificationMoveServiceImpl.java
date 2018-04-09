/**
 *
 */
package com.mmm.gpim.service.impl;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

import java.util.List;

import javax.annotation.Resource;

import com.mmm.gpim.daolayer.MMMClassificationMoveDao;
import com.mmm.gpim.service.MMMClassificationMoveService;


/**
 * @author rjo3959
 *
 */
public class MMMClassificationMoveServiceImpl implements MMMClassificationMoveService
{
	@Resource(name = "mmmClassificationMoveDao")
	private MMMClassificationMoveDao mmmClassificationMoveDao;

	@Override
	public ClassificationClassModel getClassifcationClassPk(final String classificationClass)
	{

		return mmmClassificationMoveDao.getClassifcationClassPk(classificationClass);
	}

	@Override
	public ClassificationAttributeModel getClassifcationAttributePk(final String attributeCode)
	{

		return mmmClassificationMoveDao.getClassifcationAttributePk(attributeCode);
	}

	@Override
	public List<ClassAttributeAssignmentModel> getClassAttributeAssignments(final String classificationClass,
			final String attribute)
	{

		return mmmClassificationMoveDao.getClassAttributeAssignments(classificationClass, attribute);
	}

	@Override
	public void addFeatureToNewCategory(final String attribute, final String targetClassficationClass)
	{
		mmmClassificationMoveDao.addFeatureToNewCategory(attribute, targetClassficationClass);
	}
}
