/*
 * Copyright (c) 2016, 2018, Oracle and/or its affiliates. All rights reserved.
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
 *
 */

package jdk.jfr.internal.settings;

import jdk.jfr.*;
import jdk.jfr.internal.Control;
import jdk.jfr.internal.PlatformEventType;
import jdk.jfr.internal.Type;

import java.util.Objects;
import java.util.Set;

@MetadataDefinition
@Label("Enabled")
@Description("Record event")
@Name(Type.SETTINGS_PREFIX + "Enabled")
@BooleanFlag
public final class EnabledSetting extends Control {
    private final BooleanValue booleanValue;
    private final PlatformEventType eventType;

    public EnabledSetting(PlatformEventType eventType, String defaultValue) {
        super(defaultValue);
        this.booleanValue = BooleanValue.valueOf(defaultValue);
        this.eventType = Objects.requireNonNull(eventType);
    }

    @Override
    public String combine(Set<String> values) {
        return booleanValue.union(values);
    }

    @Override
    public void setValue(String value) {
        booleanValue.setValue(value);
        eventType.setEnabled(booleanValue.getBoolean());
        if (eventType.isEnabled() && !eventType.isJVM()) {
            if (!eventType.isInstrumented()) {
                eventType.markForInstrumentation(true);
            }
        }
    }

    @Override
    public String getValue() {
        return booleanValue.getValue();
    }
}
