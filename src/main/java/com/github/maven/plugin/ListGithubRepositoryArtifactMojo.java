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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import com.github.maven.plugin.client.GithubClient;
import com.github.maven.plugin.client.exceptions.GithubException;
import com.github.maven.plugin.client.exceptions.GithubRepositoryNotFoundException;

/**
 * This goal allows to list the current available artifacts in the configured
 * github repository.
 *
 * @goal list
 * @threadSafe
 * @requiresOnline true
 *
 * @author Kevin Pollet
 */
public class ListGithubRepositoryArtifactMojo extends AbstractGithubMojo {
	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		final Log logger = getLog();
		final GithubClient githubClient = new GithubClient( getLogin(), getToken() );

		try {
			
			if (getAlternativeLogin() != null)
				githubClient.setAlternativeLogin(getAlternativeLogin());
			
			String repository = getRepository();
			
			final Set<String> artifacts = githubClient.listDownloads( repository );
			
			logger.info( "Checking downloads for " + githubClient.getDownloadsUrl(repository));

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
