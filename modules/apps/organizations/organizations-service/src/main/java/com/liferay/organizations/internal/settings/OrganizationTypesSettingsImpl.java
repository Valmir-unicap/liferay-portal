/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.internal.settings;

import com.liferay.organizations.internal.configuration.OrganizationTypeConfiguration;
import com.liferay.organizations.internal.configuration.OrganizationTypeConfigurationWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.users.admin.kernel.organization.types.OrganizationTypesSettings;

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
 * @author Marco Leo
 */
@Component(service = OrganizationTypesSettings.class)
public class OrganizationTypesSettingsImpl
	implements OrganizationTypesSettings {

	@Override
	public String[] getChildrenTypes(String type) {
		OrganizationTypeConfigurationWrapper
			organizationTypeConfigurationWrapper =
				_getOrganizationTypeConfigurationWrapper(type);

		if (organizationTypeConfigurationWrapper == null) {
			return new String[0];
		}

		return organizationTypeConfigurationWrapper.getChildrenTypes();
	}

	@Override
	public String[] getTypes() {
		return ArrayUtil.toStringArray(
			_organizationTypeConfigurationWrapperServiceTrackerMap.keySet());
	}

	@Override
	public boolean isCountryEnabled(String type) {
		OrganizationTypeConfigurationWrapper
			organizationTypeConfigurationWrapper =
				_getOrganizationTypeConfigurationWrapper(type);

		if (organizationTypeConfigurationWrapper == null) {
			return false;
		}

		return organizationTypeConfigurationWrapper.isCountryEnabled();
	}

	@Override
	public boolean isCountryRequired(String type) {
		OrganizationTypeConfigurationWrapper
			organizationTypeConfigurationWrapper =
				_getOrganizationTypeConfigurationWrapper(type);

		if (organizationTypeConfigurationWrapper == null) {
			return false;
		}

		return organizationTypeConfigurationWrapper.isCountryRequired();
	}

	@Override
	public boolean isRootable(String type) {
		OrganizationTypeConfigurationWrapper
			organizationTypeConfigurationWrapper =
				_getOrganizationTypeConfigurationWrapper(type);

		if (organizationTypeConfigurationWrapper == null) {
			return false;
		}

		return organizationTypeConfigurationWrapper.isRootable();
	}

	public class OrganizationTypeConfigurationManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			String organizationTypeConfiguration =
				_configurationPidOrganizationTypeConfigurationName.remove(pid);

			_organizationTypeConfigurationNameOrganizationTypeConfiguration.
				remove(organizationTypeConfiguration);
		}

		@Override
		public String getName() {
			return "com.liferay.organizations.internal.configuration." +
				"OrganizationTypeConfiguration";
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> dictionary)
			throws ConfigurationException {

			OrganizationTypeConfiguration organizationTypeConfiguration =
				ConfigurableUtil.createConfigurable(
					OrganizationTypeConfiguration.class, dictionary);

			_configurationPidOrganizationTypeConfigurationName.put(
				pid, organizationTypeConfiguration.name());
			_organizationTypeConfigurationNameOrganizationTypeConfiguration.put(
				organizationTypeConfiguration.name(),
				organizationTypeConfiguration);
		}

	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_managedServiceFactoryServiceRegistration =
			bundleContext.registerService(
				ManagedServiceFactory.class,
				new OrganizationTypeConfigurationManagedServiceFactory(),
				HashMapDictionaryBuilder.put(
					Constants.SERVICE_PID,
					"com.liferay.organizations.internal.configuration." +
						"OrganizationTypeConfiguration"
				).build());

		_organizationTypeConfigurationWrapperServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, OrganizationTypeConfigurationWrapper.class, null,
				ServiceReferenceMapperFactory.create(
					bundleContext,
					(organizationTypeConfigurationWrapper, emitter) ->
						emitter.emit(
							organizationTypeConfigurationWrapper.getName())));
	}

	@Deactivate
	protected void deactivate() {
		_managedServiceFactoryServiceRegistration.unregister();
		_organizationTypeConfigurationWrapperServiceTrackerMap.close();
	}

	private OrganizationTypeConfigurationWrapper
		_getOrganizationTypeConfigurationWrapper(String type) {

		OrganizationTypeConfigurationWrapper
			organizationTypeConfigurationWrapper =
				_organizationTypeConfigurationWrapperServiceTrackerMap.
					getService(type);

		if (organizationTypeConfigurationWrapper == null) {
			_log.error("Unable to get organization type: " + type);
		}

		return organizationTypeConfigurationWrapper;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrganizationTypesSettingsImpl.class);

	private final Map<String, String>
		_configurationPidOrganizationTypeConfigurationName = new HashMap<>();
	private ServiceRegistration<ManagedServiceFactory>
		_managedServiceFactoryServiceRegistration;
	private final Map<String, OrganizationTypeConfiguration>
		_organizationTypeConfigurationNameOrganizationTypeConfiguration =
			new HashMap<>();
	private ServiceTrackerMap<String, OrganizationTypeConfigurationWrapper>
		_organizationTypeConfigurationWrapperServiceTrackerMap;

}