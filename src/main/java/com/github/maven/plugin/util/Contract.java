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

/**
 * An utility class to check that a method contract is fulfilled.
 *
 * @author Kevin Pollet
 */
public final class Contract {

	private Contract() {

	}

	/**
	 * Assert that the given method parameter value is not {@code null}.
	 *
	 * @param param The value instance to check.
	 * @param paramName The name of the parameter.
	 *
	 * @throws IllegalArgumentException if the given parameter value is {@code null}.
	 */
	public static void assertNotNull(Object param, String paramName) {
		if ( param == null ) {
			throw new IllegalArgumentException( "Parameter " + paramName + " cannot be null." );
		}
	}

}
