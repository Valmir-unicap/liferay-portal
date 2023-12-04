/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.lifecycle;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.util.MapUtil;

import org.apache.aries.jax.rs.whiteboard.WhiteboardUtil;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Stian Sigvartsen
 */
public class JAXRSLifecycle {

	public static JAXRSLifecycle getInstance() {
		if (_jaxrsLifecycle == null) {
			_jaxrsLifecycle = new JAXRSLifecycle();
		}

		return _jaxrsLifecycle;
	}

	public void ensureReady() {
		Bundle bundle = FrameworkUtil.getBundle(JAXRSLifecycle.class);

		_serviceRegistrationDCLSingleton.getSingleton(
			() -> {
				WhiteboardUtil.start();

				return bundle.getBundleContext(
				).registerService(
					Object.class, new Object(),
					MapUtil.singletonDictionary(
						"liferay.jaxrs.whiteboard.ready", true)
				);
			});
	}

	public void ensureUnready() {
		_serviceRegistrationDCLSingleton.destroy(
			serviceRegistration -> {
				serviceRegistration.unregister();

				WhiteboardUtil.stop();
			});
	}

	private JAXRSLifecycle() {
	}

	private static JAXRSLifecycle _jaxrsLifecycle;

	private final DCLSingleton<ServiceRegistration<?>>
		_serviceRegistrationDCLSingleton = new DCLSingleton<>();

}