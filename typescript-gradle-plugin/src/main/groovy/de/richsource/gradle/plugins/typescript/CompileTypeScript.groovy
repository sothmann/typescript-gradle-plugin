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

package de.richsource.gradle.plugins.typescript

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.InvalidUserDataException
import org.apache.tools.ant.taskdefs.condition.Os

public class CompileTypeScript extends SourceTask {

	@OutputDirectory @Optional File outputDir;
	@OutputFile @Optional File out
	@OutputFile @Optional outFile
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
	@Input @Optional File projectFile
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
	@Input @Optional String locale
	@Input @Optional ModuleResoltion moduleResolution
	@Input @Optional boolean noLib
	@Input @Optional boolean stripInternal
	@Input @Optional boolean diagnostics
	@Input @Optional String reactNamespace
	@Input @Optional boolean listFiles
	@Input @Optional boolean skipDefaultLibCheck
	@Input @Optional boolean pretty
	@Input @Optional boolean suppressExcessPropertyErrors
	@Input @Optional boolean allowUnusedLabels
	@Input @Optional boolean noImplicitReturns
	@Input @Optional boolean noFallthroughCasesInSwitch
	@Input @Optional boolean allowUnreachableCode
	@Input @Optional boolean forceConsistentCasingInFileNames
	@Input @Optional boolean allowSyntheticDefaultImports
	@Input @Optional boolean allowJs
	@Input @Optional boolean noImplicitUseStrict
	@Input String compilerExecutable = Os.isFamily(Os.FAMILY_WINDOWS) ? "cmd /c tsc.cmd" : "tsc"

	@TaskAction
	void compile() {
		logger.info "compiling TypeScript files..."

		validate()

		File tsCompilerArgsFile = createTsCompilerArgsFile()
		logger.debug("Contents of typescript compiler arguments file: " + tsCompilerArgsFile.text)

		List<String> compilerExecutableAndArgs = compilerExecutable.split(" ").findAll { it.length() > 0 }
		String exe = compilerExecutableAndArgs[0]
		List<String> arguments = compilerExecutableAndArgs.tail() + ('@' + tsCompilerArgsFile)
		project.exec {
			executable = exe
			args = arguments
		}

		logger.info "Done TypeScript compilation."
		if(tsCompilerArgsFile.exists()) {
			tsCompilerArgsFile.delete()
		}
	}

	private File createTsCompilerArgsFile() {
		File tsCompilerArgsFile = File.createTempFile("tsCompiler-", ".args")
		tsCompilerArgsFile.deleteOnExit()

		addFlagsIfPresent(tsCompilerArgsFile, [
			'declaration': declaration,
			'noImplicitAny': noImplicitAny,
			'noResolve': noResolve,
			'removeComments': removeComments,
			'sourceMap': sourcemap,
			'noEmitOnError': noEmitOnError,
			'noEmit': noEmit,
			'experimentalDecorators': experimentalDecorators,
			'preserveConstEnums': preserveConstEnums,
			'suppressImplicitAnyIndexErrors': suppressImplicitAnyIndexErrors,
			'noEmitHelpers': noEmitHelpers,
			'inlineSourceMap': inlineSourceMap,
			'inlineSources': inlineSources,
			'watch': watch,
			'emitBOM': emitBOM,
			'emitDecoratorMetadata': emitDecoratorMetadata,
			'isolatedModules': isolatedModules,
			'noLib': noLib,
			'stripInternal': stripInternal,
			'diagnostics': diagnostics,
			'listFiles': listFiles,
			'skipDefaultLibCheck': skipDefaultLibCheck,
			'pretty': pretty,
			'suppressExcessPropertyErrors': suppressExcessPropertyErrors,
			'allowUnusedLabels': allowUnusedLabels,
			'noImplicitReturns': noImplicitReturns,
			'noFallthroughCasesInSwitch': noFallthroughCasesInSwitch,
			'allowUnreachableCode': allowUnreachableCode,
			'forceConsistentCasingInFileNames': forceConsistentCasingInFileNames,
			'allowSyntheticDefaultImports': allowSyntheticDefaultImports,
			'allowJs': allowJs,
			'noImplicitUseStrict': noImplicitUseStrict
		])

		addOptionsIfPresent(tsCompilerArgsFile, [
			'outDir': outputDir,
			'out': out,
			'outFile': outFile,
			'project': projectFile,
			'rootDir': rootDir,
			'mapRoot': mapRoot,
			'sourceRoot': sourceRoot,
			'locale': locale,
			'charset': charset,
			'codepage': codepage,
			'module': module ? module.name().toLowerCase() : null,
			'target': target ? target.name() : null,
			'newLine': newline ? newline.name() : null,
			'jsx': jsx ? jsx.name().toLowerCase() : null,
			'reactNamespace': reactNamespace,
			'moduleResolution': moduleResolution ? moduleResolution.name().toLowerCase() : null
		])

		addSourceFilesIfPresent(tsCompilerArgsFile, source, projectFile)

		return tsCompilerArgsFile
	}

	private void addSourceFilesIfPresent(File tsCompilerArgsFile, FileTree source, File projectFile) {
		List<String> files = source.collect { File f -> return "\"${f.toString();}\"" };
		logger.debug("TypeScript files to compile: " + files.join(" "));
		if (files) {
			if (projectFile) {
				logger.info("Source provided in combination with projectFile. Source option will be ignored.")
			} else {
				tsCompilerArgsFile.append(" " + files.join(" "))
			}
		}
	}

	private void addFlagsIfPresent(File tsCompilerArgsFile, Map<String, Object> potentialFlags) {
		potentialFlags.each { String flagName, Object flagValue ->
			if(flagValue) {
				tsCompilerArgsFile.append(" --${flagName}")
			}
		}
	}

	private void addOptionsIfPresent(File tsCompilerArgsFile, Map<String, Object> potentialOptions) {
		for (Map.Entry<Integer, Integer> entry : potentialOptions.entrySet()) {
			String optionName = entry.getKey()
			Object optionValue = entry.getValue()
			if(optionValue) {
				addOption(tsCompilerArgsFile, optionName, optionValue)
			}
		}
	}

	private void addOption(File tsCompilerArgsFile, String optionName, Object option) {
		if(option instanceof File) {
			tsCompilerArgsFile.append(" --${optionName} \"${option}\"")
		} else {
			tsCompilerArgsFile.append(" --${optionName} ${option}")
		}
	}

	private void validate() {
		if(sourcemap && inlineSourceMap) {
			throw new InvalidUserDataException("Option 'sourcemap' cannot be specified with option 'inlineSourceMap'")
		}
	}
}
