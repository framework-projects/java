/*
 * Copyright (c) 2003, 2019, Oracle and/or its affiliates. All rights reserved.
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

/*
 * Copyright 2003 Wily Technology, Inc.
 */

/**
 * Provides services that allow Java programming language agents to instrument
 * programs running on the JVM. The mechanism for instrumentation is modification
 * of the byte-codes of methods.
 *
 * <p> Note: developers/admininstrators are responsible for verifying
 * the trustworthiness of content and structure of the Java Agents they deploy,
 * since those are able to arbitrarily transform the bytecode from other JAR files.
 * Since that happens after the Jars containing the bytecode have been verified
 * as trusted, the trustworthiness of a Java Agent can determine the trust towards
 * the entire program.
 *
 * <p> An agent is deployed as a JAR file. An attribute in the JAR file manifest
 * specifies the agent class which will be loaded to start the agent. Agents can
 * be started in several ways:
 *
 * <ol>
 *   <li><p> For implementations that support a command-line interface, an agent
 *   can be started by specifying an option on the command-line. </p></li>
 *
 *   <li><p> An implementation may support a mechanism to start agents some time
 *   after the VM has started. For example, an implementation may provide a
 *   mechanism that allows a tool to <i>attach</i> to a running application, and
 *   initiate the loading of the tool's agent into the running application. </p></li>
 *
 *   <li><p> An agent may be packaged with an application in an executable JAR
 *   file.</p></li>
 * </ol>
 *
 * <p> Each of these ways to start an agent is described below.
 *
 *
 * <h3>Starting an Agent from the Command-Line Interface</h3>
 *
 * <p> Where an implementation provides a means to start agents from the
 * command-line interface, an agent is started by adding the following option
 * to the command-line:
 *
 * <blockquote>{@code
 *     -javaagent:<jarpath>[=<options>]
 * }</blockquote>
 *
 * where <i>{@code <jarpath>}</i> is the path to the agent JAR file and
 * <i>{@code <options>}</i> is the agent options.
 *
 * <p> The manifest of the agent JAR file must contain the attribute {@code
 * Premain-Class} in its main manifest. The value of this attribute is the
 * name of the <i>agent class</i>. The agent class must implement a public
 * static {@code premain} method similar in principle to the {@code main}
 * application entry point. After the Java Virtual Machine (JVM) has
 * initialized, the {@code premain} method will be called, then the real
 * application {@code main} method. The {@code premain} method must return
 * in order for the startup to proceed.
 *
 * <p> The {@code premain} method has one of two possible signatures. The
 * JVM first attempts to invoke the following method on the agent class:
 *
 * <blockquote>{@code
 *     public static void premain(String agentArgs, Instrumentation inst)
 * }</blockquote>
 *
 * <p> If the agent class does not implement this method then the JVM will
 * attempt to invoke:
 * <blockquote>{@code
 *     public static void premain(String agentArgs)
 * }</blockquote>

 * <p> The agent class may also have an {@code agentmain} method for use when
 * the agent is started after VM startup (see below). When the agent is started
 * using a command-line option, the {@code agentmain} method is not invoked.
 *
 * <p> Each agent is passed its agent options via the {@code agentArgs} parameter.
 * The agent options are passed as a single string, any additional parsing
 * should be performed by the agent itself.
 *
 * <p> If the agent cannot be started (for example, because the agent class
 * cannot be loaded, or because the agent class does not have an appropriate
 * {@code premain} method), the JVM will abort. If a {@code premain} method
 * throws an uncaught exception, the JVM will abort.
 *
 * <p> An implementation is not required to provide a way to start agents
 * from the command-line interface. When it does, then it supports the
 * {@code -javaagent} option as specified above. The {@code -javaagent} option
 * may be used multiple times on the same command-line, thus starting multiple
 * agents. The {@code premain} methods will be called in the order that the
 * agents are specified on the command line. More than one agent may use the
 * same <i>{@code <jarpath>}</i>.
 *
 * <p> There are no modeling restrictions on what the agent {@code premain}
 * method may do. Anything application {@code main} can do, including creating
 * threads, is legal from {@code premain}.
 *
 *
 * <h3>Starting an Agent After VM Startup</h3>
 *
 * <p> An implementation may provide a mechanism to start agents sometime after
 * the the VM has started. The details as to how this is initiated are
 * implementation specific but typically the application has already started and
 * its {@code main} method has already been invoked. In cases where an
 * implementation supports the starting of agents after the VM has started the
 * following applies:
 *
 * <ol>
 *
 *   <li><p> The manifest of the agent JAR must contain the attribute {@code
 *   Agent-Class} in its main manfiest. The value of this attribute is the name
 *   of the <i>agent class</i>. </p></li>
 *
 *   <li><p> The agent class must implement a public static {@code agentmain}
 *   method. </p></li>
 *
 * </ol>
 *
 * <p> The {@code agentmain} method has one of two possible signatures. The JVM
 * first attempts to invoke the following method on the agent class:
 *
 * <blockquote>{@code
 *     public static void agentmain(String agentArgs, Instrumentation inst)
 * }</blockquote>
 *
 * <p> If the agent class does not implement this method then the JVM will
 * attempt to invoke:
 *
 * <blockquote>{@code
 *     public static void agentmain(String agentArgs)
 * }</blockquote>
 *
 * <p> The agent class may also have a {@code premain} method for use when the
 * agent is started using a command-line option. When the agent is started after
 * VM startup the {@code premain} method is not invoked.
 *
 * <p> The agent is passed its agent options via the {@code agentArgs}
 * parameter. The agent options are passed as a single string, any additional
 * parsing should be performed by the agent itself.
 *
 * <p> The {@code agentmain} method should do any necessary initialization
 * required to start the agent. When startup is complete the method should
 * return. If the agent cannot be started (for example, because the agent class
 * cannot be loaded, or because the agent class does not have a conformant
 * {@code agentmain} method), the JVM will not abort. If the {@code agentmain}
 * method throws an uncaught exception it will be ignored (but may be logged
 * by the JVM for troubleshooting purposes).
 *
 *
 * <h3>Including an Agent in an Executable JAR file</h3>
 *
 * <p> The JAR File Specification defines manifest attributes for standalone
 * applications that are packaged as <em>executable JAR files</em>. If an
 * implementation supports a mechanism to start an application as an executable
 * JAR then the main manifest may include the {@code Launcher-Agent-Class}
 * attribute to specify the class name of an agent to start before the application
 * {@code main} method is invoked. The Java virtual machine attempts to
 * invoke the following method on the agent class:
 *
 * <blockquote>{@code
 *     public static void agentmain(String agentArgs, Instrumentation inst)
 * }</blockquote>
 *
 * <p> If the agent class does not implement this method then the JVM will
 * attempt to invoke:
 *
 * <blockquote>{@code
 *     public static void agentmain(String agentArgs)
 * }</blockquote>
 *
 * <p> The value of the {@code agentArgs} parameter is always the empty string.
 *
 * <p> The {@code agentmain} method should do any necessary initialization
 * required to start the agent and return. If the agent cannot be started, for
 * example the agent class cannot be loaded, the agent class does not define a
 * conformant {@code agentmain} method, or the {@code agentmain} method throws
 * an uncaught exception or error, the JVM will abort.
 *
 *
 * <h3> Loading agent classes and the modules/classes available to the agent
 * class </h3>
 *
 * <p> Classes loaded from the agent JAR file are loaded by the
 * {@linkplain ClassLoader#getSystemClassLoader() system class loader} and are
 * members of the system class loader's {@linkplain ClassLoader#getUnnamedModule()
 * unnamed module}. The system class loader typically defines the class containing
 * the application {@code main} method too.
 *
 * <p> The classes visible to the agent class are the classes visible to the system
 * class loader and minimally include:
 *
 * <ul>
 *
 *   <li><p> The classes in packages exported by the modules in the {@linkplain
 *   ModuleLayer#boot() boot layer}. Whether the boot layer contains all platform
 *   modules or not will depend on the initial module or how the application was
 *   started. </p></li>
 *
 *   <li><p> The classes that can be defined by the system class loader (typically
 *   the class path) to be members of its unnamed module. </p></li>
 *
 *   <li><p> Any classes that the agent arranges to be defined by the bootstrap
 *   class loader to be members of its unnamed module. </p></li>
 *
 * </ul>
 *
 * <p> If agent classes need to link to classes in platform (or other) modules
 * that are not in the boot layer then the application may need to be started in
 * a way that ensures that these modules are in the boot layer. In the JDK
 * implementation for example, the {@code --add-modules} command line option can
 * be used to add modules to the set of root modules to resolve at startup. </p>
 *
 * <p> Supporting classes that the agent arranges to be loaded by the bootstrap
 * class loader (by means of {@link Instrumentation#appendToBootstrapClassLoaderSearch
 * appendToBootstrapClassLoaderSearch} or the {@code Boot-Class-Path} attribute
 * specified below), must link only to classes defined to the bootstrap class loader.
 * There is no guarantee that all platform classes can be defined by the boot
 * class loader.
 *
 * <p> If a custom system class loader is configured (by means of the system property
 * {@code java.system.class.loader} as specified in the {@link
 * ClassLoader#getSystemClassLoader() getSystemClassLoader} method) then it must
 * define the {@code appendToClassPathForInstrumentation} method as specified in
 * {@link Instrumentation#appendToSystemClassLoaderSearch appendToSystemClassLoaderSearch}.
 * In other words, a custom system class loader must support the mechanism to
 * add an agent JAR file to the system class loader search.
 *
 * <h3>Manifest Attributes</h3>
 *
 * <p> The following manifest attributes are defined for an agent JAR file:
 *
 * <blockquote><dl>
 *
 * <dt>{@code Premain-Class}</dt>
 * <dd> When an agent is specified at JVM launch time this attribute specifies
 * the agent class. That is, the class containing the {@code premain} method.
 * When an agent is specified at JVM launch time this attribute is required. If
 * the attribute is not present the JVM will abort. Note: this is a class name,
 * not a file name or path. </dd>
 *
 * <dt>{@code Agent-Class}</dt>
 * <dd> If an implementation supports a mechanism to start agents sometime after
 * the VM has started then this attribute specifies the agent class. That is,
 * the class containing the {@code agentmain} method. This attribute is required
 * if it is not present the agent will not be started. Note: this is a class name,
 * not a file name or path. </dd>
 *
 * <dt>{@code Launcher-Agent-Class}</dt>
 * <dd> If an implementation supports a mechanism to start an application as an
 * executable JAR then the main manifest may include this attribute to specify
 * the class name of an agent to start before the application {@code main}
 * method is invoked. </dd>
 *
 * <dt>{@code Boot-Class-Path}</dt>
 * <dd> A list of paths to be searched by the bootstrap class loader. Paths
 * represent directories or libraries (commonly referred to as JAR or zip
 * libraries on many platforms). These paths are searched by the bootstrap class
 * loader after the platform specific mechanisms of locating a class have failed.
 * Paths are searched in the order listed. Paths in the list are separated by one
 * or more spaces. A path takes the syntax of the path component of a hierarchical
 * URI. The path is absolute if it begins with a slash character ('/'), otherwise
 * it is relative. A relative path is resolved against the absolute path of the
 * agent JAR file. Malformed and non-existent paths are ignored. When an agent is
 * started sometime after the VM has started then paths that do not represent a
 * JAR file are ignored. This attribute is optional. </dd>
 *
 * <dt>{@code Can-Redefine-Classes}</dt>
 * <dd> Boolean ({@code true} or {@code false}, case irrelevant). Is the ability
 * to redefine classes needed by this agent. Values other than {@code true} are
 * considered {@code false}. This attribute is optional, the default is {@code
 * false}. </dd>
 *
 * <dt>{@code Can-Retransform-Classes}</dt>
 * <dd> Boolean ({@code true} or {@code false}, case irrelevant). Is the ability
 * to retransform classes needed by this agent. Values other than {@code true}
 * are considered {@code false}. This attribute is optional, the default is
 * {@code false}. </dd>
 *
 * <dt>{@code Can-Set-Native-Method-Prefix}</dt>
 * <dd> Boolean ({@code true} or {@code false}, case irrelevant). Is the ability
 * to set native method prefix needed by this agent. Values other than {@code
 * true} are considered {@code false}. This attribute is optional, the default
 * is {@code false}. </dd>
 *
 * </dl></blockquote>
 *
 * <p> An agent JAR file may have both the {@code Premain-Class} and {@code
 * Agent-Class} attributes present in the manifest. When the agent is started
 * on the command-line using the {@code -javaagent} option then the {@code
 * Premain-Class} attribute specifies the name of the agent class and the {@code
 * Agent-Class} attribute is ignored. Similarly, if the agent is started sometime
 * after the VM has started, then the {@code Agent-Class} attribute specifies
 * the name of the agent class (the value of {@code Premain-Class} attribute is
 * ignored).
 *
 *
 * <h3>Instrumenting code in modules</h3>
 *
 * <p> As an aid to agents that deploy supporting classes on the search path of
 * the bootstrap class loader, or the search path of the class loader that loads
 * the main agent class, the Java virtual machine arranges for the module of
 * transformed classes to read the unnamed module of both class loaders.
 *
 * @since 1.5
 * @revised 1.6
 * @revised 9
 */

package java.lang.instrument;