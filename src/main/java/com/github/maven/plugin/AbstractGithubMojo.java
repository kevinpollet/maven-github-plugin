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
 * The Abstract Github Mojo class.
 *
 * @author Kevin Pollet
 */
public abstract class AbstractGithubMojo extends AbstractMojo {
	/**
	 * @parameter default-value="${project}"
	 * @readonly
	 * @required
	 */
	private MavenProject project;

	/**
	 * @parameter default-value="${settings.proxies}"
	 * @readonly
	 * @required
	 */
	private List<Proxy> proxies;

	/**
	 * @parameter expression="${github.login}"
	 * @required
	 */
	private String login;

	/**
	 * @parameter expression="${github.token}"
	 * @required
	 */
	private String token;

	/**
	 * @parameter expression="${github.repository}"
	 * @required
	 */
	private String repository;

	public String getLogin() {
		return login;
	}

	public String getToken() {
		return token;
	}

	public String getRepository() {
		return repository;
	}

	public MavenProject getProject() {
		return project;
	}

	/**
	 * Get the first active HTTPS proxy configured in
	 * maven <em>settings.xml</em> file.
	 *
	 * @return The first active HTTPS proxy or {@code null}
	 */
	public Proxy getActiveHttpsProxy() {
		for ( Proxy proxy : proxies ) {
			if ( proxy.isActive() && "https".equalsIgnoreCase( proxy.getProtocol() ) ) {
				return proxy;
			}
		}

		return null;
	}
}
