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

import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Proxy;

/**
 * The Abstract Github Mojo.
 *
 * @author Kevin Pollet
 */
public abstract class AbstractGithubMojo extends AbstractMojo {
	/**
	 * @parameter default-value="${project}"
	 * @readonly
	 * @required
	 */
	protected MavenProject mavenProject;

	/**
	 * The github login (must have write access to the github repository).
	 *
	 * @parameter expression="${github.login}"
	 * @required
	 */
	protected String login;

	/**
	 * The github token corresponding to the given login.
	 *
	 * @parameter expression="${github.token}"
	 * @required
	 */
	protected String token;

	/**
	 * The repository url (eg: https://github.com/kevinpollet/maven-github-plugin).
	 *
	 * @parameter expression="${github.repository}"
	 * @required
	 */
	protected String repository;
}
