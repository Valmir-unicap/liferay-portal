/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.util.PortletKeys;
import org.osgi.service.component.annotations.Component;

/**
 * @author Valmir Junior
 */
@Component(
	property = {
		"javax.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"javax.portlet.name=" + PortletKeys.SERVER_ADMIN,
		"mvc.command.name=/server_admin/edit_server"
	},
	service = {IdentifiableOSGiService.class}
)
	public class EditServerIdentifiableOSGIService implements IdentifiableOSGiService{

	@Override
	public String getOSGiServiceIdentifier() {
		return EditServerMVCActionCommand.class.getName();
	}

}