package com.cl0udS3c

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement

class FilePathCompletionContributor : CompletionContributor() {
    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        var parentDirectoryOfCurrentFile = parameters.originalFile
            .virtualFile
            .parent
        var queryString = getQueryString(parameters)
        val parentDirectoryOfCurrentFileStr: String =
            if (parentDirectoryOfCurrentFile == null) "" else parentDirectoryOfCurrentFile.canonicalPath.toString()


        FilePathMatcher.aggregateFilePaths(parentDirectoryOfCurrentFileStr, queryString).forEach { path ->
            result.withPrefixMatcher(queryString).addElement(LookupElementBuilder.create(path))
        }
    }



    fun getQueryString(parameters: CompletionParameters) : String {
        val caretPositionInString =  parameters.offset - parameters.position.textOffset
        var queryString = parameters.position.text.substring(0, caretPositionInString)

        if (queryString.startsWith("'") || queryString.startsWith("\"")) {
            queryString = queryString.substring(1)
        }
        return queryString
    }


    fun isAStringLiteral(element: PsiElement) : Boolean {
        var text: String = element.text

        return (text.startsWith("\"") && text.endsWith("\"")) ||
                (text.startsWith("'") && text.endsWith("'")) ||
                (getPreviousSiblingText(element) == "\"") && (getNextSiblingText(element) == "\"") ||
                (getPreviousSiblingText(element) == "\'") && (getNextSiblingText(element) == "\'")
    }

    fun getPreviousSiblingText(element: PsiElement) : String{
        if (element.prevSibling == null) return ""
        return element.prevSibling.text
    }

    fun getNextSiblingText(element: PsiElement) : String {
        if(element.nextSibling == null) return ""
        return element.nextSibling.text
    }
}