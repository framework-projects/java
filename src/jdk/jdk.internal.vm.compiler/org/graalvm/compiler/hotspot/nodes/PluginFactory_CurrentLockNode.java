// CheckStyle: stop header check
// CheckStyle: stop line length check
// GENERATED CONTENT - DO NOT EDIT
// GENERATORS: org.graalvm.compiler.replacements.processor.ReplacementsAnnotationProcessor, org.graalvm.compiler.replacements.processor.PluginGenerator
package org.graalvm.compiler.hotspot.nodes;

import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.graphbuilderconf.*;

import java.lang.annotation.Annotation;

//        class: org.graalvm.compiler.hotspot.nodes.CurrentLockNode
//       method: currentLock(int)
// generated-by: org.graalvm.compiler.replacements.processor.GeneratedNodeIntrinsicPlugin$ConstructorPlugin
final class Plugin_CurrentLockNode_currentLock extends GeneratedInvocationPlugin {

    @Override
    public boolean execute(GraphBuilderContext b, ResolvedJavaMethod targetMethod, InvocationPlugin.Receiver receiver, ValueNode[] args) {
        org.graalvm.compiler.word.WordTypes arg0 = injectedWordTypes;
        int arg1;
        if (args[0].isConstant()) {
            arg1 = args[0].asJavaConstant().asInt();
        } else {
            assert b.canDeferPlugin(this) : b.getClass().toString();
            return false;
        }
        org.graalvm.compiler.hotspot.nodes.CurrentLockNode node = new org.graalvm.compiler.hotspot.nodes.CurrentLockNode(arg0, arg1);
        b.addPush(JavaKind.Object, node);
        return true;
    }
    @Override
    public Class<? extends Annotation> getSource() {
        return org.graalvm.compiler.graph.Node.NodeIntrinsic.class;
    }

    private final org.graalvm.compiler.word.WordTypes injectedWordTypes;

    Plugin_CurrentLockNode_currentLock(NodeIntrinsicPluginFactory.InjectionProvider injection) {
        this.injectedWordTypes = injection.getInjectedArgument(org.graalvm.compiler.word.WordTypes.class);
    }
}

public class PluginFactory_CurrentLockNode implements NodeIntrinsicPluginFactory {
    @Override
    public void registerPlugins(InvocationPlugins plugins, NodeIntrinsicPluginFactory.InjectionProvider injection) {
        plugins.register(new Plugin_CurrentLockNode_currentLock(injection), org.graalvm.compiler.hotspot.nodes.CurrentLockNode.class, "currentLock", int.class);
    }
}
