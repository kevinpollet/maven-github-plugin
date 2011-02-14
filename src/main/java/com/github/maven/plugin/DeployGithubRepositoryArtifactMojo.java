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

import com.github.maven.plugin.client.GithubClient;
import com.github.maven.plugin.client.exceptions.GithubArtifactAlreadyExistException;
import com.github.maven.plugin.client.exceptions.GithubArtifactNotFoundException;
import com.github.maven.plugin.client.exceptions.GithubException;
import com.github.maven.plugin.client.exceptions.GithubRepositoryNotFoundException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Push distribution artifact into the download area of the configured Github project.
 *
 * @goal upload
 * @phase deploy
 * @threadSafe
 */
//TODO add Proxy support
public class DeployGithubRepositoryArtifactMojo extends AbstractGithubMojo {

	/**
	 * @parameter
	 */
	private String[] excludes;

	/**
	 * @parameter
	 */
	private Artifact[] artifacts;

	/**
	 * @parameter expression="${github.upload.overrideExistingArtifact}" default-value=false
	 */
	private boolean overrideExistingFile;

	public void execute() throws MojoExecutionException, MojoFailureException {
		MavenProject project = getProject();

		try {

			//if no artifacts are configured deploy artifacts matching ${project.artifactId}-${project.version}*
			if ( artifacts == null ) {
				String outputDir = project.getBuild().getDirectory();
				DirectoryScanner scanner = new DirectoryScanner();
				scanner.setBasedir( outputDir );
				scanner.setIncludes(
						new String[] {
								project.getArtifactId() + "-" + project.getVersion() + "*"
						}
				);
				scanner.setExcludes( excludes );
				scanner.scan();

				String[] files = scanner.getIncludedFiles();
				if ( files.length == 0 ) {
					throw new MojoFailureException( "No artifacts to upload into repository " + getRepository() );
				}
				else {
					artifacts = new Artifact[files.length];
					for ( int i = 0; i < files.length; i++ ) {
						Artifact artifact = new Artifact();
						artifact.setFile( new File( outputDir, files[i] ) );
						artifact.setDescription( project.getDescription() );
						artifact.setOverride( overrideExistingFile );
						artifacts[i] = artifact;
					}
				}
			}

			//upload artifacts to configured github repository
			uploadArtifacts( artifacts );

		}
		catch ( GithubRepositoryNotFoundException e ) {
			throw new MojoFailureException( e.getMessage(), e );
		}
		catch ( GithubArtifactNotFoundException e ) {
			throw new MojoFailureException( e.getMessage(), e );
		}
		catch ( GithubArtifactAlreadyExistException e ) {
			throw new MojoFailureException( e.getMessage(), e );
		}
		catch ( GithubException e ) {
			throw new MojoExecutionException( "Unexpected error", e );
		}

	}

	private void uploadArtifacts(Artifact... artifacts) throws MojoExecutionException {
		final Log log = getLog();
		final GithubClient githubClient = new GithubClient( getLogin(), getToken() );

		log.info( "" );
		for ( Artifact artifact : artifacts ) {
			if ( artifact.getFile() == null ) {
				throw new MojoExecutionException( "Missing <file> into artifact configuration " );
			}
			log.info( "Uploading " + artifact );
			if ( artifact.getOverride() ) {
				githubClient.replace(
						artifact.getFile().getName(), artifact.getFile(), artifact.getDescription(), getRepository()
				);
			}
			else {
				githubClient.upload( artifact.getFile(), artifact.getDescription(), getRepository() );
			}
		}
		log.info( "" );
	}

}

