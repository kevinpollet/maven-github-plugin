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

import java.util.Set;

import com.github.maven.plugin.client.GithubClient;
import com.github.maven.plugin.client.exceptions.GithubException;
import com.github.maven.plugin.client.exceptions.GithubRepositoryNotFoundException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal list
 * @threadSafe
 */
//TODO add support of proxy
public class ListGithubRepositoryDownloadMojo extends AbstractGithubMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		final GithubClient githubClient = new GithubClient( getLogin(), getToken() );

		try {

			final Set<String> downloads = githubClient.listDownloads( getRepository() );
			getLog().info("");
			getLog().info( "Available downloads for [" + getRepository() + "]" );
			getLog().info("");
			for ( String download : downloads ) {
				getLog().info( "* " + download );
			}
			getLog().info("");

		}
		catch ( GithubRepositoryNotFoundException e ) {
			throw new MojoFailureException( e.getMessage(), e );
		}
		catch ( GithubException e ) {
			throw new MojoExecutionException( e.getMessage(), e );
		}

	}

}
