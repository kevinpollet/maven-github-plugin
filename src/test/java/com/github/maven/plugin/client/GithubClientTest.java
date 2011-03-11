package com.github.maven.plugin.client;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsEqual;
import org.junit.Test;

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
}
