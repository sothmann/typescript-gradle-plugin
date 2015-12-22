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

import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction
import org.gradle.api.InvalidUserDataException
import org.apache.tools.ant.taskdefs.condition.Os

public class CompileTypeScript extends SourceTask {

	@OutputDirectory @Optional File outputDir;
	@Input @Optional File out
	@Input @Optional Module module
	@Input @Optional Target target
	@Input @Optional boolean declaration
	@Input @Optional boolean noImplicitAny
	@Input @Optional boolean noResolve
	@Input @Optional boolean removeComments
	@Input @Optional boolean sourcemap
	@Input @Optional File sourceRoot
	@Input @Optional Integer codepage
	@Input @Optional File mapRoot
	@Input @Optional boolean noEmitOnError
	@Input @Optional boolean noEmit
	@Input @Optional boolean experimentalDecorators
	@Input @Optional Newline newline
	@Input @Optional boolean preserveConstEnums
	@Input @Optional File projectFileDir
	@Input @Optional File rootDir
	@Input @Optional boolean suppressImplicitAnyIndexErrors
	@Input @Optional boolean noEmitHelpers
	@Input @Optional boolean inlineSourceMap
	@Input @Optional boolean inlineSources
	@Input @Optional boolean watch
	@Input @Optional String charset
	@Input @Optional boolean emitBOM
	@Input @Optional boolean emitDecoratorMetadata
	@Input @Optional boolean isolatedModules
	@Input @Optional Jsx jsx
	@Input @Optional locale
	@Input @Optional ModuleResoltion moduleResolution
	@Input @Optional boolean noLib
	@Input @Optional boolean stripInternal
	@Input String compilerExecutable = Os.isFamily(Os.FAMILY_WINDOWS) ? "cmd /c tsc.cmd" : "tsc"

	@TaskAction
	void compile() {
		logger.info "compiling TypeScript files..."

		validate()

		File tsCompilerArgsFile = createTsCompilerArgsFile()
		logger.debug("Contents of typescript compiler arguments file: " + tsCompilerArgsFile.text)

		List<String> compilerExecutableAndArgs = compilerExecutable.split(" ").findAll { it.length() > 0 }
		String exe = compilerExecutableAndArgs[0]
		List<String> compilerArgs = compilerExecutableAndArgs.tail() + ('@' + tsCompilerArgs)
		project.exec {
			executable = exe
			args = compilerArgs
		}

		logger.info "Done TypeScript compilation."
	}

	private File createTsCompilerArgsFile() {
		File tsCompilerArgsFile = File.createTempFile("tsCompiler-", ".args")
		tsCompilerArgsFile.deleteOnExit()

		addFlagsIfPresent(tsCompilerArgsFile,
				[
					(declaration): 'declaration',
					(noImplicitAny): 'noImplicitAny',
					(noResolve): 'noResolve',
					(removeComments): 'removeComments',
					(sourcemap): 'sourceMap',
					(noEmitOnError): 'noEmitOnError',
					(noEmit): 'noEmit',
					(experimentalDecorators): 'experimentalDecorators',
					(preserveConstEnums): 'preserveConstEnums',
					(suppressImplicitAnyIndexErrors): 'suppressImplicitAnyIndexErrors',
					(noEmitHelpers): 'noEmitHelpers',
					(inlineSourceMap): 'inlineSourceMap',
					(inlineSources): 'inlineSources',
					(watch): 'watch',
					(emitBOM): 'emitBOM',
					(emitDecoratorMetadata): 'emitDecoratorMetadata',
					(isolatedModules): 'isolatedModules',
					(noLib): 'noLib',
					(stripInternal): 'stripInternal'
				])

		if (outputDir) {
			tsCompilerArgsFile.append(" --outDir \"${outputDir.toString()}\"")
		}
		if (out) {
			tsCompilerArgsFile.append(" --out \"${out}\"")
		}
		if (module) {
			tsCompilerArgsFile.append(" --module ${module.name().toLowerCase()}")
		}
		if (target) {
			tsCompilerArgsFile.append(" --target ${target.name()}")
		}
		if (codepage) {
			tsCompilerArgsFile.append(" --codepage ${codepage}")
		}
		if (mapRoot) {
			tsCompilerArgsFile.append(" --mapRoot \"${mapRoot}\"")
		}
		if (sourceRoot) {
			tsCompilerArgsFile.append(" --sourceRoot \"${sourceRoot}\"")
		}
		if (newline) {
			tsCompilerArgsFile.append(" --newLine ${newline.name()}")
		}
		if (projectFileDir) {
			tsCompilerArgsFile.append(" --project \"${projectFileDir}\"")
		}
		if (rootDir) {
			tsCompilerArgsFile.append(" --rootDir \"${rootDir}\"")
		}
		if (charset) {
			tsCompilerArgsFile.append(" --charset ${charset}")
		}
		if (jsx) {
			tsCompilerArgsFile.append(" --jsx ${jsx.name().toLowerCase()}")
		}
		if (locale) {
			tsCompilerArgsFile.append(" --locale ${locale}")
		}
		if (moduleResolution) {
			tsCompilerArgsFile.append(" --moduleResolution ${moduleResolution.name().toLowerCase()}")
		}

		List<String> files = source.collect { File f -> return "\"${f.toString();}\"" };
		logger.debug("TypeScript files to compile: " + files.join(" "));
		if (files) {
			if (projectFileDir) {
				logger.info("Source provided in combination with projectFileDir. Source option will be ignored.")
			} else {
				tsCompilerArgsFile.append(" " + files.join(" "))
			}
		}
		return tsCompilerArgsFile
	}

	void addFlagsIfPresent(File tsCompilerArgsFile, Map<Object,String> potentialFlags) {
		potentialFlags.each { Object key, String flagName ->
			if(key) {
				tsCompilerArgsFile.append(" --${flagName}")
			}
		}
	}

	private void validate() {
		if(sourcemap && inlineSourceMap) {
			throw new InvalidUserDataException("Option 'sourcemap' cannot be specified with option 'inlineSourceMap'")
		}
	}
}
