// CheckStyle: stop header check
// CheckStyle: stop line length check
// GENERATED CONTENT - DO NOT EDIT
// GENERATORS: org.graalvm.compiler.replacements.processor.ReplacementsAnnotationProcessor, org.graalvm.compiler.replacements.processor.PluginGenerator
package org.graalvm.compiler.hotspot.nodes.aot;

import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.graphbuilderconf.*;

import java.lang.annotation.Annotation;

//        class: org.graalvm.compiler.hotspot.nodes.aot.LoadMethodCountersIndirectlyNode
//       method: loadMethodCounters(org.graalvm.compiler.hotspot.word.MethodPointer)
// generated-by: org.graalvm.compiler.replacements.processor.GeneratedNodeIntrinsicPlugin$ConstructorPlugin
final class Plugin_LoadMethodCountersIndirectlyNode_loadMethodCounters extends GeneratedInvocationPlugin {

    @Override
    public boolean execute(GraphBuilderContext b, ResolvedJavaMethod targetMethod, InvocationPlugin.Receiver receiver, ValueNode[] args) {
        ValueNode arg0 = args[0];
        org.graalvm.compiler.hotspot.nodes.aot.LoadMethodCountersIndirectlyNode node = new org.graalvm.compiler.hotspot.nodes.aot.LoadMethodCountersIndirectlyNode(arg0);
        b.addPush(JavaKind.Object, node);
        return true;
    }
    @Override
    public Class<? extends Annotation> getSource() {
        return org.graalvm.compiler.graph.Node.NodeIntrinsic.class;
    }
}

public class PluginFactory_LoadMethodCountersIndirectlyNode implements NodeIntrinsicPluginFactory {
    @Override
    public void registerPlugins(InvocationPlugins plugins, NodeIntrinsicPluginFactory.InjectionProvider injection) {
        plugins.register(new Plugin_LoadMethodCountersIndirectlyNode_loadMethodCounters(), org.graalvm.compiler.hotspot.nodes.aot.LoadMethodCountersIndirectlyNode.class, "loadMethodCounters", org.graalvm.compiler.hotspot.word.MethodPointer.class);
    }
}
