# TypeScript Gradle Plugin

This plugin makes it easy to build TypeScript projects using Gradle.
Among other things, the plugin provides a task to run the TypeScript compiler.

# Examples

Several example projects can be found in [/examples](examples).

# Quickstart

This will guide you through the steps needed to set up typescript-gradle-plugin for a TypeScript application project using Maven/Gradle standard layout.

## Plugin dependency

As this is not a core Gradle plugin, you have to ensure, that Gradle knows how to get the plugin. Do do this, add the following lines to your build.gradle:

    buildscript {
        repositories {
            maven {
                url 'https://github.com/sothmann/typescript-gradle-plugin/repo/'
            }
            mavenCentral()
        }
        dependencies {
            classpath 'de.richsource.gradle.plugins:typescript-gradle-plugin:0.1'
        }
    }

Next, apply the plugin.

	apply plugin: "typescript"
	
## Configuring the TypeScript compile task

You can configure the TypeScript compile task as shown below:

	compileTypeScript {
		declaration = true
		// additional configuration options
	}

# Available configuration options for the compileTypeScript task

* source - required, (Set<File>) a set of files or directories to compile
* outputDir - optional, (File) the output directory
* out - (optional), (File) Concatenate and emit output to single file
* module - (optional), (de.richsource.gradle.plugins.typescript.Module) Specify module code generation (AMD or COMMONJS)
* target - (optional), (de.richsource.gradle.plugins.typescript.Target) Specify ECMAScript target version (ES3 or ES5)
* declaration - (optional), (boolean) Generates corresponding .d.ts file
* noImplicitAny - (optional), (boolean) Warn on expressions and declarations with an implied 'any' type
* noResolve - (optional), (boolean) Skip resolution and preprocessing
* removeComments - (optional), (boolean) Do not emit comments to output
* sourcemap - (optional), (boolean) Generates corresponding .map file
* sourceRoot - (optional), (File) Specifies the location where debugger should locate TypeScript files instead of source locations
* codepage - (optional), (Integer) Specify the codepage to use when opening source files
* mapRoot - (optional), (File) Specifies the location where debugger should locate map files instead of generated locations