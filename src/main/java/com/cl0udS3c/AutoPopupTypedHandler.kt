package com.cl0udS3c

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import java.io.File

class AutoPopupTypedHandler : TypedHandlerDelegate() {
    override fun checkAutoPopup(charTyped: Char, project: Project, editor: Editor, file: PsiFile) : Result {
        if (charTyped.equals(File.separatorChar) || charTyped == '/') {
        }
        return Result.CONTINUE
    }
}