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
package org.n52.svalbard.write;

import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlOptions;
import org.n52.janmayen.Producer;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.EncodingContext;

public class GwmlV22XmlStreamWriter extends OmV20XmlStreamWriter {

    public GwmlV22XmlStreamWriter(OutputStream outputStream, EncodingContext context,
            EncoderRepository encoderRepository, Producer<XmlOptions> xmlOptions, OmObservation element)
            throws XMLStreamException {
        super(outputStream, context, encoderRepository, xmlOptions, element);
    }

}