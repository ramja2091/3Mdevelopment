/**
 *
 */
package com.mmm.gpim.facades.classification.impl;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import javax.annotation.Resource;

import com.mmm.gpim.facades.classification.MMMClassificationMoveFacade;
import com.mmm.gpim.service.MMMClassificationMoveService;


/**
 * @author rjo3959
 *
 */


public class MMMClassificationMoveFacadeImpl implements MMMClassificationMoveFacade
{

	@Resource(name = "mmmClassificationMoveService")
	private MMMClassificationMoveService mmmClassificationMoveService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	public void createClassAttributeAssignment(final String targetClassficationClass, final String attribute,
			final String sourceclassificationClass)
	{
		final ClassificationClassModel sourcClassificationClass = mmmClassificationMoveService
				.getClassifcationClassPk(sourceclassificationClass);
		final ClassificationAttributeModel classificationAttribute = mmmClassificationMoveService
				.getClassifcationAttributePk(attribute);
		if (sourcClassificationClass != null && classificationAttribute != null)
		{

			final List<ClassAttributeAssignmentModel> sourceClassAttributeAssignmentList = mmmClassificationMoveService
					.getClassAttributeAssignments(sourcClassificationClass.getPk().toString(), classificationAttribute.getPk()
							.toString());
			if (sourceClassAttributeAssignmentList.size() > 0)
			{
				final ClassificationClassModel targetClassificationClass = mmmClassificationMoveService
						.getClassifcationClassPk(targetClassficationClass);
				final List<ClassAttributeAssignmentModel> targetClassAttributeAssignmentList = mmmClassificationMoveService
						.getClassAttributeAssignments(targetClassificationClass.getPk().toString(), classificationAttribute.getPk()
								.toString());
				if (targetClassAttributeAssignmentList.size() == 0)
				{
					final ClassAttributeAssignmentModel classAssignmentModel = modelService
							.create(ClassAttributeAssignmentModel.class);
					classAssignmentModel.setClassificationAttribute(classificationAttribute);
					classAssignmentModel.setClassificationClass(mmmClassificationMoveService
							.getClassifcationClassPk(targetClassficationClass));
					classAssignmentModel.setAttributeType(sourceClassAttributeAssignmentList.get(0).getAttributeType());
					modelService.save(classAssignmentModel);
				}
				mmmClassificationMoveService.addFeatureToNewCategory(attribute, targetClassficationClass);
			}

		}

	}
}
