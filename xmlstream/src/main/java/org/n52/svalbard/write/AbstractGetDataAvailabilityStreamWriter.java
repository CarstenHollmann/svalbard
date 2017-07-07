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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.joda.time.DateTime;
import org.n52.janmayen.Producer;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.gml.time.Time.TimeFormat;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityConstants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.util.DateTimeFormatException;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.w3c.W3CConstants;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.EncodingContext;
import org.n52.svalbard.encode.XmlBeansEncodingFlags;
import org.n52.svalbard.encode.exception.EncodingException;

public abstract class AbstractGetDataAvailabilityStreamWriter extends XmlStreamWriter<List<DataAvailability>> {

    protected static final String TIME_PERIOD_PREFIX = "tp_";

    protected static final String DATA_AVAILABILITY_PREFIX = "dam_";

    protected static final String RESULT_TIME = "resultTime";

    protected static final String NAME = "name";

    protected final Map<TimePeriod, String> times;

    protected final String version;

    protected int dataAvailabilityCount = 1;

    protected int timePeriodCount = 1;

    protected int resultTimeCount = 1;

    public AbstractGetDataAvailabilityStreamWriter(OutputStream outputStream, EncodingContext context,
            EncoderRepository encoderRepository, Producer<XmlOptions> xmlOptions, List<DataAvailability> element,
            Map<TimePeriod, String> times, String version) throws XMLStreamException {
        super(outputStream, context, encoderRepository, xmlOptions, element);
        this.times = times;
        this.version = version;
    }

    @Override
    public void write() throws XMLStreamException, EncodingException {
        start();
        writeGetDataAvailabilityResponse();
        end();
        finish();
    }

    protected abstract void writeGetDataAvailabilityResponse() throws XMLStreamException, EncodingException;

    protected abstract void wirteDataAvailabilityMember(DataAvailability da)
            throws XMLStreamException, EncodingException;

    protected void writePhenomenonTime(DataAvailability da, QName element)
            throws DateTimeFormatException, XMLStreamException {
        start(element);
        if (times.containsKey(da.getPhenomenonTime())) {
            attr(GetDataAvailabilityConstants.XLINK_HREF, "#" + times.get(da.getPhenomenonTime()));
        } else {
            da.getPhenomenonTime().setGmlId(TIME_PERIOD_PREFIX + timePeriodCount++);
            times.put(da.getPhenomenonTime(), da.getPhenomenonTime().getGmlId());
            writeTimePeriod(da.getPhenomenonTime());
        }
        end(element);
    }

    protected void writeFeatureOfInterest(DataAvailability da, QName element) throws XMLStreamException {
        start(element);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getFeatureOfInterest().getHref());
        if (da.getFeatureOfInterest().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitleOrFromHref());
        }
        end(element);
    }

    protected void writeProcedure(DataAvailability da, QName element) throws XMLStreamException {
        start(element);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getProcedure().getHref());
        if (da.getProcedure().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitleOrFromHref());
        }
        end(element);
    }

    protected void writeObservedProperty(DataAvailability da, QName element) throws XMLStreamException {
        start(element);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getObservedProperty().getHref());
        if (da.getObservedProperty().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitleOrFromHref());
        }
        end(element);
    }

    protected void writeTimePeriod(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GmlConstants.QN_TIME_PERIOD_32);
        attr(GmlConstants.QN_ID_32, tp.getGmlId());
        writeBegin(tp);
        writeEnd(tp);
        end(GmlConstants.QN_TIME_PERIOD_32);
    }

    protected void writeBegin(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GmlConstants.QN_BEGIN_POSITION_32);
        if (tp.isSetStartIndeterminateValue()) {
            attr(GmlConstants.AN_INDETERMINATE_POSITION, tp.getStartIndet().getValue());
        }
        if (tp.isSetStart()) {
            writeTimeString(tp.getStart(), tp.getTimeFormat());
        }
        end(GmlConstants.QN_BEGIN_POSITION_32);
    }

    protected void writeEnd(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GmlConstants.QN_END_POSITION_32);
        if (tp.isSetEndIndeterminateValue()) {
            attr(GmlConstants.AN_INDETERMINATE_POSITION, tp.getEndIndet().getValue());
        }
        if (tp.isSetEnd()) {
            writeTimeString(tp.getEnd(), tp.getTimeFormat());
        }
        end(GmlConstants.QN_END_POSITION_32);
    }

    protected void writeTimeString(DateTime time, TimeFormat format)
            throws XMLStreamException, DateTimeFormatException {
        chars(DateTimeHelper.formatDateTime2String(time, format));
    }

    protected void writeCount(long count, QName element) throws XMLStreamException {
        start(element);
        chars(Long.toString(count));
        end(element);
    }

    protected void writeResultTimes(List<TimeInstant> resultTimes, QName element)
            throws XMLStreamException, EncodingException {
        start(element);
        start(SweConstants.QN_DATA_RECORD_SWE_200);
        attr("definition", RESULT_TIME);
        for (TimeInstant resultTime : resultTimes) {
            start(SweConstants.QN_FIELD_200);
            attr(NAME, RESULT_TIME + resultTimeCount++);
            writeTime(resultTime);
            end(SweConstants.QN_FIELD_200);
        }
        end(SweConstants.QN_DATA_RECORD_SWE_200);
        end(element);
    }

    protected void writeTime(TimeInstant ti) throws XMLStreamException, DateTimeFormatException {
        start(SweConstants.QN_TIME_SWE_200);
        writeValue(ti);
        writeUom();
        end(SweConstants.QN_TIME_SWE_200);
    }

    private void writeUom() throws XMLStreamException {
        start(SweConstants.QN_UOM_SWE_200);
        attr(W3CConstants.QN_XLINK_HREF, OmConstants.PHEN_UOM_ISO8601);
        end(SweConstants.QN_UOM_SWE_200);

    }

    protected void writeValue(TimeInstant ti) throws XMLStreamException, DateTimeFormatException {
        start(SweConstants.QN_VALUE_SWE_200);
        writeTimeString(ti.getValue(), ti.getTimeFormat());
        end(SweConstants.QN_VALUE_SWE_200);
    }

    protected void writeElementWithStringValue(String value, QName element) throws XMLStreamException {
        start(element);
        chars(value);
        end(element);
    }

    protected void writeMetadata(Map<String, NamedValue<?>> map, QName element)
            throws XMLStreamException, EncodingException {
        for (Entry<String, NamedValue<?>> entry : map.entrySet()) {
            Object o = getEncoder(OmConstants.NS_OM_2, entry.getValue()).encode(entry.getValue(),
                    EncodingContext.of(XmlBeansEncodingFlags.DOCUMENT));
            if (o != null && o instanceof XmlObject) {
                start(GetDataAvailabilityConstants.GDA_EXTENSION);
                attr(NAME, entry.getKey());
                rawText(((XmlObject) o) .xmlText(getXmlOptions()));
                end(GetDataAvailabilityConstants.GDA_EXTENSION);
            }
        }
    }
}