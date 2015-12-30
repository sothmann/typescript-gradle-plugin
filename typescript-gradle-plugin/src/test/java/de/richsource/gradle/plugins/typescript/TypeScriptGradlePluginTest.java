/**
 * Copyright (C) 2014 Sönke Sothmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.richsource.gradle.plugins.typescript;

import org.gradle.testkit.jarjar.org.apache.commons.io.filefilter.NameFileFilter;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.gradle.testkit.runner.TaskOutcome.*;

public class TypeScriptGradlePluginTest {
	@Rule
	public final TemporaryFolder testProjectDir = new TemporaryFolder();
	private File buildFile;
	private List<File> pluginClasspath;

	@Before
	public void setup() throws IOException {
		buildFile = testProjectDir.newFile("build.gradle");
		URL pluginClasspathResource = getClass().getClassLoader().getSystemResource("plugin-classpath.txt");
		if (pluginClasspathResource == null) {
			throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.");
		}

		pluginClasspath = readPluginClasspath(pluginClasspathResource);
	}

	private List<File> readPluginClasspath(URL pluginClasspathResource) throws IOException {
		File pluginClasspathManifest = new File(pluginClasspathResource.getFile());
		BufferedReader reader = new BufferedReader(new FileReader(pluginClasspathManifest));
		String line;
		List<File> files = new ArrayList<File>();
		while((line = reader.readLine()) != null) {
			files.add(new File(line));
		}
		return files;
	}

	@Test
	public void given_declarationOptionIsTrue_when_compileTypeScript_then_expectCompiledJsFileAndDeclarationFile()
			throws IOException {
		String buildFileContent = "plugins {\n" +
				  "id 'typescript'\n" +
				"}\n" +
				"compileTypeScript {\n" +
				  "declaration = true\n" +
				  "target = \"ES5\"\n" +
				"}";
		writeFile(buildFile, buildFileContent);

		testProjectDir.newFolder("src", "main", "ts");
		File tsFile = testProjectDir.newFile("src/main/ts/test.ts");
		writeFile(tsFile, "class Test {\n" +
				"    greet(): void {\n" +
				"        console.log(\"hello world\");\n" +
				"    }\n" +
				"}\n" +
				"\n" +
				"new Test().greet();");

		BuildResult result = GradleRunner.create()
				.withProjectDir(testProjectDir.getRoot())
				.withArguments("compileTypeScript", "--info")
				.withPluginClasspath(pluginClasspath)
				.forwardOutput()
				.build();

		assertEquals(SUCCESS, result.task(":compileTypeScript").getOutcome());

		File tsOutputDir = new File(testProjectDir.getRoot(), "build/ts");
		assertEquals(1, tsOutputDir.listFiles((FileFilter)new NameFileFilter("test.js")).length);
		assertEquals(1, tsOutputDir.listFiles((FilenameFilter)new NameFileFilter("test.d.ts")).length);
	}

	private void writeFile(File destination, String content) throws IOException {
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(destination));
			output.write(content);
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}
}
