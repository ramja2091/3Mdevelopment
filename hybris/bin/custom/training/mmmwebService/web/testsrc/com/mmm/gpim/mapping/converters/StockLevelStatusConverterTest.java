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
package com.mmm.gpim.mapping.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;

import junit.framework.Assert;
import ma.glasnost.orika.metadata.Type;

import org.junit.Test;


@UnitTest
public class StockLevelStatusConverterTest
{
	private final StockLevelStatusConverter converter = new StockLevelStatusConverter();
	private final String stringStatus = StockLevelStatus.INSTOCK.toString();
	private final StockLevelStatus status = StockLevelStatus.INSTOCK;

	@Test
	public void testConvertFrom()
	{
		final StockLevelStatus result = converter.convertFrom(stringStatus, (Type<StockLevelStatus>) null);
		Assert.assertEquals(status, result);
	}

	@Test
	public void testConvertTo()
	{
		final String result = converter.convertTo(status, (Type<String>) null);
		Assert.assertEquals(stringStatus, result);
	}
}
