package com.euph28.tson.runner

import com.euph28.tson.core.Utility
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.file
import java.io.FileInputStream
import java.nio.file.Paths
import java.util.*

class TSONRunnerCLI : CliktCommand() {
    val targetFile by option("--test", help = "Target TSON file to run")
        .file(mustExist = true, canBeDir = false)
        .prompt()

    val workspace by option(help = "Folder containing test files")
        .file(mustExist = true, canBeFile = false)
        .default(Paths.get("").toFile())

    val customPropertiesFile by option("--properties", help = "Custom properties to be used")
        .file(mustExist = true, canBeDir = false)

    override fun run() {
        echo("TSON Runner - CLI Mode (" + Utility.getVersion() + ")")
        echo("Running test: $targetFile")

        // Load custom properties
        val properties = Properties()
        customPropertiesFile?.let { file -> properties.load(FileInputStream(file)) }

        // Create and run TSONRunner
        val tsonRunner = TSONRunner(workspace, properties)
        val result = tsonRunner.run(targetFile.relativeTo(workspace).toString())
        result.reportAsBasicString.forEach { echo(it) }
    }
}

fun main(args: Array<String>) = TSONRunnerCLI().main(args)