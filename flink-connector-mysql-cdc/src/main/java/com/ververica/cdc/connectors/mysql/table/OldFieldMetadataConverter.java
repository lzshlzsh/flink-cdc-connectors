/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ververica.cdc.connectors.mysql.table;

import org.apache.flink.table.data.GenericArrayData;
import org.apache.flink.table.data.GenericMapData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.types.logical.RowType;

import com.ververica.cdc.debezium.table.MetadataConverter;
import io.debezium.data.Envelope;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.HashMap;
import java.util.Map;

/** A {@link MetadataConverter} for {@link MySqlReadableMetadata#OLD}. */
public class OldFieldMetadataConverter implements MetadataConverter {
    private static final long serialVersionUID = 1L;

    private final RowType rowType;
    private final MetadataConverter converter;

    public OldFieldMetadataConverter(RowType rowType) {
        this.rowType = rowType;
        this.converter = MySqlReadableMetadata.OLD.getConverter();
    }

    @Override
    public Object read(SourceRecord record) {
        Object obj = converter.read(record);
        if (obj == null) {
            return null;
        }
        Struct value = (Struct) record.value();
        Schema valueSchema = record.valueSchema();
        Schema beforeSchema = valueSchema.field(Envelope.FieldName.BEFORE).schema();
        Struct before = value.getStruct(Envelope.FieldName.BEFORE);

        Map<StringData, StringData> oldData = new HashMap<>();
        rowType.getFieldNames()
                .forEach(
                        fieldName -> {
                            Field field = beforeSchema.field(fieldName);
                            if (field == null) {
                                oldData.put(StringData.fromString(fieldName), null);
                            } else {
                                // TODO: fieldValue maybe struct type, toString() do right thing ?
                                Object fieldValue = before.get(field);
                                oldData.put(
                                        StringData.fromString(fieldName),
                                        StringData.fromString(fieldValue.toString()));
                            }
                        });
        return new GenericArrayData(new Object[] {new GenericMapData(oldData)});
    }
}
