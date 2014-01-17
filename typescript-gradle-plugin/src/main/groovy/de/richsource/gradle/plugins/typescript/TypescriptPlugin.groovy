package de.richsource.gradle.plugins.typescript

import org.gradle.api.Plugin
import org.gradle.api.Project;

class TypescriptPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.tasks.create("compileTypeScript", CompileTypeScript) {
			source += project.file("src/main/ts")
			outputDir = project.file("${project.buildDir}/ts")
		}
	}

}
