/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.storage;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSet;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexWriter;
import com.liferay.portal.search.tuning.synonyms.web.internal.storage.helper.SynonymSetJSONStorageHelper;
import com.liferay.portal.search.tuning.synonyms.web.internal.storage.util.SynonymSetStorageAdapterUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class SynonymSetStorageAdapterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCreate() {
		Mockito.doReturn(
			"synonymSetDocumentId"
		).when(
			_synonymSetJSONStorageHelper
		).addJSONStorageEntry(
			Mockito.nullable(String.class), Mockito.nullable(String.class)
		);

		Assert.assertEquals(
			"synonymSetDocumentId",
			SynonymSetStorageAdapterUtil.create(
				Mockito.mock(SynonymSetIndexName.class),
				Mockito.mock(SynonymSet.class), _synonymSetIndexWriter,
				_synonymSetJSONStorageHelper));

		Mockito.verify(
			_synonymSetIndexWriter, Mockito.times(1)
		).create(
			Mockito.any(), Mockito.any()
		);
	}

	@Test
	public void testDelete() throws Exception {
		SynonymSetStorageAdapterUtil.delete(
			Mockito.mock(SynonymSetIndexName.class),
			"synonymSetDocumentId_PORTLET_1112", _synonymSetIndexWriter,
			_synonymSetJSONStorageHelper);

		Mockito.verify(
			_synonymSetIndexWriter, Mockito.times(1)
		).remove(
			Mockito.any(), Mockito.anyString()
		);
	}

	@Test(expected = PortalException.class)
	public void testDeleteException() throws Exception {
		SynonymSetStorageAdapterUtil.delete(
			Mockito.mock(SynonymSetIndexName.class),
			"synonymSetDocumentId_PORTLET", _synonymSetIndexWriter,
			_synonymSetJSONStorageHelper);
	}

	@Test
	public void testUpdate() throws Exception {
		SynonymSet synonymSet = Mockito.mock(SynonymSet.class);

		Mockito.doReturn(
			"synonymSetDocumentId_PORTLET_1112"
		).when(
			synonymSet
		).getSynonymSetDocumentId();

		SynonymSetStorageAdapterUtil.update(
			Mockito.mock(SynonymSetIndexName.class), synonymSet,
			_synonymSetIndexWriter, _synonymSetJSONStorageHelper);

		Mockito.verify(
			_synonymSetIndexWriter, Mockito.times(1)
		).update(
			Mockito.any(), Mockito.any()
		);
	}

	@Test(expected = PortalException.class)
	public void testUpdateException() throws Exception {
		SynonymSet synonymSet = Mockito.mock(SynonymSet.class);

		Mockito.doReturn(
			"synonymSetDocumentId_PORTLET"
		).when(
			synonymSet
		).getSynonymSetDocumentId();

		SynonymSetStorageAdapterUtil.update(
			Mockito.mock(SynonymSetIndexName.class), synonymSet,
			_synonymSetIndexWriter, _synonymSetJSONStorageHelper);
	}

	private final SynonymSetIndexWriter _synonymSetIndexWriter = Mockito.mock(
		SynonymSetIndexWriter.class);
	private final SynonymSetJSONStorageHelper _synonymSetJSONStorageHelper =
		Mockito.mock(SynonymSetJSONStorageHelper.class);

}