package com.cl0udS3c

import com.intellij.codeInsight.completion.CompletionConfidence
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.ThreeState

class FilePathCompletionConfidence : CompletionConfidence(){
    override fun shouldSkipAutopopup(contextElement: PsiElement, psiFile: PsiFile, offset: Int): ThreeState {
        return ThreeState.NO
    }
}