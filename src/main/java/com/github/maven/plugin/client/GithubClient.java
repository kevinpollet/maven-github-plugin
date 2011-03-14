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
	Set<String> listDownloads(String repositoryUrl);

	void upload(String fileName, File file, String description, String repositoryUrl);

	void replace(String downloadName, File file, String description, String repositoryUrl);
}
