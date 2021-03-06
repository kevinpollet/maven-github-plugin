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
package com.github.maven.plugin.client.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.maven.plugin.client.GithubClient;
import com.github.maven.plugin.client.exceptions.GithubArtifactAlreadyExistException;
import com.github.maven.plugin.client.exceptions.GithubArtifactNotFoundException;
import com.github.maven.plugin.client.exceptions.GithubException;
import com.github.maven.plugin.client.exceptions.GithubRepositoryNotFoundException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import static com.github.maven.plugin.util.Contract.assertNotNull;
import static com.github.maven.plugin.util.Contract.assertStartsWith;

/**
 * @author Kevin Pollet
 */
public class GithubClientImpl implements GithubClient {

	private final static String GITHUB_REPOSITORY_URL_PREFIX = "https://github.com";

	private final static String GITHUB_S3_URL = "https://github.s3.amazonaws.com/";

	private final HttpClient httpClient;

	private final String login;

	private final String token;

	/**
	 * Constructs a client
	 */
	public GithubClientImpl() {
		this( null, null );
	}

	/**
	 * Constructs a github client for the given
	 * login and token.
	 *
	 * @param login The login.
	 * @param token The github token associated to the given login.
	 */
	public GithubClientImpl(String login, String token) {
		this.login = login;
		this.token = token;
		this.httpClient = new HttpClient();
	}

	public Set<String> listAvailableDownloads(String repositoryUrl) {
		assertStartsWith( repositoryUrl, GITHUB_REPOSITORY_URL_PREFIX, "repositoryUrl" );

		final Set<String> downloads = new HashSet<String>();
		final GetMethod githubGet = new GetMethod( toRepositoryDownloadUrl( repositoryUrl ) );

		int response;

		try {

			response = httpClient.executeMethod( githubGet );
		}
		catch ( IOException e ) {
			throw new GithubRepositoryNotFoundException(
					"Cannot retrieve github repository " + repositoryUrl + " informations", e
			);
		}

		if ( response == HttpStatus.SC_OK ) {

			String githubResponse;

			try {

				githubResponse = githubGet.getResponseBodyAsString();
			}
			catch ( IOException e ) {
				throw new GithubRepositoryNotFoundException(
						"Cannot retrieve github repository " + repositoryUrl + "  informations", e
				);
			}

			Pattern pattern = Pattern.compile(
					String.format(
							"<a href=\"/downloads%s/?([^\"]+)\"", removeGithubUrlPart( repositoryUrl )
					)
			);

			Matcher matcher = pattern.matcher( githubResponse );
			while ( matcher.find() ) {
				downloads.add( matcher.group( 1 ) );
			}

		}
		else if ( response == HttpStatus.SC_NOT_FOUND ) {
			throw new GithubRepositoryNotFoundException( "Cannot found repository " + repositoryUrl );
		}
		else {
			throw new GithubRepositoryNotFoundException( "Cannot retrieve github repository " + repositoryUrl + " informations" );
		}

		githubGet.releaseConnection();

		return downloads;
	}

	public void upload(String artifactName, File file, String description, String repositoryUrl) {
		assertNotNull( artifactName, "artifactName" );
		assertNotNull( file, "file" );
		assertNotNull( repositoryUrl, "repositoryUrl" );
		assertStartsWith( repositoryUrl, GITHUB_REPOSITORY_URL_PREFIX, "repositoryUrl" );

		PostMethod githubPost = new PostMethod( toRepositoryDownloadUrl( repositoryUrl ) );
		githubPost.setRequestBody(
				new NameValuePair[] {
						new NameValuePair( "login", login ),
						new NameValuePair( "token", token ),
						new NameValuePair( "file_name", artifactName ),
						new NameValuePair( "file_size", String.valueOf( file.length() ) ),
						new NameValuePair( "description", description == null ? "" : description )
				}
		);

		try {

			int response = httpClient.executeMethod( githubPost );

			if ( response == HttpStatus.SC_OK ) {

				ObjectMapper mapper = new ObjectMapper();
				JsonNode node = mapper.readValue( githubPost.getResponseBodyAsString(), JsonNode.class );

				PostMethod s3Post = new PostMethod( GITHUB_S3_URL );

				Part[] parts = {
						new StringPart( "Filename", artifactName ),
						new StringPart( "policy", node.path( "policy" ).getTextValue() ),
						new StringPart( "success_action_status", "201" ),
						new StringPart( "key", node.path( "path" ).getTextValue() ),
						new StringPart( "AWSAccessKeyId", node.path( "accesskeyid" ).getTextValue() ),
						new StringPart( "Content-Type", node.path( "mime_type" ).getTextValue() ),
						new StringPart( "signature", node.path( "signature" ).getTextValue() ),
						new StringPart( "acl", node.path( "acl" ).getTextValue() ),
						new FilePart( "file", file )
				};

				MultipartRequestEntity partEntity = new MultipartRequestEntity( parts, s3Post.getParams() );
				s3Post.setRequestEntity( partEntity );

				int s3Response = httpClient.executeMethod( s3Post );
				if ( s3Response != HttpStatus.SC_CREATED ) {
					throw new GithubException( "Cannot upload " + file.getName() + " to repository " + repositoryUrl );
				}

				s3Post.releaseConnection();
			}
			else if ( response == HttpStatus.SC_NOT_FOUND ) {
				throw new GithubRepositoryNotFoundException( "Cannot found repository " + repositoryUrl );
			}
			else if ( response == HttpStatus.SC_UNPROCESSABLE_ENTITY ) {
				throw new GithubArtifactAlreadyExistException( "File " + artifactName + " already exist in " + repositoryUrl + " repository" );
			}
			else {
				throw new GithubException( "Error " + HttpStatus.getStatusText( response ) );
			}
		}
		catch ( IOException e ) {
			throw new GithubException( e );
		}

		githubPost.releaseConnection();
	}

	public void replace(String artifactName, File file, String description, String repositoryUrl) {
		assertNotNull( artifactName, "artifactName" );
		assertNotNull( file, "file" );
		assertNotNull( repositoryUrl, "repositoryUrl" );
		assertStartsWith( repositoryUrl, GITHUB_REPOSITORY_URL_PREFIX, "repositoryUrl" );

		try {
			delete( repositoryUrl, artifactName );
		}
		catch ( GithubArtifactNotFoundException ex ) {
			// replace should not fail if file was not found on server.
		}
		upload( artifactName, file, description, repositoryUrl );
	}

	/**
	 * Removes the given download from the repository download section.
	 *
	 * @param repositoryUrl The repository url.
	 * @param artifactName The download name.
	 */
	private void delete(String repositoryUrl, String artifactName) {
		final Map<String, Integer> downloads = retrieveDownloadsInfos( repositoryUrl );

		if ( !downloads.containsKey( artifactName ) ) {
			throw new GithubArtifactNotFoundException( "The download " + artifactName + " cannot be found in " + repositoryUrl );
		}

		final String downloadUrl = String.format(
				"%s/%d", toRepositoryDownloadUrl( repositoryUrl ), downloads.get( artifactName )
		);

		PostMethod githubDelete = new PostMethod( downloadUrl );
		githubDelete.setRequestBody(
				new NameValuePair[] {
						new NameValuePair( "_method", "delete" ),
						new NameValuePair( "login", login ),
						new NameValuePair( "token", token )
				}
		);

		try {
			int response = httpClient.executeMethod( githubDelete );
			if ( response != HttpStatus.SC_MOVED_TEMPORARILY ) {
				throw new GithubException( "Unexpected error " + HttpStatus.getStatusText( response ) );
			}
		}
		catch ( IOException e ) {
			throw new GithubException( e );
		}

		githubDelete.releaseConnection();
	}

	/**
	 * Retrieves the download informations associated to the given repository
	 * url.
	 *
	 * @param repositoryUrl The repository url.
	 *
	 * @return A map containing the downloads informations.
	 */
	private Map<String, Integer> retrieveDownloadsInfos(String repositoryUrl) {
		final Map<String, Integer> downloads = new HashMap<String, Integer>();

		GetMethod githubGet = new GetMethod( toRepositoryDownloadUrl( repositoryUrl ) );
		githubGet.setQueryString(
				new NameValuePair[] {
						new NameValuePair( "login", login ),
						new NameValuePair( "token", token )
				}
		);

		int response;

		try {

			response = httpClient.executeMethod( githubGet );
		}
		catch ( IOException e ) {
			throw new GithubRepositoryNotFoundException(
					"Cannot retrieve github repository " + repositoryUrl + " informations", e
			);
		}

		if ( response == HttpStatus.SC_OK ) {

			String githubResponse;

			try {

				githubResponse = githubGet.getResponseBodyAsString();
			}
			catch ( IOException e ) {
				throw new GithubRepositoryNotFoundException(
						"Cannot retrieve github repository " + repositoryUrl + "  informations", e
				);
			}

			Pattern pattern = Pattern.compile(
					String.format(
							"<a href=\"(/downloads)?%s/?([^\"]+)\"", removeGithubUrlPart( repositoryUrl )
					)
			);

			Matcher matcher = pattern.matcher( githubResponse );
			while ( matcher.find() ) {
				String tmp = matcher.group( 2 );

				if ( tmp.contains( "downloads" ) ) {
					String id = matcher.group( 2 ).substring( tmp.lastIndexOf( '/' ) + 1, tmp.length() );
					Integer downloadId = Integer.parseInt( id );
					if ( matcher.find() ) {
						downloads.put( matcher.group( 2 ), downloadId );
					}
				}
			}

		}
		else if ( response == HttpStatus.SC_NOT_FOUND ) {
			throw new GithubRepositoryNotFoundException( "Cannot found repository " + repositoryUrl );
		}
		else {
			throw new GithubRepositoryNotFoundException( "Cannot retrieve github repository " + repositoryUrl + " informations" );
		}

		githubGet.releaseConnection();

		return downloads;
	}

	/**
	 * Returns the repositoryUrl download url for the given repositoryUrl
	 * url.
	 *
	 * @param repositoryUrl The repository url.
	 *
	 * @return The download url for the given repositoryUrl url.
	 *
	 * @throws NullPointerException If the parameter repositoryUrl is {@code null}.
	 */
	private String toRepositoryDownloadUrl(String repositoryUrl) {
		return repositoryUrl.concat( repositoryUrl.endsWith( "/" ) ? "downloads" : "/downloads" );
	}

	/**
	 * Removes the github url part from the given repository url.
	 *
	 * @param repositoryUrl The repository url.
	 *
	 * @return The repoditory url without the github part or {@code null} if it's not a valid github repository url.
	 */
	private String removeGithubUrlPart(String repositoryUrl) {
		Pattern pattern = Pattern.compile( Pattern.quote( GITHUB_REPOSITORY_URL_PREFIX ) + "(.*)?" );
		Matcher matcher = pattern.matcher( repositoryUrl );
		if ( matcher.find() ) {
			return matcher.group( 1 );
		}
		return null;
	}
}
