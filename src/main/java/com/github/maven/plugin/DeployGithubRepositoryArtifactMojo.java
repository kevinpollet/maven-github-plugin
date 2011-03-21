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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.maven.plugin.client.GithubClient;
import com.github.maven.plugin.client.exceptions.GithubArtifactAlreadyExistException;
import com.github.maven.plugin.client.exceptions.GithubArtifactNotFoundException;
import com.github.maven.plugin.client.exceptions.GithubException;
import com.github.maven.plugin.client.exceptions.GithubRepositoryNotFoundException;
import com.github.maven.plugin.client.impl.GithubClientImpl;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Deploys artifacts to the download section of
 * the configured github repository.
 *
 * @author Kevin Pollet
 *
 * @goal deploy
 * @phase deploy
 * @threadSafe
 * @requiresOnline true
 * @since 1.0
 */
public final class DeployGithubRepositoryArtifactMojo extends AbstractGithubMojo {
	/**
	 * If true, skip the deployment of the project artifacts. To see which
	 * artifacts will be uploaded use the dryRun property.
	 *
	 * @parameter expression="${github.upload.skip}" default-value="false"
	 */
	private boolean skipUpload;

	/**
	 * Allows to see which artifacts will be deployed to the download
	 * section of the configured github repository (these artifacts will
	 * not be uploaded).
	 *
	 * @parameter expression="${dryRun}" default-value="false"
	 */
	private boolean dryRun;

	/**
	 * Sets default override behavior when uploading artifacts, used when artifact.override is not set. By default is false.
	 *
	 * @parameter expression="${github.overrideArtifacts}" default-value="false"
	 */
	private boolean overrideArtifacts;

	/**
	 * Allows to disable the upload of artifacts which match at least one
	 * pattern of the given list.
	 *
	 * @parameter
	 */
	private String[] excludes;

	/**
	 * Allows to configure the artifacts to upload.
	 *
	 * @parameter
	 */
	private Artifact[] artifacts;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if ( !skipUpload ) {
			try {

				//if no artifacts are configured, upload main artifact and attached artifacts
				if ( artifacts == null ) {
					final String projectDescription = mavenProject.getDescription();
					final Set<Artifact> githubArtifacts = new HashSet<Artifact>();

					final DirectoryScanner scanner = new DirectoryScanner();
					scanner.setExcludes( excludes );
					scanner.setBasedir( mavenProject.getBuild().getDirectory() );
					scanner.scan();

					final List<String> includedFiles = Arrays.asList( scanner.getIncludedFiles() );

					//add main artifact
					if ( includedFiles.contains( mavenProject.getArtifact().getFile().getName() ) ) {
						boolean shouldOverride = mavenProject.getArtifact().isSnapshot() || overrideArtifacts;
						githubArtifacts.add(
								new Artifact(
										mavenProject.getArtifact().getFile(),
										projectDescription,
										shouldOverride
								)
						);
					}

					//add attached artifacts
					for ( org.apache.maven.artifact.Artifact attachedArtifact : mavenProject.getAttachedArtifacts() ) {
						if ( includedFiles.contains( attachedArtifact.getFile().getName() ) ) {
							boolean shouldOverride = attachedArtifact.isSnapshot() || overrideArtifacts;
							githubArtifacts.add(
									new Artifact(
											attachedArtifact.getFile(),
											projectDescription,
											shouldOverride
									)
							);
						}
					}

					artifacts = githubArtifacts.toArray( new Artifact[0] );
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
	}

	/**
	 * Upload the given artifacts into the configured github repository.
	 *
	 * @param artifacts The artifacts to upload.
	 *
	 * @throws MojoExecutionException if artifacts are not properly configured.
	 */
	private void uploadArtifacts(Artifact... artifacts) throws MojoExecutionException {
		final GithubClient githubClient = new GithubClientImpl( login, token );

		for ( Artifact artifact : artifacts ) {
			String artifactName = artifact.getName();
			String artifactDescription = artifact.getDescription();
			File artifactFile = artifact.getFile();

			//verify that the artifact defined a valid file
			if ( artifactFile == null || !artifactFile.exists() || artifactFile.isDirectory() ) {
				throw new MojoExecutionException(
						"Missing <file> definition or the defined file doesn't exist or is a directory"
				);
			}

			loggerHelper.info( "Uploading %s to %s", artifact, repository );

			if ( !dryRun ) { //upload or replace the artifact
				if ( artifact.getOverride() ) {
					githubClient.replace( artifactName, artifactFile, artifactDescription, repository );
				}
				else {
					githubClient.upload( artifactName, artifactFile, artifactDescription, repository );
				}
			}
		}
	}
}

