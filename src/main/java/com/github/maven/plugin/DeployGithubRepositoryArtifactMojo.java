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
import java.io.FilenameFilter;

import com.github.maven.plugin.client.GithubClient;
import com.github.maven.plugin.client.exceptions.GithubDownloadAlreadyExistException;
import com.github.maven.plugin.client.exceptions.GithubDownloadNotFoundException;
import com.github.maven.plugin.client.exceptions.GithubException;
import com.github.maven.plugin.client.exceptions.GithubRepositoryNotFoundException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

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
	private Artifact[] artifacts;

	/**
	 * @parameter expression="${github.upload.overrideExistingArtifact}" default-value=false
	 */
	private boolean overrideExistingFile;

	public void execute() throws MojoExecutionException, MojoFailureException {

		try {

			//if no artifacts are configured deploy artifacts matching ${project.artifactId}-${project.version}
			if ( artifacts == null ) {
				File targetFolder = new File( getProject().getBasedir(), "target" );
				File[] targetFiles = targetFolder.listFiles( new DefaultArtifactsFilter() );
				artifacts = new Artifact[ targetFiles.length ];

				for ( int i = 0; i < targetFiles.length; i++ ) {
					Artifact artifact = new Artifact();
					artifact.setFile( targetFiles[i] );
					artifact.setDescription( getProject().getDescription() );
					artifact.setOverride( overrideExistingFile );

					artifacts[i] = artifact;
				}
			}

			uploadArtifacts( artifacts );

		}
		catch ( GithubRepositoryNotFoundException e ) {
			throw new MojoFailureException( e.getMessage(), e );
		}
		catch ( GithubDownloadNotFoundException e ) {
			throw new MojoFailureException( e.getMessage(), e );
		}
		catch ( GithubDownloadAlreadyExistException e ) {
			throw new MojoFailureException( e.getMessage(), e );
		}
		catch ( GithubException e ) {
			throw new MojoExecutionException( "Unexpected error", e );
		}

	}

	private void uploadArtifacts(Artifact... artifacts) {
		final GithubClient githubClient = new GithubClient( getLogin(), getToken() );

		getLog().info( "" );
		for ( Artifact artifact : artifacts ) {
			String artifactName = artifact.getFile().getName();
			String artifactDescription = artifact.getDescription();
			boolean override = artifact.getOverride();

			getLog().info( "Uploading [file=" + artifactName + ", description=" + artifactDescription + ", override=" + override + "]" );
			if ( artifact.getOverride() ) {
				githubClient.replace( artifactName, artifact.getFile(), artifactDescription, getRepository() );
			}
			else {
				githubClient.upload( artifact.getFile(),  artifactDescription, getRepository() );
			}
		}
		getLog().info( "" );

	}

	class DefaultArtifactsFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			final MavenProject project = getProject();
			final String nameSuffix = String.format( "%s-%s", project.getArtifactId(), project.getVersion() );

			return name != null && name.startsWith( nameSuffix );
		}

	}

}


