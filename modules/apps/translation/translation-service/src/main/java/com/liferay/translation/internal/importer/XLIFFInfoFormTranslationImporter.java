/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.translation.internal.importer;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.translation.exception.XLIFFFileException;
import com.liferay.translation.importer.TranslationInfoItemFieldValuesImporter;
import com.liferay.translation.internal.util.XLIFFLocaleIdUtil;
import com.liferay.translation.internal.util.XLIFFTranslationImporterUtil;
import com.liferay.translation.snapshot.TranslationSnapshot;
import com.liferay.translation.snapshot.TranslationSnapshotProvider;

import java.io.CharConversionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiIllegalFilterOperationException;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.filters.autoxliff.AutoXLIFFFilter;
import net.sf.okapi.lib.xliff2.InvalidParameterException;
import net.sf.okapi.lib.xliff2.XLIFFException;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.Part;
import net.sf.okapi.lib.xliff2.core.StartXliffData;
import net.sf.okapi.lib.xliff2.core.Unit;
import net.sf.okapi.lib.xliff2.document.XLIFFDocument;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "content.type=application/xliff+xml",
	service = TranslationInfoItemFieldValuesImporter.class
)

public class XLIFFInfoFormTranslationImporter
	implements TranslationInfoItemFieldValuesImporter{

	@Override
	public InfoItemFieldValues importInfoItemFieldValues(
			long groupId, InfoItemReference infoItemReference,
			InputStream inputStream)
		throws IOException, XLIFFFileException {


		TranslationSnapshot translationSnapshot = XLIFFTranslationImporterUtil._getTranslationSnapshot(
			groupId, infoItemReference, inputStream, false);

		return translationSnapshot.getInfoItemFieldValues();
	}

	private InfoItemReference _getInfoItemReference(XLIFFDocument xliffDocument)
		throws XLIFFFileException {

		List<String> fileNodeIds = xliffDocument.getFileNodeIds();

		Matcher matcher = _pattern.matcher(fileNodeIds.get(0));

		if (!matcher.matches()) {
			throw new XLIFFFileException.MustBeWellFormed(
				"The XLIFF file is not well formed");
		}

		return new InfoItemReference(
			matcher.group(1), GetterUtil.getLong(matcher.group(2)));
	}




	private static final Pattern _pattern = Pattern.compile("([^:]+):(.+)");

}