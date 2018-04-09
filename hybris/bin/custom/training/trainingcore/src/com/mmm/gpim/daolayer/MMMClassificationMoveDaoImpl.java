package com.mmm.gpim.daolayer;

import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.classification.daos.ProductFeaturesDao;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.classification.features.UnlocalizedFeature;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mmm.gpim.pojolayer.FeaturePojo;



public class MMMClassificationMoveDaoImpl implements MMMClassificationMoveDao
{


	@Resource
	private ModelService modelService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	ClassificationSystemService classificationSystemService;
	@Resource
	private ClassificationService classificationService;
	@Resource
	private ProductService productService;
	@Resource
	private ProductFeaturesDao productFeaturesDao;
	@Resource
	private I18NService i18nService;
	private CommonI18NService commonI18nService;
	private SearchRestrictionService searchRestrictionService;
	@Resource
	UserService userService;

	private static final Logger LOG = Logger.getLogger(MMMClassificationMoveDaoImpl.class);

	/* Starting Point after impex */
	@Override
	public void addFeatureToNewCategory(final String classAttrCode, final String classCatCode)
	{

		getSearchRestrictionService().disableSearchRestrictions();
		userService.setCurrentUser(userService.getAdminUser());
		/* Start */
		final List<ClassificationAttributeModel> classificationAttributeModelList = getClassifcationAttributePkList(classAttrCode);

		unlinkingOldCategory(classificationAttributeModelList, classCatCode, classAttrCode);
		/* End */

	}

	@Override
	public void productMoveOnly(final String attribute, final String sourceClass, final String targetClass, final String product)
	{
		getSearchRestrictionService().disableSearchRestrictions();
		userService.setCurrentUser(userService.getAdminUser());
		/* Start */
		final List<ClassificationAttributeModel> classificationAttributeModelList = getClassifcationAttributePkList(attribute);
		moveProduct(classificationAttributeModelList, sourceClass, attribute, targetClass, product);
		/* End */
	}

	@Override
	public ClassificationClassModel getClassifcationClassPk(final String classificationClass)
	{
		final Map<String, Object> params = new HashMap<>();
		final String query = "select {pk} from {ClassificationClass} where {code} = ?code";
		params.put("code", classificationClass);
		LOG.info("classfication class query" + query);
		//final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		final SearchResult<ClassificationClassModel> searchResult = flexibleSearchService.search(query, params);
		final List<ClassificationClassModel> result = searchResult.getResult();
		return result.get(0);
	}

	/* Pass calssification attribute code to get classificationAttr Pk */
	@Override
	public ClassificationAttributeModel getClassifcationAttributePk(final String attributeCode)
	{
		final Map<String, Object> params = new HashMap<>();
		final String query = "select {pk} from {ClassificationAttribute} where {code} = ?code";
		params.put("code", attributeCode);
		//final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		final SearchResult<ClassificationAttributeModel> searchResult = flexibleSearchService.search(query, params);
		final List<ClassificationAttributeModel> result = searchResult.getResult();
		return result.get(0);
	}

	@Override
	public List<ClassificationAttributeModel> getClassifcationAttributePkList(final String attributeCode)
	{
		final Map<String, Object> params = new HashMap<>();
		final String query = "select {pk} from {ClassificationAttribute} where {code} = ?code";
		params.put("code", attributeCode);
		// final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		final SearchResult<ClassificationAttributeModel> searchResult = flexibleSearchService.search(query, params);
		final List<ClassificationAttributeModel> result = searchResult.getResult();
		return result;
	}

	@Override
	public List<ClassAttributeAssignmentModel> getClassAttributeAssignments(final String classificationClassCode,
			final String attributeCode)
	{
		final Map<String, Object> params = new HashMap<>();
		final String query = "select {pk} from {ClassAttributeAssignment}  where {ClassificationClass} = ?classificationClassCode and {ClassificationAttribute} = ?attributeCode ";
		params.put("classificationClassCode", classificationClassCode);
		params.put("attributeCode", attributeCode);
		//final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		final SearchResult<ClassAttributeAssignmentModel> searchResult = flexibleSearchService.search(query, params);
		final List<ClassAttributeAssignmentModel> result = searchResult.getResult();
		return result;
	}


	/*
	 * public ClassAttributeAssignmentModel getClassificationAttributeAssignment(String classiClassCode, String
	 * classiAttrCode) { Map<String, Object> subparams = new HashMap<>(); //Query to get all the
	 * ClassAttributeAssignments final String query =
	 * "select {pk} from {ClassAttributeAssignment} where {ClassificationAttribute} = ?ca and {classificationClass} = ?cc"
	 * ; subparams.put("ca", classiAttrCode); subparams.put("cc", classiClassCode); final
	 * SearchResult<ClassAttributeAssignmentModel> subSearchResult = flexibleSearchService.search(query, subparams);
	 * final List<ClassAttributeAssignmentModel> subresult = subSearchResult.getResult(); for
	 * (ClassAttributeAssignmentModel cam : subresult) { return cam; } return null; }
	 */

	/* old code for decorator */
	public void unlinkingOldCategory(final List<ClassificationAttributeModel> classificationAttributeModelList,
			final String newClassificationClassCode, final String newClassAttrCode)
	{

		final FeaturePojo featurePojo = new FeaturePojo();
		ClassAttributeAssignmentModel newClassAttributeAssgModel = null;

		//ClassAttributeAssignmentModel newClassAttributeAssgModel = getClassificationAttributeAssignment(newClassificationClassCode, newClassAttrCode);


		for (final ClassificationAttributeModel classAttributeModel : classificationAttributeModelList)
		{
			final Map<String, Object> subparams = new HashMap<>();

			//Query to get all the ClassAttributeAssignments related to that classificationAttribute
			final String query2 = "select {pk} from {ClassAttributeAssignment} where {ClassificationAttribute} = ?ca";
			subparams.put("ca", classAttributeModel.getPk().toString());
			final SearchResult<ClassAttributeAssignmentModel> subSearchResult = flexibleSearchService.search(query2, subparams);
			final List<ClassAttributeAssignmentModel> subresult = subSearchResult.getResult();
			String currentClassificationCode = StringUtils.EMPTY;

			/* Get here new ClassAttrAssignment model which contains new classificationClass and classificationAttribute */
			for (final ClassAttributeAssignmentModel cam : subresult)
			{
				if (cam.getClassificationClass().getCode().equalsIgnoreCase(newClassificationClassCode))
				{
					newClassAttributeAssgModel = cam;
				}
			}

			for (final ClassAttributeAssignmentModel currentAttrAssngModel : subresult)
			{

				currentClassificationCode = currentAttrAssngModel.getClassificationClass().getCode();

				/* Check for the new classificationClass should not be equal to current modal's ClassificationClass */
				if (!newClassificationClassCode.equalsIgnoreCase(currentClassificationCode))
				{

					/* Get the products associated with this oldAssignmentModel (old one which is to be unlinked) */
					final List<ProductModel> productModelList = getProductsAssociatedWithOldAssignmentModel(currentAttrAssngModel
							.getPk().toString());

					for (final ProductModel productModel : productModelList)
					{

						/* Approach 1: */

						/* Get all the various features associated with this product */
						// FeatureList featureList = classificationService.getFeatures(productModel);

						/* Approach 2: */
						/* Get Exact old feature */
						final Feature exactFeature = classificationService.getFeature(productModel, currentAttrAssngModel);
						if (null != exactFeature)
						{
							System.out.println("#########################################################");
							System.out.println("Exact old Feature Details ->\n 1.Code =" + exactFeature.getCode());
							System.out.println("2.Classification Attribute ="
									+ exactFeature.getClassAttributeAssignment().getClassificationAttribute().getCode()
									+ "\n3.classification class = "
									+ exactFeature.getClassAttributeAssignment().getClassificationClass().getCode());
							System.out.println("4.Product Code =" + productModel.getCode());
							System.out.println("5. Feature Value =" + exactFeature.getValue().getValue());

							//Get the new feature by assignment
							final Feature newFeature = classificationService.getFeature(productModel, newClassAttributeAssgModel);

							/*
							 * If newFeature is not null and it's value is null then only go and insert the value to that
							 * feature else escape this step
							 */
							if (null != newFeature && null == newFeature.getValue())
							{

								System.out.println("#########################################################");
								System.out.println("New Feature Details ->\n 1.Code =" + newFeature.getCode());
								System.out.println("2.Classification Attribute ="
										+ newFeature.getClassAttributeAssignment().getClassificationAttribute().getCode()
										+ "\n3.classification class = "
										+ newFeature.getClassAttributeAssignment().getClassificationClass().getCode());
								System.out.println("4.Product Code =" + productModel.getCode());
								System.out.println("5.Setting Feature Value =" + exactFeature.getValue().getValue());
								newFeature.addValue(new FeatureValue(exactFeature.getValue().getValue()));
								classificationService.setFeature(productModel, newFeature);
							}
							else if (null == newFeature)
							{
								final String newCode = exactFeature.getCode().replace(currentClassificationCode,
										newClassificationClassCode);
								featurePojo.setCode(newCode);
								featurePojo.setProductCode(productModel.getCode());
								featurePojo.setClassificationClassCode(currentClassificationCode);
								featurePojo.setClassificationAttrcode(classAttributeModel.getCode());
								featurePojo.setFeatureValue(exactFeature.getValue());
								featurePojo.setProduct(productModel);
								//overwriteTheFeature(newClassAttributeAssgModel, featurePojo , currentAttrAssngModel);
								writeFeatureValues(productModel, newClassAttributeAssgModel, featurePojo);
							}
						}

						/*
						 * List features = featureList.getFeatures();
						 *
						 * for (Feature feature : featureList) {
						 *
						 * String code = feature.getCode().toUpperCase();
						 *
						 *
						 * // If code contains classAtrributeCode and currentClassificationCode which is not equal to new
						 * classificationclasscode if (code.contains(classAttributeModel.getCode()) &&
						 * code.contains(currentClassificationCode)) {
						 *//* System.out.println(feature.getCode() + "\t ---->" + feature.getValue()); *//*

																																  *//*
																																		 * String newCode
																																		 * = code.replace(
																																		 * currentClassificationCode
																																		 * ,
																																		 * newClassificationClassCode
																																		 * );
																																		 *
																																		 * featurePojo.
																																		 * setCode
																																		 * (newCode);
																																		 * featurePojo.
																																		 * setProductCode(
																																		 * productModel.
																																		 * getCode());
																																		 * featurePojo.
																																		 * setClassificationClassCode
																																		 * (
																																		 * currentClassificationCode
																																		 * ); featurePojo.
																																		 * setClassificationAttrcode
																																		 * (
																																		 * classAttributeModel
																																		 * .getCode());
																																		 * featurePojo.
																																		 * setFeatureValue
																																		 * (
																																		 * feature.getValue
																																		 * ( ));
																																		 * featurePojo.
																																		 * setProduct(
																																		 * productModel);
																																		 *//*
																																			 *
																																			 * //modelService
																																			 * . save(
																																			 * caaHistoryModel
																																			 * );
																																			 *
																																			 * //Get the
																																			 * new feature
																																			 * by
																																			 * assignment
																																			 * Feature
																																			 * newFeature =
																																			 * featureList.
																																			 * getFeatureByAssignment
																																			 * (
																																			 * newClassAttributeAssgModel
																																			 * );
																																			 *//*
																																				 * If
																																				 * newFeature
																																				 * is not
																																				 * null and
																																				 * it's
																																				 * value is
																																				 * null then
																																				 * only go
																																				 * and
																																				 * insert
																																				 * the value
																																				 * to that
																																				 * feature
																																				 * else
																																				 * escape
																																				 * this step
																																				 *//*
																																					 * if (
																																					 * newFeature
																																					 * !=
																																					 * null
																																					 * &&
																																					 * newFeature
																																					 * .
																																					 * getValue
																																					 * () ==
																																					 * null)
																																					 * {
																																					 *
																																					 * System.
																																					 * out.
																																					 * println
																																					 * (
																																					 * "#########################################################"
																																					 * );
																																					 * System
																																					 * .out.
																																					 * println
																																					 * (
																																					 * "New Feature Details ->\n 1.Code ="
																																					 * +
																																					 * newFeature
																																					 * .
																																					 * getCode
																																					 * ());
																																					 * System
																																					 * .out.
																																					 * println
																																					 * (
																																					 * "2.Classification Attribute ="
																																					 * +
																																					 * newFeature
																																					 * .
																																					 * getClassAttributeAssignment
																																					 * ().
																																					 * getClassificationAttribute
																																					 * (
																																					 * ).getCode
																																					 * ( ) +
																																					 * "\n3.classification class = "
																																					 * +
																																					 * newFeature
																																					 * .
																																					 * getClassAttributeAssignment
																																					 * ().
																																					 * getClassificationClass
																																					 * (
																																					 * ).getCode
																																					 * ( ));
																																					 * System
																																					 * .out.
																																					 * println
																																					 * (
																																					 * "4.Product Code ="
																																					 * +
																																					 * productModel
																																					 * .
																																					 * getCode
																																					 * ()) ;
																																					 * newFeature
																																					 * .
																																					 * addValue
																																					 * ( new
																																					 * FeatureValue
																																					 * (
																																					 * feature
																																					 * .
																																					 * getValue
																																					 * ().
																																					 * getValue
																																					 * ()) );
																																					 * classificationService
																																					 * .
																																					 * setFeature
																																					 * (
																																					 * productModel
																																					 * ,
																																					 * newFeature
																																					 * ) ;
																																					 *
																																					 * }
																																					 *//*
																																						 * else
																																						 * {
																																						 * writeFeatureValues
																																						 * (
																																						 * featurePojo
																																						 * .
																																						 * getProduct
																																						 * (),
																																						 * newClassAttributeAssgModel
																																						 * ,
																																						 * featurePojo
																																						 * );
																																						 * }
																																						 *//*
																																							 * }
																																							 *
																																							 *
																																							 * }
																																							 */
					}


					//code for unlinking which is working for decorator.
					if (!newClassificationClassCode.equals(currentClassificationCode))
					{
						System.out.println("Success");
						final List<ClassificationAttributeModel> classificationFeatures = currentAttrAssngModel
								.getClassificationClass().getClassificationAttributes();
						for (final ClassificationAttributeModel feature : classificationFeatures)
						{
							if (feature.getPk() == classAttributeModel.getPk())
							{
								/*
								 * Fetch the productFeatureModal which is related to ClassAttribute Assignment and delete that
								 */
								final List<ProductFeatureModel> featureModelList = getProductFeatureModelForCAA(currentAttrAssngModel
										.getPk().toString());
								for (final ProductFeatureModel productFeatureModel : featureModelList)
								{
									modelService.remove(productFeatureModel.getPk());
								}
								modelService.remove(currentAttrAssngModel.getPk());
							}
						}
					}
				}
			}
		}
	}


	/* Get Product Feature Model */
	public List<ProductFeatureModel> getProductFeatureModelForCAA(final String classAttrAssngPk)
	{
		final Map<String, Object> params = new HashMap<>();
		final String query = "select {pk} from {productfeature} where {classificationAttributeAssignment} = ?caaPk";
		params.put("caaPk", classAttrAssngPk);
		final SearchResult<ProductFeatureModel> searchResult = flexibleSearchService.search(query, params);
		final List<ProductFeatureModel> featureModelList = searchResult.getResult();
		return featureModelList;
	}

	/* pass classAttrAssngPK and get productfeatureModel */
	public List<ProductModel> getProductsAssociatedWithOldAssignmentModel(final String classAttrAssngPK)
	{
		final List<ProductFeatureModel> productFeatureModelList = new ArrayList<>();
		final Map<String, String> featureMap = new HashMap<>();
		final List<ProductFeatureModel> featureModelList = getProductFeatureModelForCAA(classAttrAssngPK);

		//this list stores all the attributes required for retriving the orphan values.
		final List<ProductModel> productModelList = new ArrayList<>();

		for (final ProductFeatureModel productFeatureModel : featureModelList)
		{
			featureMap.put(productFeatureModel.getProduct().getCode(), productFeatureModel.getValue().toString());
			productFeatureModelList.add(productFeatureModel);
			final ProductModel productModel = productFeatureModel.getProduct();
			//add products to product list
			productModelList.add(productModel);
		}
		return productModelList;
	}


	/*
	 * private void overwriteTheFeature(ClassAttributeAssignmentModel newClassAttributeAssgModel, FeaturePojo
	 * featurePojo, ClassAttributeAssignmentModel currentAttrAssngModel) { List<ProductFeatureModel>
	 * productFeatureModelList = getProductFeatureModelForCAA(currentAttrAssngModel.getPk().toString()); for
	 * (ProductFeatureModel productFeatureModel : productFeatureModelList) {
	 * productFeatureModel.setQualifier(featurePojo.getCode());
	 * //productFeatureModel.setClassificationAttributeAssignment(newClassAttributeAssgModel);
	 * //currentAttrAssngModel.setClassificationClass(newClassAttributeAssgModel.getClassificationClass());
	 * modelService.save(currentAttrAssngModel); modelService.save(productFeatureModel);
	 *
	 * } }
	 */


	/* code for creating new feature */
	private void writeFeatureValues(final ProductModel product, final ClassAttributeAssignmentModel assignment,
			final FeaturePojo featurePojo)
	{


		int index = getMaxValuePosition(product, assignment);

		ProductFeatureModel productFeature = null;
		final Locale locale = null;

		productFeature = createNewProductFeature(product, assignment, featurePojo, locale);
		productFeature.setValue(featurePojo.getFeatureValue().getValue());
		productFeature.setValuePosition(Integer.valueOf(++index));
		productFeature.setFeaturePosition(assignment == null ? Integer.valueOf(100) : assignment.getPosition());
		modelService.save(productFeature);
	}


	private ProductFeatureModel createNewProductFeature(final ProductModel product,
			final ClassAttributeAssignmentModel assignment, final FeaturePojo featurePojo, final Locale locale)
	{
		ProductFeatureModel productFeature;
		productFeature = modelService.create(ProductFeatureModel.class);
		productFeature.setProduct(product);
		productFeature.setClassificationAttributeAssignment(assignment);
		productFeature.setQualifier(featurePojo.getCode());
		if (locale != null)
		{
			try
			{
				final Locale dataLocale = i18nService.getBestMatchingLocale(locale);
				final LanguageModel language = commonI18nService.getLanguage(dataLocale.toString());
				productFeature.setLanguage(language);
			}
			catch (final UnknownIdentifierException e)
			{
				LOG.error("Cannot set language for iso code: " + locale.getLanguage());
			}
		}
		return productFeature;
	}

	private int getMaxValuePosition(final ProductModel product, final ClassAttributeAssignmentModel assignment)
	{
		final List<Integer> maxValuePosition = productFeaturesDao.getProductFeatureMaxValuePosition(product, assignment);
		return maxValuePosition.get(0) == null ? 0 : maxValuePosition.get(0).intValue();
	}

	/**
	 * @param classificationAttributeModelList
	 * @param sourceClass
	 * @param attribute
	 * @param targetClass
	 * @param product
	 */
	private void moveProduct(final List<ClassificationAttributeModel> classificationAttributeModelList, final String sourceClass,
			final String attribute, final String targetClass, final String product)
	{
		ClassAttributeAssignmentModel newClassAttributeAssgModel = null;
		for (final ClassificationAttributeModel classAttributeModel : classificationAttributeModelList)
		{
			final Map<String, Object> subparams = new HashMap<>();

			//Query to get all the ClassAttributeAssignments related to that classificationAttribute
			final String query2 = "select {pk} from {ClassAttributeAssignment} where {ClassificationAttribute} = ?ca";
			LOG.info("query for product move" + query2);
			subparams.put("ca", classAttributeModel.getPk().toString());
			final SearchResult<ClassAttributeAssignmentModel> subSearchResult = flexibleSearchService.search(query2, subparams);
			final List<ClassAttributeAssignmentModel> subresult = subSearchResult.getResult();
			String currentClassificationCode = StringUtils.EMPTY;

			/* Get here new ClassAttrAssignment model which contains new classificationClass and classificationAttribute */
			for (final ClassAttributeAssignmentModel cam : subresult)
			{
				if (cam.getClassificationClass().getCode().equalsIgnoreCase(targetClass))
				{
					newClassAttributeAssgModel = cam;
				}
			}

			for (final ClassAttributeAssignmentModel currentAttrAssngModel : subresult)
			{

				currentClassificationCode = currentAttrAssngModel.getClassificationClass().getCode();
				if (sourceClass.equalsIgnoreCase(currentClassificationCode))
				{
					/* Check for the new classificationClass should not be equal to current modal's ClassificationClass */
					if (!targetClass.equalsIgnoreCase(currentClassificationCode))
					{

						/* Get the products associated with this oldAssignmentModel (old one which is to be unlinked) */
						final List<ProductModel> productModelList = getProductsAssociatedWithOldAssignmentModel(currentAttrAssngModel
								.getPk().toString());

						for (final ProductModel productModel : productModelList)
						{

							if (product.equals(productModel.getCode()))
							{
								final FeatureList featureList = classificationService.getFeatures(productModel);
								final Feature exactFeature = featureList.getFeatureByAssignment(currentAttrAssngModel);
								//classificationService.getFeature(productModel, currentAttrAssngModel);
								if (null != exactFeature)
								{
									//Get the new feature by assignment
									final Feature newFeature = classificationService.getFeature(productModel, newClassAttributeAssgModel);
									if (null != newFeature && null == newFeature.getValue())
									{

										if (exactFeature instanceof UnlocalizedFeature)
										{
											//final List<FeatureValue> featureValues = ((UnlocalizedFeature) exactFeature).getValues();
											// This line added for multivalued attribute
											final List<FeatureValue> featureValueList = exactFeature.getValues();
											for (final FeatureValue featureValue : featureValueList)
											{
												newFeature.addValue(new FeatureValue(featureValue.getValue()));
											}
										}
										else if (exactFeature instanceof LocalizedFeature)
										{
											final Map<Locale, List<FeatureValue>> localizedValueList = ((LocalizedFeature) exactFeature)
													.getValuesForAllLocales();
											for (final Locale locale : localizedValueList.keySet())
											{
												final List<FeatureValue> featureValue = localizedValueList.get(locale);
												((LocalizedFeature) newFeature).addValue(featureValue.get(0), locale);
											}


										}


										//Remove from product value
										removeFeaturValue(productModel, currentAttrAssngModel, exactFeature);
										//add feature
										classificationService.setFeature(productModel, newFeature);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/* code for creating new feature */
	@SuppressWarnings("unused")
	private void removeFeaturValue(final ProductModel product, final ClassAttributeAssignmentModel assignment,
			final Feature exactFeature)
	{
		final List<ProductFeatureModel> featureModelList = getProductFeatureModelForCAAPkList(assignment.getPk().toString());
		for (final ProductFeatureModel productFeatureModel : featureModelList)
		{
			if (product.getCode().equals(productFeatureModel.getProduct().getCode()))
			{
				modelService.remove(productFeatureModel);
			}

		}
	}

	public List<ProductFeatureModel> getProductFeatureModelForCAAPkList(final String classAttrAssngPk)
	{
		final Map<String, Object> params = new HashMap<>();
		final String query = "select {pk} from {productfeature} where {classificationAttributeAssignment} = ?caaPk";
		params.put("caaPk", classAttrAssngPk);
		final SearchResult<ProductFeatureModel> searchResult = flexibleSearchService.search(query, params);
		final List<ProductFeatureModel> featureModelList = searchResult.getResult();
		return featureModelList;
	}

	@Required
	public void setCommonI18nService(final CommonI18NService commonI18nService)
	{
		this.commonI18nService = commonI18nService;
	}

	public void testSample(final ClassAttributeAssignment classAttributeAssignmentModel)
	{
		classAttributeAssignmentModel.getPK();
		System.out.println("<--- Test System --->");
	}

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}
}
