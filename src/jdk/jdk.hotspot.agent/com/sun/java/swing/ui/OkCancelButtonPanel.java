/*
 * Copyright (c) 2000, 2008, Oracle and/or its affiliates. All rights reserved.
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


package com.sun.java.swing.ui;

import javax.swing.*;
import java.awt.event.ActionListener;

// Referenced classes of package com.sun.java.swing.ui:
//            CommonUI

public class OkCancelButtonPanel extends JPanel
{

    public OkCancelButtonPanel(ActionListener listener)
    {
        DelegateAction okAction = new OkAction();
        okAction.addActionListener(listener);
        DelegateAction cancelAction = new CancelAction();
        cancelAction.addActionListener(listener);
        add(CommonUI.createButton(okAction));
        add(CommonUI.createButton(cancelAction));
    }

    public static final String OK_COMMAND = "ok-command";
    public static final String CANCEL_COMMAND = "cancel-command";

}
