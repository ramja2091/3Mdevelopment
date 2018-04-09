package com.mmm.gpim.pojolayer;

import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;


/**
 * Created by sao3960 on 10/3/18.
 */
public class FeaturePojo
{

	//this is the fullyqualifiedattribute name
	String code;
	//code of fetaure - gxj24gr
	String classificationAttrcode;
	//name of feature - Material
	String classificationAttrname;
	//value of feature
	FeatureValue featureValue;
	//feature applied on which product
	String productCode;
	//Pk of classificationAttrAssng class
	PK classAttrAssngPk;
	//code of classification class

	public ProductModel getProduct()
	{
		return product;
	}

	public void setProduct(final ProductModel product)
	{
		this.product = product;
	}

	String classificationClassCode;
	ProductModel product;

	public String getCode()
	{
		return code;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	public FeatureValue getFeatureValue()
	{
		return featureValue;
	}

	public void setFeatureValue(final FeatureValue featureValue)
	{
		this.featureValue = featureValue;
	}

	public String getClassificationAttrcode()
	{
		return classificationAttrcode;
	}

	public void setClassificationAttrcode(final String classificationAttrcode)
	{
		this.classificationAttrcode = classificationAttrcode;
	}

	public String getClassificationAttrname()
	{
		return classificationAttrname;
	}

	public void setClassificationAttrname(final String classificationAttrname)
	{
		this.classificationAttrname = classificationAttrname;
	}

	public String getProductCode()
	{
		return productCode;
	}

	public void setProductCode(final String productCode)
	{
		this.productCode = productCode;
	}

	public String getClassificationClassCode()
	{
		return classificationClassCode;
	}

	public void setClassificationClassCode(final String classificationClassCode)
	{
		this.classificationClassCode = classificationClassCode;
	}


	public PK getClassAttrAssngPk()
	{
		return classAttrAssngPk;
	}

	public void setClassAttrAssngPk(final PK classAttrAssngPk)
	{
		this.classAttrAssngPk = classAttrAssngPk;
	}


}
