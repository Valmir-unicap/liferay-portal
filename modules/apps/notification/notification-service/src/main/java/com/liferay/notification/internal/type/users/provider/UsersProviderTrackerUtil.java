/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.users.provider;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Feliphe Marinho
 */
public class UsersProviderTrackerUtil {

	public static UsersProvider getUsersProvider(String recipientType) {
		return _serviceTrackerMap.getService(recipientType);
	}

	private static final ServiceTrackerMap<String, UsersProvider>
		_serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(UsersProviderTrackerUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, UsersProvider.class, "recipient.type");
	}

}