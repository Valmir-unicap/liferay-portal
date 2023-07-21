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

package com.liferay.adaptive.media.image.internal.util.comparator;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AMDistanceComparator;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.portal.kernel.repository.model.FileVersion;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class AMPropertyDistanceComparator
	implements AMDistanceComparator
		<AdaptiveMedia<AMProcessor<FileVersion, AMProcessor>>> {

	public AMPropertyDistanceComparator(
		Map<AMAttribute<AMProcessor<FileVersion, AMProcessor>, ?>, ?>
			amAttributes) {

		_amAttributes = amAttributes;
	}

	@Override
	public long compare(
		AdaptiveMedia<AMProcessor<FileVersion, AMProcessor>> adaptiveMedia1,
		AdaptiveMedia<AMProcessor<FileVersion, AMProcessor>> adaptiveMedia2) {

		for (Map.Entry<AMAttribute<AMProcessor<FileVersion, AMProcessor>, ?>, ?>
				entry : _amAttributes.entrySet()) {

			AMAttribute<AMProcessor<FileVersion, AMProcessor>, Object>
				amAttribute =
					(AMAttribute<AMProcessor<FileVersion, AMProcessor>, Object>)
						entry.getKey();

			Object value1 = adaptiveMedia1.getValue(amAttribute);
			Object value2 = adaptiveMedia2.getValue(amAttribute);

			if ((value1 != null) && (value2 != null)) {
				Object requestedValue = entry.getValue();

				long valueDistance1 = amAttribute.distance(
					value1, requestedValue);

				long valueDistance2 = amAttribute.distance(
					value2, requestedValue);

				long result = valueDistance1 - valueDistance2;

				if (result != 0) {
					return result;
				}
			}
		}

		return 0L;
	}

	private final Map<AMAttribute<AMProcessor<FileVersion, AMProcessor>, ?>, ?>
		_amAttributes;

}