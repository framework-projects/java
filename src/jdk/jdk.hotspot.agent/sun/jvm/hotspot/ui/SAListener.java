/*
 * Copyright (c) 2004, 2005, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package sun.jvm.hotspot.ui;

import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.ui.tree.SimpleTreeNode;
import sun.jvm.hotspot.utilities.LivenessPathList;


public interface SAListener {
    public void showThreadOopInspector(JavaThread thread);
    public void showInspector(SimpleTreeNode node);
    public void showThreadStackMemory(JavaThread thread);
    public void showThreadInfo(JavaThread thread);
    public void showJavaStackTrace(JavaThread thread);
    public void showCodeViewer(Address address);
    public void showLiveness(Oop oop, LivenessPathList liveness);
}
