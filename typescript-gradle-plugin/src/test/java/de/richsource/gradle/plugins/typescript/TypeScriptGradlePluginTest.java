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

import static org.gradle.testkit.runner.TaskOutcome.FAILED;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.gradle.testkit.jarjar.org.apache.commons.io.filefilter.NameFileFilter;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TypeScriptGradlePluginTest {
	@Rule
	public final TemporaryFolder testProjectDir = new TemporaryFolder();
	private File buildFile;
	private List<File> pluginClasspath;

	@Before
	public void setup() throws IOException {
		testProjectDir.newFolder("src", "main", "ts");
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
		Map<String,String> compilerOptions = new HashMap<String, String>();
		compilerOptions.put("declaration", "true");
		compilerOptions.put("target", "\"ES5\"");
		createBuildFile(compilerOptions);
		createTSFile("src/main/ts/test.ts", "Test");

		BuildResult result = executeCompileTypeScriptTask();

		assertEquals(SUCCESS, result.task(":compileTypeScript").getOutcome());
		assertFilesInDir(defaultOutputDir(), "test.js", "test.d.ts");
	}

	@Test
	public void given_sourcemapAndInlineSourceMapOptionsBothPresent_when_compileTypeScript_then_expectValidationError()
			throws IOException {
		Map<String,String> compilerOptions = new HashMap<String, String>();
		compilerOptions.put("sourcemap", "true");
		compilerOptions.put("inlineSourceMap", "true");
		createBuildFile(compilerOptions);

		createTSFile("src/main/ts/test.ts", "Test");

		BuildResult result = GradleRunner.create()
				.withProjectDir(testProjectDir.getRoot())
				.withArguments("compileTypeScript", "--info")
				.withPluginClasspath(pluginClasspath)
				.buildAndFail();

		assertEquals(FAILED, result.task(":compileTypeScript").getOutcome());
		assertTrue("validation error expected", result.getOutput().contains("Option 'sourcemap' cannot be specified with option 'inlineSourceMap'"));
	}

	@Test
	public void given_noSourceFile_when_compileTypeScript_then_expectUpToDate()
			throws IOException {
		createBuildFile(new HashMap<String, String>());

		BuildResult result = executeCompileTypeScriptTask();

		assertEquals(UP_TO_DATE, result.task(":compileTypeScript").getOutcome());
	}

	@Test
	public void given_twoSourceFiles_when_compileTypeScript_then_expectTwoOutputFiles()
			throws IOException {
		createBuildFile(new HashMap<String, String>());
		createTSFile("src/main/ts/test.ts", "Test");
		createTSFile("src/main/ts/test2.ts", "Test2");

		BuildResult result = executeCompileTypeScriptTask();

		assertEquals(SUCCESS, result.task(":compileTypeScript").getOutcome());
		assertFilesInDir(defaultOutputDir(), "test.js", "test2.js");
	}

	@Test
	public void given_twoSourceFilesAndOutOption_when_compileTypeScript_then_expectSingleOutputFile()
			throws IOException {
		Map<String,String> compilerOptions = new HashMap<String, String>();
		compilerOptions.put("out", "file(\"build/js/out.js\")");
		createBuildFile(compilerOptions);
		createTSFile("src/main/ts/test.ts", "Test");
		createTSFile("src/main/ts/test2.ts", "Test2");

		BuildResult result = executeCompileTypeScriptTask();

		assertEquals(SUCCESS, result.task(":compileTypeScript").getOutcome());
		File tsOutputDir = new File(testProjectDir.getRoot(), "build/js");
		assertFilesInDir(tsOutputDir, "out.js");
	}

	@Test
	public void given_multipleFlagsEnabled_when_compileTypeScript_then_expectSuccess()
			throws IOException {
		List<String> flags = Arrays.asList("declaration", "noImplicitAny", "noResolve", "removeComments",
				"noEmitOnError", "experimentalDecorators", "preserveConstEnums",
				"suppressImplicitAnyIndexErrors", "noEmitHelpers", "inlineSourceMap", "inlineSources", "emitBOM",
				"emitDecoratorMetadata", "stripInternal");
		Map<String,String> compilerOptions = new HashMap<String, String>();
		for(String flag : flags) {
			compilerOptions.put(flag, "true");
		}
		createBuildFile(compilerOptions);
		createTSFile("src/main/ts/test.ts", "Test");

		BuildResult result = executeCompileTypeScriptTask();

		assertEquals(SUCCESS, result.task(":compileTypeScript").getOutcome());
		assertFilesInDir(defaultOutputDir(), "test.js", "test.d.ts");
	}

	@Test
	public void given_sourcemapFlagEnabled_when_compileTypeScript_then_expectJsAndMapFile()
			throws IOException {
		Map<String,String> compilerOptions = new HashMap<String, String>();
		compilerOptions.put("sourcemap", "true");
		createBuildFile(compilerOptions);
		createTSFile("src/main/ts/test.ts", "Test");

		BuildResult result = executeCompileTypeScriptTask();

		assertEquals(SUCCESS, result.task(":compileTypeScript").getOutcome());
		assertFilesInDir(defaultOutputDir(), "test.js", "test.js.map");
	}

	private BuildResult executeCompileTypeScriptTask() {
		return GradleRunner.create()
                    .withProjectDir(testProjectDir.getRoot())
                    .withArguments("compileTypeScript", "--info")
                    .withPluginClasspath(pluginClasspath)
                    .build();
	}

	@Test
	public void when_compileTypeScriptSucceeds_expect_noTsCompilerArgsFileRemains()
			throws IOException {
		createBuildFile(new HashMap<String, String>());
		createTSFile("src/main/ts/test.ts", "Test");

		BuildResult result = executeCompileTypeScriptTask();

		assertEquals(SUCCESS, result.task(":compileTypeScript").getOutcome());
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		for(String file : tempDir.list()) {
			assertFalse(file.matches("tsCompiler-.*\\.args"));
		}
	}

	private void assertFilesInDir(File directory, String... filenames) {
		assertNotNull(directory.listFiles());
		assertEquals(filenames.length, directory.listFiles().length);
		for(String filename : filenames) {
			assertEquals(
					"expected file " + filename + " in directory " + directory,
					1,
					directory.listFiles((FileFilter) new NameFileFilter(filename)).length);
		}
	}

	private void createTSFile(String pathAndFilename, String className) throws IOException {
		File tsFile = testProjectDir.newFile(pathAndFilename);
		writeFile(tsFile, "class " + className + " {\n" +
				"    greet(): void {\n" +
				"        console.log(\"hello world\");\n" +
				"    }\n" +
				"}\n" +
				"\n" +
				"new Test().greet();");
	}

	private void createBuildFile(Map<String,String> compilerOptions) throws IOException {
		StringBuilder buildFileContent = new StringBuilder("plugins {\n" +
				"id 'typescript'\n" +
				"}\n" +
				"compileTypeScript {\n");
		for (Map.Entry<String,String> entry : compilerOptions.entrySet()) {
			String optionName = entry.getKey();
			Object optionValue = entry.getValue();
			buildFileContent.append(optionName + " = " + optionValue + "\n");
		}
		buildFileContent.append("}");
		writeFile(buildFile, buildFileContent.toString());
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

	private File defaultOutputDir() {
		return new File(testProjectDir.getRoot(), "build/ts");
	}
}
