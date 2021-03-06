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
package com.github.maven.plugin;

import java.io.File;

/**
 * The artifact definition.
 *
 * @author Kevin Pollet
 */
public class Artifact {
	/**
	 * The artifact file.
	 */
	private File file;

	/**
	 * The artifact description.
	 */
	private String description;

	/**
	 * Whether or not this artifact has to be overridden
	 * if it already exists in the repository download section.
	 */
	private boolean override;

	/**
	 * Lets you modify the Github name of the uploaded file,
	 * by default {@code File#getName()} will be used.
	 */
	private String finalName;

	/**
	 * Default constructor. Only used by maven
	 * for instantiation to allow field injection.
	 */
	public Artifact() {
		this( null, null, false );
	}

	/**
	 * Constructs an artifact with the given
	 * parameters. The name used in the download
	 * section is the file name.
	 *
	 * @param file The file to upload.
	 * @param description The uploaded file description.
	 * @param override Whether or not this artifact can be overridden.
	 */
	public Artifact(File file, String description, boolean override) {
		this( file, description, override, null );
	}

	/**
	 * Constructs an artifact with the given
	 * parameters.
	 *
	 * @param file The file to upload.
	 * @param description The uploaded file description.
	 * @param override Whether or not this artifact can be overridden.
	 * @param finalName The name used in the download section. If {@code null} the file name is used.
	 */
	public Artifact(File file, String description, boolean override, String finalName) {
		this.file = file;
		this.description = description;
		this.override = override;
		this.finalName = finalName;
	}

	public String getName() {
		if ( finalName == null || finalName.length() == 0 ) {
			return file.getName();
		}
		return finalName;
	}

	public File getFile() {
		return file;
	}

	public String getDescription() {
		return description;
	}

	public boolean getOverride() {
		return override;
	}

	@Override
	public String toString() {
		return "[artifactName=" + getName() +
				", description=" + description +
				", override=" + override +
				"]";
	}
}
