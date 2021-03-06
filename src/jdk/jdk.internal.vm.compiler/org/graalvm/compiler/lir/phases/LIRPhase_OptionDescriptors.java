// CheckStyle: stop header check
// CheckStyle: stop line length check
// GENERATED CONTENT - DO NOT EDIT
// Source: LIRPhase.java
package org.graalvm.compiler.lir.phases;

import org.graalvm.compiler.options.OptionType;

public class LIRPhase_OptionDescriptors implements OptionDescriptors {
    @Override
    public OptionDescriptor get(String value) {
        switch (value) {
        // CheckStyle: stop line length check
        case "LIROptimization": {
            return OptionDescriptor.create(
                /*name*/ "LIROptimization",
                /*optionType*/ OptionType.Debug,
                /*optionValueType*/ Boolean.class,
                /*help*/ "Enable LIR level optimiztations.",
                /*declaringClass*/ LIRPhase.Options.class,
                /*fieldName*/ "LIROptimization",
                /*option*/ LIRPhase.Options.LIROptimization);
        }
        // CheckStyle: resume line length check
        }
        return null;
    }

    @Override
    public Iterator<OptionDescriptor> iterator() {
        return new Iterator<OptionDescriptor>() {
            int i = 0;
            @Override
            public boolean hasNext() {
                return i < 1;
            }
            @Override
            public OptionDescriptor next() {
                switch (i++) {
                    case 0: return get("LIROptimization");
                }
                throw new NoSuchElementException();
            }
        };
    }
}
