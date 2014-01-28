package de.richsource.gradle.plugins.typescript;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

public class CompileTypeScript extends DefaultTask {
	
	@InputFiles Set<File> source = [] as Set;
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
	
	@TaskAction
	void compile() {
		println "compiling TypeScript files..."
		project.exec {
			executable = "tsc"
			List<File> files = source.collect { File source ->
				if(!source.isDirectory())
					return source
				return project.fileTree(source) { include "**/*.ts" }.files
			}.flatten()

			if(outputDir) {
				args "--outDir", outputDir.toString()
			}
			if(out) {
				args "--out", out
			}
			if(module) {
				args "--module", module.name().toLowerCase()
			}
			if(target) {
				args "--target", target.name()
			}
			if(declaration) {
				args "--declaration"
			}
			if(noImplicitAny) {
				args "--noImplicitAny"
			}
			if(noResolve) {
				args "--noResolve"
			}
			if(codepage) {
				args "--codepage", codepage
			}
			if(mapRoot) {
				args "--mapRoot", mapRoot
			}
			if(removeComments) {
				args "--removeComments"
			}
			if(sourcemap) {
				args "--sourcemap"
			}
			if(sourceRoot) {
				args "--sourceRoot", sourceRoot
			}
			args files
		}
	}
}
