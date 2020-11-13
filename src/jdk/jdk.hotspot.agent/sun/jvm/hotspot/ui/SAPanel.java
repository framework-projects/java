/*
 * Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
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

/**
 * This base class encapsulates many of the events that are fired from
 * the various panels in this directory so they can easily be plugged
 * in to different containing frameworks (HSDB).
 */
public class SAPanel extends JPanel {
    protected List listeners = new ArrayList();


    public SAPanel() {
    }

    public void addPanelListener(SAListener listener) {
        listeners.add(listener);
    }

    public void removePanelListener(SAListener listener) {
        listeners.remove(listener);
    }

    public void showThreadOopInspector(JavaThread t) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showThreadOopInspector(t);
        }
    }

    public void showInspector(Oop oop) {
        showInspector(new OopTreeNodeAdapter(oop, null));
    }

    public void showInspector(SimpleTreeNode node) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showInspector(node);
        }
    }

    public void showThreadStackMemory(JavaThread t) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showThreadStackMemory(t);
        }
    }

    public void showJavaStackTrace(JavaThread t) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showJavaStackTrace(t);
        }
    }

    public void showThreadInfo(JavaThread t) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showThreadInfo(t);
        }
    }

    public void showCodeViewer(Address address) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showCodeViewer(address);
        }
    }


}
