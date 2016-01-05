# TypeScript Gradle Plugin

This plugin makes it easy to build TypeScript projects using Gradle.
Among other things, the plugin provides a task to run the TypeScript compiler.


# Examples

Several example projects can be found in [/examples](examples).


# Prerequisites

You need to have installed node.js and installed the typescript node module:

	npm install -g typescript

Alternatively on windows you can install the Typescript SDK and configure the `compilerExecutable` config option to `tsc` - see *Available configuration options*.


# Quickstart

This will guide you through the steps needed to set up typescript-gradle-plugin for a TypeScript application project using Maven/Gradle standard layout.


## Plugin dependency

Build script snippet for use in all Gradle versions:

	buildscript {
	  repositories {
	    maven {
	      url "https://plugins.gradle.org/m2/"
	    }
	  }
	  dependencies {
	    classpath "de.richsource.gradle.plugins:typescript-gradle-plugin:1.7.1"
	  }
	}
	
	apply plugin: "de.richsource.gradle.plugins.typescript"
	
Build script snippet for new, incubating, plugin mechanism introduced in Gradle 2.1:

	plugins {
	  id "de.richsource.gradle.plugins.typescript" version "1.7.1"
	}


## Configuring the TypeScript compile task

You can configure the TypeScript compile task as shown below:

	compileTypeScript {
		sourcemap = true
		// additional configuration options
	}


## Run the TypeScript compiler

	gradle compileTypeScript


# Available configuration options

Here is a list of the available configuration options of the _compileTypeScript_ task:

* `source` - (File) directories to compile, defaults to `src/main/ts`
* `outputDir` - (File) the output directory, defaults to _buildDir_/ts
* `out` - (File) Concatenate and emit output to single file, e.g. `file("${buildDir}/js/out.js")`
* `module` - (de.richsource.gradle.plugins.typescript.Module) Specify module code generation (`AMD`, `COMMONJS`, `SYSTEM`, `UMD`)
* `target` - (de.richsource.gradle.plugins.typescript.Target) Specify ECMAScript target version (`ES3`, `ES5` or `ES6`)
* `declaration` - (boolean) Generates corresponding .d.ts file
* `noImplicitAny` - (boolean) Warn on expressions and declarations with an implied 'any' type
* `noResolve` - (boolean) Skip resolution and preprocessing
* `removeComments` - (boolean) Do not emit comments to output
* `sourcemap` - (boolean) Generates corresponding .map file
* `sourceRoot` - (File) Specifies the location where debugger should locate TypeScript files instead of source locations
* `codepage` - (Integer) Specify the codepage to use when opening source files
* `mapRoot` - (File) Specifies the location where debugger should locate map files instead of generated locations
* `compilerExecutable` - (String) The tsc compiler executable to use. Defaults to `cmd /c tsc.cmd` on windows and `tsc` on other systems.
* `noEmitOnError` - (boolean) Do not emit outputs if any type checking errors were reported
* `noEmit` - (boolean) Do not emit outputs
* `experimentalDecorators` - (boolean) Enables experimental support for ES7 decorators
* `newline` - (de.richsource.gradle.plugins.typescript.Newline) Specifies the end of line sequence to be used when emitting files (`CRLF` or `LF`)
* `preserveConstEnums` - (boolean) Do not erase const enum declarations in generated code
* `projectFileDir` - (File) Compile the project in the given directory where a tsconfig.json file is present. File specified with the `source` option will be ignore, but you should still explicitely configure the source files as this will make the Gradle UP-TO-DATE check work.
* `rootDir` - (File) Specifies the root directory of input files. Use to control the output directory structure with `outDir`.
* `suppressImplicitAnyIndexErrors` - (boolean) Suppress noImplicitAny errors for indexing objects lacking index signatures
* `noEmitHelpers` - (boolean) Do not emit helpers like `__extends`
* `inlineSourceMap` - (boolean) Causes source map files to be written inline in the generated .js files instead of in a independent .js.map file
* `inlineSources` - (boolean) Allows for additionally inlining the source .ts file into the .js file when used in combination with `inlineSourceMap`
* `watch` - (boolean) Watch input files
* `charset` - (String) The character set of the input files
* `emitBOM` - (boolean) Emit a UTF-8 Byte Order Mark (BOM) in the beginning of output files
* `emitDecoratorMetadata` - (boolean) Emit design-type metadata for decorated declarations in source
* `isolatedModules` - (boolean) Unconditionally emit imports for unresolved files
* `jsx` - (de.richsource.gradle.plugins.typescript.Jsx) Specify JSX code generation (`PRESERVE` or `REACT`)
* `locale` - (String) The locale to use to show error messages, e.g. `en-us`
* `moduleResolution` - (de.richsource.gradle.plugins.typescript.ModuleResoltion) Specify module resolution strategy (`NODE` or `CLASSIC`)
* `noLib` - (boolean) Do not include the default library file (`lib.d.ts`)
* `stripInternal` - (boolean) Do not emit declarations for code that has an `/** @internal */` JSDoc annotation


# Integrating the compiled files into a WAR file (for Java Webapps)

If you are integrating TypeScript into a Java web application, you can easily integrate the compiled files into the WAR file.
All you have to do is to configure the war task to pick up the compiled files.
Whenever you call the war task, the TypeScript compiler will compile your TypeScript files first.
In the example below, the compiled files will be put into the js directory in the WAR file.

	apply plugin: "war"
 
	war {
    		into("js") {
        		from compileTypeScript.outputs
    		}
	}


# Configuring multiple source directories

You can configure the TypeScript compile task to use multiple source directories as shown below:

	compileTypeScript {
		source = [file("src/main/ts"), file("src/main/additionalts")]
	}
