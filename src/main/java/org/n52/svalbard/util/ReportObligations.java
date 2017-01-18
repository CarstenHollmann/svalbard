/*
 * Copyright 2016-2017 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.svalbard.util;

import org.n52.janmayen.function.Functions;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.swe.simpleType.SweText;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public final class ReportObligations {
    private ReportObligations() {
    }

    public static boolean hasFlow(Extensions extensions) {
        return extensions != null && extensions.containsExtension(AqdConstants.EXTENSION_FLOW);
    }

    public static ReportObligationType getFlow(Extensions extensions) throws OwsExceptionReport {
        return extensions.getExtension(AqdConstants.EXTENSION_FLOW)
                .map(Extension::getValue)
                .flatMap(Functions.castIfInstanceOf(SweText.class))
                .map(SweText::getValue)
                .map(ReportObligationType::from)
                .orElse(ReportObligationType.E2A);
    }
}