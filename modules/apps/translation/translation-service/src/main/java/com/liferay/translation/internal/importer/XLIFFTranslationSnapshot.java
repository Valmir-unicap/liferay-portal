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
 * @author Valmir Junior
 */
@Component(
	property = "content.type=application/xliff+xml",
	service = {
		TranslationSnapshotProvider.class
	}
)


public class XLIFFTranslationSnapshot implements TranslationSnapshotProvider{
	@Override
	public TranslationSnapshot getTranslationSnapshot(
		long groupId, InfoItemReference infoItemReference,
		InputStream inputStream)
		throws IOException, PortalException {

		return _getTranslationSnapshot(
			groupId, infoItemReference, inputStream, true);
	}


	private TranslationSnapshot _getTranslationSnapshot(
		long groupId, InfoItemReference infoItemReference,
		InputStream inputStream, boolean includeSource)
		throws IOException, XLIFFFileException {

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		currentThread.setContextClassLoader(
			XLIFFInfoFormTranslationImporter.class.getClassLoader());

		try (AutoXLIFFFilter autoXLIFFFilter = new AutoXLIFFFilter()) {
			List<Event> events = new ArrayList<>();

			File tempFile = FileUtil.createTempFile(inputStream);

			Document document = _saxReader.read(tempFile);

			LocaleId sourceLocaleId = XLIFFLocaleIdUtil.getSourceLocaleId(
				document);
			LocaleId targetLocaleId = XLIFFLocaleIdUtil.getTargetLocaleId(
				document);

			autoXLIFFFilter.open(
				new RawDocument(
					tempFile.toURI(), document.getXMLEncoding(), sourceLocaleId,
					targetLocaleId));

			while (autoXLIFFFilter.hasNext()) {
				events.add(autoXLIFFFilter.next());
			}

			if (_isVersion20(events)) {
				return new TranslationSnapshot(
					_getInfoItemFieldValuesXLIFFv20(
						groupId, infoItemReference, tempFile, includeSource),
					LocaleUtil.fromLanguageId(sourceLocaleId.toString()),
					LocaleUtil.fromLanguageId(targetLocaleId.toString()));
			}

			return new TranslationSnapshot(
				_getInfoItemFieldValuesXLIFFv12(
					events, infoItemReference, includeSource),
				LocaleUtil.fromLanguageId(sourceLocaleId.toString()),
				LocaleUtil.fromLanguageId(targetLocaleId.toString()));
		}
		catch (OkapiIllegalFilterOperationException | XLIFFException
			exception) {

			if (exception.getCause() instanceof CharConversionException) {
				throw new XLIFFFileException.MustHaveCorrectEncoding(exception);
			}

			throw new XLIFFFileException.MustBeValid(exception);
		}
		catch (DocumentException documentException) {
			throw new XLIFFFileException.MustHaveCorrectEncoding(
				documentException);
		}
		catch (InvalidParameterException invalidParameterException) {
			throw new XLIFFFileException.MustHaveValidParameter(
				invalidParameterException);
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}


	private boolean _isVersion20(List<Event> events) {
		for (Event event : events) {
			if (event.isStartDocument()) {
				StartDocument startDocument = event.getStartDocument();

				Property versionProperty = startDocument.getProperty("version");

				if (versionProperty != null) {
					double version = GetterUtil.getDouble(
						versionProperty.getValue());

					if ((version >= 2.0) && (version < 3.0)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Reference
	private SAXReader _saxReader;

	@Reference
	private XLIFFInfoFormTranslationImporter _xliffInfoFormTranslationImporter;

}