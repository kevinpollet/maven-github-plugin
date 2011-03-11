package com.github.maven.plugin.client;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsEqual;
import org.junit.Test;

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
public class GithubClientTest {

	@Test
	public void shouldLetSpecifyRepositoryFromUserDifferentThanLogin() {
		
		GithubClient githubClient = new GithubClient("LOGIN", "TOKEN");
		
		String downloadsUrl = githubClient.getDownloadsUrl("REPOSITORY");
		
		assertThat(downloadsUrl, IsEqual.equalTo("https://github.com/LOGIN/REPOSITORY/downloads"));
		
		githubClient.setAlternativeLogin("OTHERLOGIN");
		
		downloadsUrl = githubClient.getDownloadsUrl("REPOSITORY");
		
		assertThat(downloadsUrl, IsEqual.equalTo("https://github.com/OTHERLOGIN/REPOSITORY/downloads"));
		
	}
	
	@Test
	public void shouldReturnLoginAsRepositoryLoginIfAlternativeNotSet() {
		GithubClient githubClient = new GithubClient("LOGIN", "TOKEN");
		assertThat(githubClient.getRepositoryLogin(), IsEqual.equalTo("LOGIN"));
	}
	
	@Test
	public void shouldReturnAlternativeLoginAsRepositoryLoginIfAlternativeSet() {
		GithubClient githubClient = new GithubClient("LOGIN", "TOKEN");
		githubClient.setAlternativeLogin("ALTERNATIVELOGIN");
		assertThat(githubClient.getRepositoryLogin(), IsEqual.equalTo("ALTERNATIVELOGIN"));
	}
	
	// another option could be to have the repository as an absolute url "https://github.com/..." directly
}
