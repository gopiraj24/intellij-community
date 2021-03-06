// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.yaml.schema;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.json.pointer.JsonPointerPosition;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.jetbrains.jsonSchema.ide.JsonSchemaService;
import com.jetbrains.jsonSchema.impl.JsonSchemaObject;
import com.jetbrains.jsonSchema.impl.JsonSchemaResolver;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.YAMLKeyValue;

public class YamlJsonSchemaGotoDeclarationHandler implements GotoDeclarationHandler {
  @Nullable
  @Override
  public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
    final IElementType elementType = PsiUtilCore.getElementType(sourceElement);
    if (elementType != YAMLTokenTypes.SCALAR_KEY) return null;
    final YAMLKeyValue literal = PsiTreeUtil.getParentOfType(sourceElement, YAMLKeyValue.class);
    if (literal == null || literal.getKey() != sourceElement) return null;
    final JsonSchemaService service = JsonSchemaService.Impl.get(literal.getProject());
    final PsiFile containingFile = literal.getContainingFile();
    final VirtualFile file = containingFile.getVirtualFile();
    if (file == null || !service.isApplicableToFile(file)) return null;
    final JsonPointerPosition steps = new YamlJsonPsiWalker().findPosition(literal, true);
    if (steps == null) return null;
    final JsonSchemaObject schemaObject = service.getSchemaObject(file);
    if (schemaObject != null) {
      final PsiElement target = new JsonSchemaResolver(schemaObject, false, steps)
        .findNavigationTarget(literal.getValue(),
                              JsonSchemaService.isSchemaFile(containingFile));
      if (target != null) {
        return new PsiElement[] {target};
      }
    }
    return null;
  }
}
