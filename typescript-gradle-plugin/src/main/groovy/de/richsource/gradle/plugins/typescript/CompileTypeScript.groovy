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
	@Input String compilerExecutable = Os.isFamily(Os.FAMILY_WINDOWS) ? "tsc.cmd" : "tsc"
	File tsCompilerArgs = File.createTempFile("tsCompiler-", ".args")

	@TaskAction
	void compile() {
		tsCompilerArgs.deleteOnExit()
		
		logger.info "compiling TypeScript files..."

		List<String> files = source.collect{ File f -> f.toString(); };
		logger.debug("TypeScript files to compile: " + files.join(" "));

		if(outputDir) {
			tsCompilerArgs.append(" --outDir " + outputDir.toString())
		}
		if(out) {
			tsCompilerArgs.append(" --out " + out)
		}
		if(module) {
			tsCompilerArgs.append(" --module " + module.name().toLowerCase())
		}
		if(target) {
			tsCompilerArgs.append(" --target " + target.name())
		}
		if(declaration) {
			tsCompilerArgs.append(" --declaration")
		}
		if(noImplicitAny) {
			tsCompilerArgs.append(" --noImplicitAny")
		}
		if(noResolve) {
			tsCompilerArgs.append(" --noResolve")
		}
		if(codepage) {
			tsCompilerArgs.append(" --codepage " + codepage)
		}
		if(mapRoot) {
			tsCompilerArgs.append(" --mapRoot " + mapRoot)
		}
		if(removeComments) {
			tsCompilerArgs.append(" --removeComments")
		}
		if(sourcemap) {
			tsCompilerArgs.append(" --sourcemap")
		}
		if(sourceRoot) {
			tsCompilerArgs.append(" --sourceRoot " + sourceRoot)
		}
		tsCompilerArgs.append(" " + files.join(" "))

		logger.debug("Contents of typescript compiler arguments file: " + tsCompilerArgs.text);
		
		project.exec {
			executable = compilerExecutable
			args '@' + tsCompilerArgs
		}
		
		logger.info "Done TypeScript compilation."
	}
}
