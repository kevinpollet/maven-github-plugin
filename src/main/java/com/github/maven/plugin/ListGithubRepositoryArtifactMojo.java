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
import com.github.maven.plugin.client.impl.GithubClientImpl;
import com.github.maven.plugin.client.exceptions.GithubException;
import com.github.maven.plugin.client.exceptions.GithubRepositoryNotFoundException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * Allows to list the current available artifacts in the download
 * section of the configured github repository.
 *
 * @author Kevin Pollet
 *
 * @goal list
 * @threadSafe
 * @requiresDirectInvocation true
 * @requiresOnline true
 * @since 1.0
 */
public final class ListGithubRepositoryArtifactMojo extends AbstractGithubMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		final Log logger = getLog();
		final GithubClient githubClient = new GithubClientImpl( login, token );

		try {

			final Set<String> artifacts = githubClient.listAvailableDownloads( repository );

			logger.info( "" );
			if ( artifacts.isEmpty() ) {
				logger.info( "No available downloads for [" + repository + "]" );
			}
			else {
				logger.info( "Available downloads for [" + repository + "]" );
				logger.info( "" );
				for ( String download : artifacts ) {
					getLog().info( "* " + download );
				}
			}
			logger.info( "" );

		}
		catch ( GithubRepositoryNotFoundException e ) {
			throw new MojoFailureException( e.getMessage(), e );
		}
		catch ( GithubException e ) {
			throw new MojoExecutionException( e.getMessage(), e );
		}
	}
}
