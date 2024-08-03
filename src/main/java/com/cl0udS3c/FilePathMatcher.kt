package com.cl0udS3c

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import okio.Path.Companion.toPath
import java.io.File
import java.io.IOException
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

object FilePathMatcher {
    fun match(str: String) : Stream<Path> {
        var matches = mutableListOf<Path>()
        val file = File(str)

        if (str.endsWith(File.separator) || str.endsWith("/")) {
            var filesInDir = file.listFiles()
            filesInDir.forEach {
                matches.add(Paths.get(it.absolutePath))
            }
        } else {
            if (file.parent != null) {
                val basename = getBaseName(str)
                val matchesInParent = File(File(str).parent)
                    .listFiles { dir: File?, name: String -> name.startsWith(basename) && name != basename }
                if (matchesInParent != null) {
                    matchesInParent.forEach {
                        matches.add(Paths.get(it.absolutePath))
                    }
                }
            }
        }
        return matches.stream().sorted()
    }

    fun aggregateFilePaths(dirContainingFile: String, queryString: String) : Stream<String> {
        var queryFile = File(queryString)
        var queryStringLastSlash = getLastIndexOfSeparator(queryString)

        if (queryFile.isAbsolute) {
            return match(queryString).map { obj: Path -> obj.toString() }
                .map { s: String ->
                    preserveOriginalPrefix(
                        queryString,
                        s,
                        queryStringLastSlash
                    )
                }
        } else if (queryString.startsWith("/")) {
            var canonicalPath: String = File(System.getProperty("user.home") + queryString.substring(1).toPath()).toString()
            if (queryString.endsWith(File.separator) || queryString.endsWith("/")) {
                canonicalPath += File.separatorChar.toString()
            }

            return match(canonicalPath)
                .map { obj: Path -> obj.toString() }
                .map { s: String ->
                    preserveOriginalPrefix(
                        queryString,
                        s,
                        queryStringLastSlash
                    )
                }
        }

        var relativeDirFile = File(dirContainingFile, queryString)
        try {
            var canonicalPath = relativeDirFile.getCanonicalPath()
            if (queryString.endsWith(File.separator) || queryString.endsWith("/")) {
                canonicalPath += File.separatorChar
            }
            return match(canonicalPath).map { obj: Path -> obj.toString() }
                .map { s: String ->
                    preserveOriginalPrefix(
                        queryString,
                        s,
                        queryStringLastSlash
                    )
                }
        } catch (e: IOException) {
            return Stream.empty()
        }
    }


    private fun preserveOriginalPrefix(
        originalQueryString: String,
        resolvedPath: String,
        queryStringLastSlash: Int
    ): String {
        return originalQueryString.substring(0, queryStringLastSlash + 1) + getBaseName(resolvedPath)
    }

    fun getLastIndexOfSeparator(path: String) : Int {
        val separatorIndex = path.lastIndexOf(File.separatorChar)
        val forwardSlashIndex = path.lastIndexOf("/")
        return Math.max(separatorIndex, forwardSlashIndex)
    }
    private fun getBaseName(path: String) : String {
        try {
            val pathObj = Paths.get(path)
            return if (pathObj.fileName == null) "" else pathObj.fileName.toString()
        } catch (e: InvalidPathException) {
            return ""
        }
    }
}