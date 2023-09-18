/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.resolver;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.opensaml.integration.internal.metadata.MetadataManager;
import com.liferay.saml.opensaml.integration.resolver.NameIdResolver;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Mika Koivisto
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MIN_VALUE,
	service = NameIdResolver.class
)
public class DefaultNameIdResolver implements NameIdResolver {

	public NameIdResolver getNameIdResolver(String entityId) {
		long companyId = CompanyThreadLocal.getCompanyId();

		NameIdResolver nameIdResolver = _serviceTrackerMap.getService(
			companyId + "," + entityId);

		if (nameIdResolver == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"No attribute resolver for company ID ", companyId,
						" and entity ID ", entityId));
			}

			nameIdResolver = _defaultNameIdResolver;
		}

		return nameIdResolver;
	}

	@Override
	public String resolve(
		User user, String entityId, String format, String spQualifierName,
		boolean allowCreate,
		NameIdResolverSAMLContext nameIdResolverSAMLContext) {

		return _getNameIdValue(user, entityId);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, NameIdResolver.class, "(companyId=*)",
			new DefaultServiceReferenceMapper(_log));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private String _getNameIdAttributeName(String entityId) {
		return _metadataManager.getNameIdAttribute(entityId);
	}

	private String _getNameIdValue(User user, String entityId) {
		String nameIdAttributeName = _getNameIdAttributeName(entityId);

		if (Validator.isNull(nameIdAttributeName)) {
			return user.getEmailAddress();
		}

		if (nameIdAttributeName.startsWith("expando:")) {
			String attributeName = nameIdAttributeName.substring(8);

			ExpandoBridge expandoBridge = user.getExpandoBridge();

			return _toString(expandoBridge.getAttribute(attributeName));
		}

		if (nameIdAttributeName.startsWith("static:")) {
			return nameIdAttributeName.substring(7);
		}

		return _toString(_beanProperties.getObject(user, nameIdAttributeName));
	}

	private String _toString(Object object) {
		if (object == null) {
			return StringPool.BLANK;
		}

		return object.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultNameIdResolver.class);

	private static ServiceTrackerMap<String, NameIdResolver> _serviceTrackerMap;

	@Reference
	private BeanProperties _beanProperties;

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY, target = "(!(companyId=*))"
	)
	private NameIdResolver _defaultNameIdResolver;

	@Reference
	private MetadataManager _metadataManager;

}