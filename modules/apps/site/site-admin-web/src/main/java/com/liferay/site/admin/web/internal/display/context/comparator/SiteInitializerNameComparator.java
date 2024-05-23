/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.display.context.comparator;

import com.liferay.site.admin.web.internal.util.SiteInitializerItem;

import java.util.Comparator;

/**
 * @author Marco Leo
 */
public class SiteInitializerNameComparator
	implements Comparator<SiteInitializerItem> {

	public static SiteInitializerNameComparator getInstance(boolean ascending) {
		return _ASCENDING;
	}

	@Override
	public int compare(
		SiteInitializerItem siteInitializerItem1,
		SiteInitializerItem siteInitializerItem2) {

		String name1 = siteInitializerItem1.getName();
		String name2 = siteInitializerItem2.getName();

		int value = name1.compareToIgnoreCase(name2);

		if (_ascending) {
			return value;
		}

		return -value;
	}

	private SiteInitializerNameComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final SiteInitializerNameComparator _ASCENDING =
		new SiteInitializerNameComparator(true);

	private final boolean _ascending;

}