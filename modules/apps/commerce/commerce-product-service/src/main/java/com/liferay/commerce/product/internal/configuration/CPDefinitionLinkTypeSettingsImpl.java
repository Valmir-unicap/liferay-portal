/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.configuration;

import com.liferay.commerce.product.configuration.CPDefinitionLinkTypeConfiguration;
import com.liferay.commerce.product.configuration.CPDefinitionLinkTypeSettings;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = CPDefinitionLinkTypeSettings.class)
public class CPDefinitionLinkTypeSettingsImpl
	implements CPDefinitionLinkTypeSettings {

	@Override
	public String[] getTypes() {
		return ArrayUtil.toStringArray(_pidTypeMap.keySet());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_managedServiceFactoryServiceRegistration =
			bundleContext.registerService(
				ManagedServiceFactory.class,
				new CPDefinitionLinkTypeManagedServiceFactory(),
				HashMapDictionaryBuilder.put(
					Constants.SERVICE_PID,
					"com.liferay.commerce.product.configuration." +
						"CPDefinitionLinkTypeConfiguration"
				).build());
	}

	@Deactivate
	protected void deactivate() {
		_managedServiceFactoryServiceRegistration.unregister();
	}

	private static final Map<String, String> _pidTypeMap = new HashMap<>();

	private ServiceRegistration<ManagedServiceFactory>
		_managedServiceFactoryServiceRegistration;

	private class CPDefinitionLinkTypeManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			_pidTypeMap.remove(pid);
		}

		@Override
		public String getName() {
			return "com.liferay.commerce.product.configuration." +
				"CPDefinitionLinkTypeConfiguration";
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> dictionary)
			throws ConfigurationException {

			CPDefinitionLinkTypeConfiguration
				cpDefinitionLinkTypeConfiguration =
					ConfigurableUtil.createConfigurable(
						CPDefinitionLinkTypeConfiguration.class, dictionary);

			_pidTypeMap.put(pid, cpDefinitionLinkTypeConfiguration.type());
		}

	}

}