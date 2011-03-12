Maven Github Plugin
===================
*Current version: 1.0-SNAPSHOT*

__This Maven plugin is under development, don't use it inside production project (the project API can change during it's development).__

What is it ?
------------

For each Github repository there is a download section which allows people to download project tags. Sometimes project has distribution assemblies which provide a functionnal application out of the box. This maven plugin allows you to deploy your project ditribution assemblies to the repository download section during the build process.

How to use it ?
---------------

### Configure plugin repository

Currently, I don't have an official Maven repository. To use this plugin you have to configure my github Maven repository. To do that, look at my [maven-repository](https://github.com/kevinpollet/maven-repository) project.
	
### Add it in your build configuration

To add the _maven-github-plugin_ in your project just add the following lines

	<build>
		<plugins>
   	 	   <plugin>
   	 	       <groupId>com.github.maven.plugin</groupId>
   	 	       <artifactId>maven-github-plugin</artifactId>
   	 	       <version>PLUGIN_VERSION</version>
   	 	       //... Plugin configuration
   	 	   </plugin>
		</plugins>
	</build>

After that you have to configure the goals of the maven plugin. If you're not familiar with this step, see [Maven documentation](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).

### maven-github-plugin available goals

#### maven-github-plugin:upload (bound to deploy phase)

- Default configuration

Without any artifact configuration (see _Specify artifacts to upload_) the plugin uploads all artifacts in the build directory (by default `target`) who match the following expression `${project.artifactId}-${project.version}*.(jar|zip|tar.gz|tar.bz2)`. If you want to exclude files just add the following configuration in your pom.

	<configuration>
		<login>YOUR_GITHUB_LOGIN</login>
		<token>YOUR_GITHUB_TOKEN</token>
		<repository>YOUR_GITHUB_REPOSITORY_URL</repository> (ex: https://github.com/kevinpollet/maven-github-plugin)
		<excludes>
			<exclude>*.zip</exclude>
		</excludes>
	</configuration>
	
- Specify artifacts to upload

To specify artifacts which have to be uploaded to the repository download section just add the following lines.

	<configuration>
		<login>YOUR_GITHUB_LOGIN</login>
		<token>YOUR_GITHUB_TOKEN</token>
		<repository>YOUR_GITHUB_REPOSITORY_URL</repository> (ex: https://github.com/kevinpollet/maven-github-plugin)
		<artifacts>
			<artifact>
				<file>ABSOLUTE_FILE_PATH</file>
				<finalName>UPLOADED_FILE_NAME</finalName>
				<description>ARTIFACT_DESCRIPTION</description>
				<override>true|false</override>
			</artifact>
		</artifacts>
	</configuration>

#### maven-github-plugin:list

This aim of this goal is just to list the available github repository downloads. To use this goal you have to add the configuration below in your `pom.xml`. After that, execute the following command `mvn com.github.maven.plugin:maven-github-plugin:list`


	<plugin>
		<groupId>com.github.maven.plugin</groupId>
		<artifactId>maven-github-plugin</artifactId>
		<version>PLUGIN_VERSION</version>
		<configuration>
			<login>YOUR_GITHUB_LOGIN</login>
			<token>YOUR_GITHUB_TOKEN</token>
			<repository>YOUR_GITHUB_REPOSITORY_URL</repository> (ex: https://github.com/kevinpollet/maven-github-plugin)
		</configuration>
	</plugin>

FAQ
---
 
* __How keep secret your github token__ ?

>A System property is associated to github token _String_. You can define it in the `settings.xml` file or by specifying the property on the command line like `mvn clean deploy -Dgithub.token=YOUR_GITHUB_TOKEN`.

* __How report a bug or request a new feature__ ?

>To report an issue or request a new feature you just have to open an issue in the repository issue tracker. After that you can add a label to it. Currently there is two label __Bug__ and __Feature__.
		
How contribute
--------------

To contribute, follow this steps:

 1. Fork this project
 2. Add the progress label to the issue you want to solve (add a comments to say that you work on it)
 3. Create a topic branch for this issue
 4. When you have finish your work, open a pull request (use the issue title for the pull request title)

Licence
-------

>Copyright 2011 Kevin Pollet

>Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

>http://www.apache.org/licenses/LICENSE-2.0

>Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License