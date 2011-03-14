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
package com.github.maven.plugin.client;

import java.io.File;
import java.util.Set;

/**
 * @author Kevin Pollet
 */
public interface GithubClient {
	/**
	 * Returns the available downloads list for the given repository.
	 *
	 * @param repositoryUrl The repository url.
	 *
	 * @return A Set containing the available downloads.
	 */
	Set<String> listDownloads(String repositoryUrl);

	/**
	 * Uploads the given file in the download section of the given
	 * repository url.
	 *
	 * @param file The file to upload.
	 * @param description The description of the file to upload.
	 * @param repositoryUrl The repository url.
	 */
	void upload(String fileName, File file, String description, String repositoryUrl);

	/**
	 * Replaces the given download by the given file in the given repository url.
	 *
	 * @param downloadName The name of the artifact to replace.
	 * @param file The file to upload.
	 * @param description The file description
	 * @param repositoryUrl The repository url.
	 */
	void replace(String downloadName, File file, String description, String repositoryUrl);
}
