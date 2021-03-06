/*
 * Copyright (c) 2013, 2017, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.formats.html;

import jdk.javadoc.internal.doclets.formats.html.markup.*;
import jdk.javadoc.internal.doclets.formats.html.markup.Navigation.PageMode;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.DocFileIOException;
import jdk.javadoc.internal.doclets.toolkit.util.DocPath;

import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * Abstract class to generate the module overview files in
 * Frame and Non-Frame format. This will be sub-classed to
 * generate module-overview-frame.html as well as module-overview-summary.html.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 * @author Bhavesh Patel
 */
public abstract class AbstractModuleIndexWriter extends HtmlDocletWriter {

    /**
     * Modules to be documented.
     */
    protected SortedMap<ModuleElement, Set<PackageElement>> modules;

    protected Navigation navBar;

    /**
     * Constructor. Also initializes the modules variable.
     *
     * @param configuration  The current configuration
     * @param filename Name of the module index file to be generated.
     */
    public AbstractModuleIndexWriter(HtmlConfiguration configuration,
                                      DocPath filename) {
        super(configuration, filename);
        modules = configuration.modulePackages;
        this.navBar = new Navigation(null, configuration, fixedNavDiv, PageMode.OVERVIEW, path);
    }

    /**
     * Adds the navigation bar header to the documentation tree.
     *
     * @param body the document tree to which the navigation bar header will be added
     */
    protected abstract void addNavigationBarHeader(Content body);

    /**
     * Adds the navigation bar footer to the documentation tree.
     *
     * @param body the document tree to which the navigation bar footer will be added
     */
    protected abstract void addNavigationBarFooter(Content body);

    /**
     * Adds the overview header to the documentation tree.
     *
     * @param body the document tree to which the overview header will be added
     */
    protected abstract void addOverviewHeader(Content body);

    /**
     * Adds the modules list to the documentation tree.
     *
     * @param body the document tree to which the modules list will be added
     */
    protected abstract void addModulesList(Content body);

    /**
     * Adds the module packages list to the documentation tree.
     *
     * @param modules the set of modules
     * @param text caption for the table
     * @param tableSummary summary for the table
     * @param body the document tree to which the modules list will be added
     * @param mdle the module being documented
     */
    protected abstract void addModulePackagesList(Map<ModuleElement, Set<PackageElement>> modules, String text,
            String tableSummary, Content body, ModuleElement mdle);

    /**
     * Generate and prints the contents in the module index file. Call appropriate
     * methods from the sub-class in order to generate Frame or Non
     * Frame format.
     *
     * @param title the title of the window.
     * @param includeScript boolean set true if windowtitle script is to be included
     * @throws DocFileIOException if there is a problem building the module index file
     */
    protected void buildModuleIndexFile(String title, boolean includeScript) throws DocFileIOException {
        String windowOverview = configuration.getText(title);
        Content body = getBody(includeScript, getWindowTitle(windowOverview));
        addNavigationBarHeader(body);
        addOverviewHeader(body);
        addIndex(body);
        addOverview(body);
        addNavigationBarFooter(body);
        printHtmlDocument(configuration.metakeywords.getOverviewMetaKeywords(title,
                configuration.doctitle), includeScript, body);
    }

    /**
     * Generate and prints the contents in the module packages index file. Call appropriate
     * methods from the sub-class in order to generate Frame or Non
     * Frame format.
     *
     * @param title the title of the window.
     * @param includeScript boolean set true if windowtitle script is to be included
     * @param mdle the name of the module being documented
     * @throws DocFileIOException if there is an exception building the module packages index file
     */
    protected void buildModulePackagesIndexFile(String title,
            boolean includeScript, ModuleElement mdle) throws DocFileIOException {
        String windowOverview = configuration.getText(title);
        Content body = getBody(includeScript, getWindowTitle(windowOverview));
        addNavigationBarHeader(body);
        addOverviewHeader(body);
        addModulePackagesIndex(body, mdle);
        addOverview(body);
        addNavigationBarFooter(body);
        printHtmlDocument(configuration.metakeywords.getOverviewMetaKeywords(title,
                configuration.doctitle), includeScript, body);
    }

    /**
     * Default to no overview, override to add overview.
     *
     * @param body the document tree to which the overview will be added
     */
    protected void addOverview(Content body) { }

    /**
     * Adds the frame or non-frame module index to the documentation tree.
     *
     * @param body the document tree to which the index will be added
     */
    protected void addIndex(Content body) {
        addIndexContents(configuration.modules, "doclet.Module_Summary",
                configuration.getText("doclet.Member_Table_Summary",
                configuration.getText("doclet.Module_Summary"),
                configuration.getText("doclet.modules")), body);
    }

    /**
     * Adds the frame or non-frame module packages index to the documentation tree.
     *
     * @param body the document tree to which the index will be added
     * @param mdle the module being documented
     */
    protected void addModulePackagesIndex(Content body, ModuleElement mdle) {
        addModulePackagesIndexContents("doclet.Module_Summary",
                configuration.getText("doclet.Member_Table_Summary",
                configuration.getText("doclet.Module_Summary"),
                configuration.getText("doclet.modules")), body, mdle);
    }

    /**
     * Adds module index contents. Call appropriate methods from
     * the sub-classes. Adds it to the body HtmlTree
     *
     * @param modules the modules to be documented
     * @param text string which will be used as the heading
     * @param tableSummary summary for the table
     * @param body the document tree to which the index contents will be added
     */
    protected void addIndexContents(Collection<ModuleElement> modules, String text,
            String tableSummary, Content body) {
        HtmlTree htmlTree = (configuration.allowTag(HtmlTag.NAV))
                ? HtmlTree.NAV()
                : new HtmlTree(HtmlTag.DIV);
        htmlTree.setStyle(HtmlStyle.indexNav);
        HtmlTree ul = new HtmlTree(HtmlTag.UL);
        addAllClassesLink(ul);
        addAllPackagesLink(ul);
        htmlTree.addContent(ul);
        body.addContent(htmlTree);
        addModulesList(body);
    }

    /**
     * Adds module packages index contents. Call appropriate methods from
     * the sub-classes. Adds it to the body HtmlTree
     *
     * @param text string which will be used as the heading
     * @param tableSummary summary for the table
     * @param body the document tree to which the index contents will be added
     * @param mdle the module being documented
     */
    protected void addModulePackagesIndexContents(String text,
            String tableSummary, Content body, ModuleElement mdle) {
        HtmlTree htmlTree = (configuration.allowTag(HtmlTag.NAV))
                ? HtmlTree.NAV()
                : new HtmlTree(HtmlTag.DIV);
        htmlTree.setStyle(HtmlStyle.indexNav);
        HtmlTree ul = new HtmlTree(HtmlTag.UL);
        addAllClassesLink(ul);
        addAllPackagesLink(ul);
        addAllModulesLink(ul);
        htmlTree.addContent(ul);
        body.addContent(htmlTree);
        addModulePackagesList(modules, text, tableSummary, body, mdle);
    }

    /**
     * Adds the doctitle to the documentation tree, if it is specified on the command line.
     *
     * @param body the document tree to which the title will be added
     */
    protected void addConfigurationTitle(Content body) {
        if (configuration.doctitle.length() > 0) {
            Content title = new RawHtml(configuration.doctitle);
            Content heading = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING,
                    HtmlStyle.title, title);
            Content div = HtmlTree.DIV(HtmlStyle.header, heading);
            body.addContent(div);
        }
    }

    /**
     * Do nothing. This will be overridden in ModuleIndexFrameWriter.
     *
     * @param div the document tree to which the all classes link will be added
     */
    protected void addAllClassesLink(Content div) { }

    /**
     * Do nothing. This will be overridden in ModuleIndexFrameWriter.
     *
     * @param div the document tree to which the all packages link will be added
     */
    protected void addAllPackagesLink(Content div) { }

    /**
     * Do nothing. This will be overridden in ModulePackageIndexFrameWriter.
     *
     * @param div the document tree to which the all modules link will be added
     */
    protected void addAllModulesLink(Content div) { }
}
