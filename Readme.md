What is it ?
============

For each Github repository there is a download section which allows people to download project tags. Sometimes project has distribution artifacts which provide a functionnal application out of the box. This maven plugin allows you to deploy your project ditribution artifacts to the repository download section during the build process.

How to use it ?
---------------

### Configuration of Maven plugin repository

Actually, I don't have an official maven repository. To use this plugin you have to add the following lines in your `pom.xml` or `settings.xml` file.


	<pluginRepositories>
		<pluginRepository>
			<id>releases</id>
			<url>https://github.com/kevinpollet/maven-repository/raw/master/releases/</url>
			<layout>default</layout>
		</pluginRepository>
		<pluginRepository>
			<id>snpashots</id>
			<url>https://github.com/kevinpollet/maven-repository/raw/master/snapshots</url>
			<layout>default</layout>
		</pluginRepository>
	</pluginwepositories>


### Maven plugin available goals

#### maven-github-plugin:upload

* Default configuration

_By default this goal is bounded to the deploy phase_

* Specify artifacts to upload

_By default this goal is not bounded to a lifecycle phase_

    <plugin>
    	<groupId>com.github.maven.plugin</groupId>
    	<artifactId>maven-github-plugin</artifactId>
    	<version>1.0-SNAPSHOT</version>
    	<configuration>
    		<login>YOUR_GITHUB_LOGIN</login>
    		<token>YOUR_GITHUB_TOKEN</token>
    		<repository>YOUR_GITHUB_REPOSITORY</repository>
    		<artifacts>
    			<artifact>
    				<file>ABSOLUTE_ARTIFACT_PATH</file>
    				<description>ARTIFACT_DESCRIPTION</description>
    				<override>true|false</override> --> Default to false
    			</artifact>
    		<artifacts>
    	</configuration>
    </plugin>

#### maven-github-plugin:list

(_By default this goal is not bounded to a lifecycle phase_)

This aim of this goal is just to list the available github repository downloads. To use this goal you have to add the configuration below in your `pom.xml`.

	<plugin>
		<groupId>com.github.maven.plugin</groupId>
		<artifactId>maven-github-plugin</artifactId>
		<version>1.0-SNAPSHOT</version>
		<configuration>
			<login>YOUR_GITHUB_LOGIN</login>
			<token>YOUR_GITHUB_TOKEN</token>
			<repository>YOUR_GITHUB_REPOSITORY</repository>
		</configuration>
	</plugin>

FAQ
---
 
* How keep secret your github token ?

* How reporting a bug or request a new feature ?

To report an issue or request a new feature you just have to open an issue in the repository issue tracker. After that you can add a label to it. Actually there two types of label _Bug_ and _Feature_

Licence
-------

<pre>
Copyright 2011 Kevin Pollet

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License
</pre>



