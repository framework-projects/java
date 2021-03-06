/*
 * Copyright (c) 2003, 2018, Oracle and/or its affiliates. All rights reserved.
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


import jdk.javadoc.internal.doclets.toolkit.*;
import jdk.javadoc.internal.doclets.toolkit.util.ClassTree;
import jdk.javadoc.internal.doclets.toolkit.util.VisibleMemberTable;

import javax.lang.model.element.Element;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * The factory that returns HTML writers.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 * @author Jamie Ho
 */
public class WriterFactoryImpl implements WriterFactory {

    private final HtmlConfiguration configuration;
    public WriterFactoryImpl(HtmlConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstantsSummaryWriter getConstantsSummaryWriter() {
        return new ConstantsSummaryWriterImpl(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PackageSummaryWriter getPackageSummaryWriter(PackageElement packageElement) {
        return new PackageWriterImpl(configuration, packageElement);
    }

    /**
     * {@inheritDoc}
     */
    public ModuleSummaryWriter getModuleSummaryWriter(ModuleElement mdle) {
        return new ModuleWriterImpl(configuration, mdle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassWriter getClassWriter(TypeElement typeElement, ClassTree classTree) {
        return new ClassWriterImpl(configuration, typeElement, classTree);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationTypeWriter getAnnotationTypeWriter(TypeElement annotationType) {
        return new AnnotationTypeWriterImpl(configuration, annotationType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationTypeFieldWriter getAnnotationTypeFieldWriter(
            AnnotationTypeWriter annotationTypeWriter) {
        TypeElement te = annotationTypeWriter.getAnnotationTypeElement();
        return new AnnotationTypeFieldWriterImpl(
            (SubWriterHolderWriter) annotationTypeWriter, te);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationTypeOptionalMemberWriter getAnnotationTypeOptionalMemberWriter(
        AnnotationTypeWriter annotationTypeWriter) {
        TypeElement te = annotationTypeWriter.getAnnotationTypeElement();
        return new AnnotationTypeOptionalMemberWriterImpl(
            (SubWriterHolderWriter) annotationTypeWriter, te);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationTypeRequiredMemberWriter getAnnotationTypeRequiredMemberWriter(
            AnnotationTypeWriter annotationTypeWriter) {
        TypeElement te = annotationTypeWriter.getAnnotationTypeElement();
        return new AnnotationTypeRequiredMemberWriterImpl(
            (SubWriterHolderWriter) annotationTypeWriter, te);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnumConstantWriterImpl getEnumConstantWriter(ClassWriter classWriter) {
        return new EnumConstantWriterImpl((SubWriterHolderWriter) classWriter,
                classWriter.getTypeElement());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldWriterImpl getFieldWriter(ClassWriter classWriter) {
        return new FieldWriterImpl((SubWriterHolderWriter) classWriter, classWriter.getTypeElement());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyWriterImpl getPropertyWriter(ClassWriter classWriter) {
        return new PropertyWriterImpl((SubWriterHolderWriter) classWriter,
                classWriter.getTypeElement());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MethodWriterImpl getMethodWriter(ClassWriter classWriter) {
        return new MethodWriterImpl((SubWriterHolderWriter) classWriter, classWriter.getTypeElement());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstructorWriterImpl getConstructorWriter(ClassWriter classWriter) {
        return new ConstructorWriterImpl((SubWriterHolderWriter) classWriter,
                classWriter.getTypeElement());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemberSummaryWriter getMemberSummaryWriter(ClassWriter classWriter,
            VisibleMemberTable.Kind memberType) {
        switch (memberType) {
            case CONSTRUCTORS:
                return getConstructorWriter(classWriter);
            case ENUM_CONSTANTS:
                return getEnumConstantWriter(classWriter);
            case FIELDS:
                return getFieldWriter(classWriter);
            case PROPERTIES:
                return getPropertyWriter(classWriter);
            case INNER_CLASSES:
                return new NestedClassWriterImpl((SubWriterHolderWriter)
                    classWriter, classWriter.getTypeElement());
            case METHODS:
                return getMethodWriter(classWriter);
            default:
                return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemberSummaryWriter getMemberSummaryWriter(AnnotationTypeWriter annotationTypeWriter,
            VisibleMemberTable.Kind memberType) {
        switch (memberType) {
            case ANNOTATION_TYPE_FIELDS:
                return (AnnotationTypeFieldWriterImpl)
                    getAnnotationTypeFieldWriter(annotationTypeWriter);
            case ANNOTATION_TYPE_MEMBER_OPTIONAL:
                return (AnnotationTypeOptionalMemberWriterImpl)
                    getAnnotationTypeOptionalMemberWriter(annotationTypeWriter);
            case ANNOTATION_TYPE_MEMBER_REQUIRED:
                return (AnnotationTypeRequiredMemberWriterImpl)
                    getAnnotationTypeRequiredMemberWriter(annotationTypeWriter);
            default:
                return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SerializedFormWriter getSerializedFormWriter() {
        return new SerializedFormWriterImpl(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocFilesHandler getDocFilesHandler(Element element) {
        return new DocFilesHandlerImpl(configuration, element);
    }
}
