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

package com.alibaba.ververica.cdc.connectors.mysql.options;

import javax.annotation.Nullable;

import java.util.Objects;

/**
 * Options class for MySQL offset.
 */
public class MySQLOffsetOptions {
	@Nullable
	private final String sourceOffsetFile;
	@Nullable
	private final Integer sourceOffsetPosition;

	public MySQLOffsetOptions(@Nullable String sourceOffsetFile, @Nullable Integer sourceOffsetPosition) {
		this.sourceOffsetFile = sourceOffsetFile;
		this.sourceOffsetPosition = sourceOffsetPosition;
	}

	private MySQLOffsetOptions(Builder builder) {
		sourceOffsetFile = builder.sourceOffsetFile;
		sourceOffsetPosition = builder.sourceOffsetPosition;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Nullable
	public String getSourceOffsetFile() {
		return sourceOffsetFile;
	}

	@Nullable
	public Integer getSourceOffsetPosition() {
		return sourceOffsetPosition;
	}

	@Override
	public String toString() {
		return "MySQLOffsetOptions{" +
				"sourceOffsetFile='" + sourceOffsetFile + '\'' +
				", sourceOffsetPosition=" + sourceOffsetPosition +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MySQLOffsetOptions that = (MySQLOffsetOptions) o;
		return Objects.equals(getSourceOffsetFile(), that.getSourceOffsetFile()) &&
				Objects.equals(getSourceOffsetPosition(), that.getSourceOffsetPosition());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getSourceOffsetFile(), getSourceOffsetPosition());
	}


	/**
	 * {@code MySQLOffsetOptions} builder static inner class.
	 */
	public static final class Builder {
		private String sourceOffsetFile;
		private Integer sourceOffsetPosition;

		private Builder() {
		}

		/**
		 * Sets the {@code sourceOffsetFile} and returns a reference to this Builder so that the methods can be chained together.
		 *
		 * @param val the {@code sourceOffsetFile} to set
		 * @return a reference to this Builder
		 */
		public Builder sourceOffsetFile(String val) {
			sourceOffsetFile = val;
			return this;
		}

		/**
		 * Sets the {@code sourceOffsetPosition} and returns a reference to this Builder so that the methods can be chained together.
		 *
		 * @param val the {@code sourceOffsetPosition} to set
		 * @return a reference to this Builder
		 */
		public Builder sourceOffsetPosition(Integer val) {
			sourceOffsetPosition = val;
			return this;
		}

		/**
		 * Returns a {@code MySQLOffsetOptions} built from the parameters previously set.
		 *
		 * @return a {@code MySQLOffsetOptions} built with parameters of this {@code MySQLOffsetOptions.Builder}
		 */
		public MySQLOffsetOptions build() {
			return new MySQLOffsetOptions(sourceOffsetFile, sourceOffsetPosition);
		}
	}
}
