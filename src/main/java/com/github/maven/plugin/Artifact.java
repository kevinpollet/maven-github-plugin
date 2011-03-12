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
	 * Whether or not this artifact has to be overridden if it already exists in the repository download section.
	 */
	private boolean override;

	/**
	 * If you want to upload the file with another name.
	 */
	private String alternativeFileName;

	public File getFile() {
		return file;
	}

	public String getDescription() {
		return description;
	}

	public boolean getOverride() {
		return override;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public String getAlternativeFileName() {
		return alternativeFileName;
	}

	public void setAlternativeFileName(String alternativeFileName) {
		this.alternativeFileName = alternativeFileName;
	}

	@Override
	public String toString() {
		return "[file=" + file.getName() + ", description=" + description + ", override=" + override + ", alternativeFileName=" + alternativeFileName + "]";
	}
}
