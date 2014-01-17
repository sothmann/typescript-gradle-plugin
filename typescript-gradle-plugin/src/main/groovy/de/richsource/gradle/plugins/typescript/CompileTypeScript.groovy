package de.richsource.gradle.plugins.typescript;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

public class CompileTypeScript extends DefaultTask {
	
	@InputFiles
	Set<File> source = [] as Set;
	
	@OutputDirectory
	File outputDir;
	
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

			args "--outDir"
			args outputDir.toString()
			args files
		}
	}
}
