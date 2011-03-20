/*
 * Copyright 2011 Kevin Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.maven.plugin.util;

import java.util.Set;

import org.apache.maven.plugin.logging.Log;

/**
 * An utility class which simplify the use
 * of the maven logger.
 *
 * @author Kevin Pollet
 */
public final class LoggerHelper {

	private final Log logger;

	/**
	 * Constructs an instance of logger helper.
	 *
	 * @param logger The logger.
	 */
	public LoggerHelper(Log logger) {
		this.logger = logger;
	}

	/**
	 * Displays the given list as a sequence
	 * of info message.
	 *
	 * @param lineFormat The line format.
	 * @param list The list to display.
	 *
	 * @see String#format(String, Object...)
	 */
	public void info(String lineFormat, Set<?> list) {
		for ( Object o : list ) {
			info( lineFormat, o );
		}
	}

	/**
	 * Displays the given info message.
	 *
	 * @param format The format string.
	 * @param args The format string arguments.
	 *
	 * @see String#format(String, Object...)
	 */
	public void info(String format, Object... args) {
		if ( logger.isInfoEnabled() ) {
			final String message = String.format( format, args );
			logger.info( message );
		}
	}
}
