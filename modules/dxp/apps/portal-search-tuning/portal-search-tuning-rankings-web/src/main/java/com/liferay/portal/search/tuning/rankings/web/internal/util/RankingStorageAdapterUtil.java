/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.search.tuning.rankings.web.internal.index.Ranking;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexWriter;
import com.liferay.portal.search.tuning.rankings.web.internal.index.name.RankingIndexName;

/**
 * @author Bryan Engler
 */
public class RankingStorageAdapterUtil {

	public static String create(
		Ranking ranking, RankingIndexName rankingIndexName,
		RankingIndexWriter rankingIndexWriter) {

		String rankingDocumentId = RankingJSONStorageUtil.addJSONStorageEntry(
			ranking);

		Ranking.RankingBuilder rankingBuilder = new Ranking.RankingBuilder(
			ranking);

		rankingBuilder.rankingDocumentId(rankingDocumentId);

		rankingIndexWriter.create(rankingIndexName, rankingBuilder.build());

		return rankingDocumentId;
	}

	public static void delete(
			String rankingDocumentId, RankingIndexName rankingIndexName,
			RankingIndexWriter rankingIndexWriter)
		throws PortalException {

		RankingJSONStorageUtil.deleteJSONStorageEntry(rankingDocumentId);

		rankingIndexWriter.remove(rankingIndexName, rankingDocumentId);
	}

	public static void update(
			Ranking ranking, RankingIndexName rankingIndexName,
			RankingIndexWriter rankingIndexWriter)
		throws PortalException {

		RankingJSONStorageUtil.updateJSONStorageEntry(ranking);

		rankingIndexWriter.update(rankingIndexName, ranking);
	}

}