/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.util;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.json.storage.service.JSONStorageEntryLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.tuning.rankings.web.internal.index.Ranking;

/**
 * @author Bryan Engler
 */
public class RankingJSONStorageUtil {

	public static String addJSONStorageEntry(Ranking ranking) {
		long classPK = CounterLocalServiceUtil.increment();

		String rankingDocumentId =
			Ranking.class.getName() + "_PORTLET_" + classPK;

		JSONStorageEntryLocalServiceUtil.addJSONStorageEntries(
			CompanyThreadLocal.getCompanyId(),
			ClassNameLocalServiceUtil.getClassNameId(Ranking.class), classPK,
			JSONUtil.put(
				"aliases", JSONFactoryUtil.createJSONArray(ranking.getAliases())
			).put(
				"groupExternalReferenceCode",
				ranking.getGroupExternalReferenceCode()
			).put(
				"hiddenDocumentIds",
				JSONFactoryUtil.createJSONArray(ranking.getHiddenDocumentIds())
			).put(
				"indexName", ranking.getIndexName()
			).put(
				"name", ranking.getName()
			).put(
				"pins", _getPinsJSONArray(ranking)
			).put(
				"queryString", ranking.getQueryString()
			).put(
				"rankingDocumentId", rankingDocumentId
			).put(
				"status", ranking.getStatus()
			).put(
				"sxpBlueprintExternalReferenceCode",
				ranking.getSXPBlueprintExternalReferenceCode()
			).toString());

		return rankingDocumentId;
	}

	public static void deleteJSONStorageEntry(String rankingDocumentId)
		throws PortalException {

		JSONStorageEntryLocalServiceUtil.deleteJSONStorageEntries(
			ClassNameLocalServiceUtil.getClassNameId(Ranking.class),
			_getClassPK(rankingDocumentId));
	}

	public static void updateJSONStorageEntry(Ranking ranking)
		throws PortalException {

		long classPK = _getClassPK(ranking.getRankingDocumentId());

		JSONObject jsonObject = JSONStorageEntryLocalServiceUtil.getJSONObject(
			ClassNameLocalServiceUtil.getClassNameId(Ranking.class), classPK);

		jsonObject.put(
			"aliases", JSONFactoryUtil.createJSONArray(ranking.getAliases())
		).put(
			"groupExternalReferenceCode",
			ranking.getGroupExternalReferenceCode()
		).put(
			"hiddenDocumentIds",
			JSONFactoryUtil.createJSONArray(ranking.getHiddenDocumentIds())
		).put(
			"name", ranking.getName()
		).put(
			"pins", _getPinsJSONArray(ranking)
		).put(
			"status", ranking.getStatus()
		).put(
			"sxpBlueprintExternalReferenceCode",
			ranking.getSXPBlueprintExternalReferenceCode()
		);

		JSONStorageEntryLocalServiceUtil.updateJSONStorageEntries(
			CompanyThreadLocal.getCompanyId(),
			ClassNameLocalServiceUtil.getClassNameId(Ranking.class), classPK,
			jsonObject.toString());
	}

	private static long _getClassPK(String rankingDocumentId)
		throws PortalException {

		String[] parts = StringUtil.split(rankingDocumentId, "_PORTLET_");

		if (parts.length != 2) {
			_log.error(
				StringBundler.concat(
					"Ranking document ID ", rankingDocumentId, " has an ",
					"unexpected format. Rankings may need to be imported to ",
					"the database via the rankings database importer Groovy ",
					"script before they can be edited or deleted."));

			throw new PortalException();
		}

		return Long.valueOf(parts[1]);
	}

	private static JSONArray _getPinsJSONArray(Ranking ranking) {
		JSONArray pinsJSONArray = JSONFactoryUtil.createJSONArray();

		for (Ranking.Pin pin : ranking.getPins()) {
			pinsJSONArray.put(
				JSONUtil.put(
					"documentId", pin.getDocumentId()
				).put(
					"position", pin.getPosition()
				));
		}

		return pinsJSONArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RankingJSONStorageUtil.class);

}