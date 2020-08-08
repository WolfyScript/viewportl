/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */


package me.wolfyscript.utilities.libraries.org.mozilla.javascript.optimizer;

import me.wolfyscript.utilities.libraries.org.mozilla.javascript.Kit;
import me.wolfyscript.utilities.libraries.org.mozilla.javascript.Node;
import me.wolfyscript.utilities.libraries.org.mozilla.javascript.Token;
import me.wolfyscript.utilities.libraries.org.mozilla.javascript.ast.FunctionNode;
import me.wolfyscript.utilities.libraries.org.mozilla.javascript.ast.ScriptNode;

public final class OptFunctionNode {
    public final FunctionNode fnode;
    boolean itsContainsCalls0;
    boolean itsContainsCalls1;
    private boolean[] numberVarFlags;
    private int directTargetIndex = -1;
    private boolean itsParameterNumberContext;

    OptFunctionNode(FunctionNode fnode) {
        this.fnode = fnode;
        fnode.setCompilerData(this);
    }

    public static OptFunctionNode get(ScriptNode scriptOrFn, int i) {
        FunctionNode fnode = scriptOrFn.getFunctionNode(i);
        return (OptFunctionNode) fnode.getCompilerData();
    }

    public static OptFunctionNode get(ScriptNode scriptOrFn) {
        return (OptFunctionNode) scriptOrFn.getCompilerData();
    }

    public boolean isTargetOfDirectCall() {
        return directTargetIndex >= 0;
    }

    public int getDirectTargetIndex() {
        return directTargetIndex;
    }

    void setDirectTargetIndex(int directTargetIndex) {
        // One time action
        if (directTargetIndex < 0 || this.directTargetIndex >= 0)
            Kit.codeBug();
        this.directTargetIndex = directTargetIndex;
    }

    public boolean getParameterNumberContext() {
        return itsParameterNumberContext;
    }

    void setParameterNumberContext(boolean b) {
        itsParameterNumberContext = b;
    }

    public int getVarCount() {
        return fnode.getParamAndVarCount();
    }

    public boolean isParameter(int varIndex) {
        return varIndex < fnode.getParamCount();
    }

    public boolean isNumberVar(int varIndex) {
        varIndex -= fnode.getParamCount();
        if (varIndex >= 0 && numberVarFlags != null) {
            return numberVarFlags[varIndex];
        }
        return false;
    }

    void setIsNumberVar(int varIndex) {
        varIndex -= fnode.getParamCount();
        // Can only be used with non-parameters
        if (varIndex < 0) Kit.codeBug();
        if (numberVarFlags == null) {
            int size = fnode.getParamAndVarCount() - fnode.getParamCount();
            numberVarFlags = new boolean[size];
        }
        numberVarFlags[varIndex] = true;
    }

    public int getVarIndex(Node n) {
        int index = n.getIntProp(Node.VARIABLE_PROP, -1);
        if (index == -1) {
            Node node;
            int type = n.getType();
            if (type == Token.GETVAR) {
                node = n;
            } else if (type == Token.SETVAR ||
                    type == Token.SETCONSTVAR) {
                node = n.getFirstChild();
            } else {
                throw Kit.codeBug();
            }
            index = fnode.getIndexForNameNode(node);
            if (index < 0) throw Kit.codeBug();
            n.putIntProp(Node.VARIABLE_PROP, index);
        }
        return index;
    }
}
