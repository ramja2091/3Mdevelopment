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
package com.mmm.gpim.v2.helper;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductCategorySearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import com.mmm.gpim.util.ws.SearchQueryCodec;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;


@Component
public class ProductsHelper extends AbstractHelper
{
	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;
	@Resource(name = "cwsSearchQueryCodec")
	private SearchQueryCodec<SolrSearchQueryData> searchQueryCodec;
	@Resource(name = "solrSearchStateConverter")
	private Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;

	@Cacheable(value = "productSearchCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'DTO',#query,#currentPage,#pageSize,#sort,#fields)")
	public ProductSearchPageWsDTO searchProducts(final String query, final int currentPage, final int pageSize, final String sort,
			final String fields)
	{
		final ProductSearchPageData<SearchStateData, ProductData> sourceResult = searchProducts(query, currentPage, pageSize, sort);
		if (sourceResult instanceof ProductCategorySearchPageData)
		{
			return getDataMapper().map(sourceResult, ProductCategorySearchPageWsDTO.class, fields);
		}

		return getDataMapper().map(sourceResult, ProductSearchPageWsDTO.class, fields);
	}

	@Cacheable(value = "productSearchCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'Data',#query,#currentPage,#pageSize,#sort)")
	public ProductSearchPageData<SearchStateData, ProductData> searchProducts(final String query, final int currentPage,
			final int pageSize, final String sort)
	{
		final SolrSearchQueryData searchQueryData = searchQueryCodec.decodeQuery(query);
		final PageableData pageable = createPageableData(currentPage, pageSize, sort);

		return productSearchFacade.textSearch(solrSearchStateConverter.convert(searchQueryData), pageable);
	}
}
