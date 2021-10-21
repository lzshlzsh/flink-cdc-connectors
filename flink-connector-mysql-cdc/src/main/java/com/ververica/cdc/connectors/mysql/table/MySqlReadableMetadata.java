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

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.data.TimestampData;
import org.apache.flink.table.types.DataType;

import com.ververica.cdc.debezium.table.MetadataConverter;
import io.debezium.connector.AbstractSourceInfo;
import io.debezium.data.Envelope;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

/** Defines the supported metadata columns for {@link MySqlTableSource}. */
public enum MySqlReadableMetadata {
    /** Name of the table that contain the row. . */
    TABLE_NAME(
            "table_name",
            DataTypes.STRING().notNull(),
            new MetadataConverter() {
                private static final long serialVersionUID = 1L;

                @Override
                public Object read(SourceRecord record) {
                    Struct messageStruct = (Struct) record.value();
                    Struct sourceStruct = messageStruct.getStruct(Envelope.FieldName.SOURCE);
                    return StringData.fromString(
                            sourceStruct.getString(AbstractSourceInfo.TABLE_NAME_KEY));
                }
            }),

    /** Name of the database that contain the row. */
    DATABASE_NAME(
            "database_name",
            DataTypes.STRING().notNull(),
            new MetadataConverter() {
                private static final long serialVersionUID = 1L;

                @Override
                public Object read(SourceRecord record) {
                    Struct messageStruct = (Struct) record.value();
                    Struct sourceStruct = messageStruct.getStruct(Envelope.FieldName.SOURCE);
                    return StringData.fromString(
                            sourceStruct.getString(AbstractSourceInfo.DATABASE_NAME_KEY));
                }
            }),

    /**
     * It indicates the time that the change was made in the database. If the record is read from
     * snapshot of the table instead of the binlog, the value is always 0.
     */
    OP_TS(
            "op_ts",
            DataTypes.TIMESTAMP_WITH_LOCAL_TIME_ZONE(3).notNull(),
            new MetadataConverter() {
                private static final long serialVersionUID = 1L;

                @Override
                public Object read(SourceRecord record) {
                    Struct messageStruct = (Struct) record.value();
                    Struct sourceStruct = messageStruct.getStruct(Envelope.FieldName.SOURCE);
                    return TimestampData.fromEpochMillis(
                            (Long) sourceStruct.get(AbstractSourceInfo.TIMESTAMP_KEY));
                }
            }),

    /** Operation type, INSERT/UPDATE/DELETE. */
    OP_TYPE(
            "op_type",
            DataTypes.STRING().notNull(),
            new MetadataConverter() {
                private static final long serialVersionUID = 1L;

                @Override
                public Object read(SourceRecord record) {
                    final Envelope.Operation op = Envelope.operationFor(record);
                    if (op == Envelope.Operation.CREATE || op == Envelope.Operation.READ) {
                        return StringData.fromString("INSERT");
                    } else if (op == Envelope.Operation.DELETE) {
                        return StringData.fromString("DELETE");
                    } else {
                        return StringData.fromString("UPDATE");
                    }
                }
            }),

    /** Not important, a simple increment counter. */
    BATCH_ID(
            "batch_id",
            DataTypes.BIGINT().nullable(),
            new MetadataConverter() {
                private static final long serialVersionUID = 1L;

                private long id = 0;

                @Override
                public Object read(SourceRecord record) {
                    return id++;
                }
            }),

    /** Source does not emit ddl data. */
    IS_DDL(
            "is_ddl",
            DataTypes.BOOLEAN().notNull(),
            new MetadataConverter() {
                private static final long serialVersionUID = 1L;

                @Override
                public Object read(SourceRecord record) {
                    return false;
                }
            }),

    /** The update-before data for UPDATE record. */
    OLD(
            "update_before",
            DataTypes.ARRAY(
                            DataTypes.MAP(
                                            DataTypes.STRING().nullable(),
                                            DataTypes.STRING().nullable())
                                    .nullable())
                    .nullable(),
            new MetadataConverter() {
                private static final long serialVersionUID = 1L;

                @Override
                public Object read(SourceRecord record) {
                    final Envelope.Operation op = Envelope.operationFor(record);
                    if (op != Envelope.Operation.UPDATE) {
                        return null;
                    }
                    return record;
                }
            });

    private final String key;

    private final DataType dataType;

    private final MetadataConverter converter;

    MySqlReadableMetadata(String key, DataType dataType, MetadataConverter converter) {
        this.key = key;
        this.dataType = dataType;
        this.converter = converter;
    }

    public String getKey() {
        return key;
    }

    public DataType getDataType() {
        return dataType;
    }

    public MetadataConverter getConverter() {
        return converter;
    }
}
