/*
 * Copyright (c) 2000, 2020, Oracle and/or its affiliates. All rights reserved.
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

package sun.security.tools.jarsigner;

/**
 * <p> This class represents the <code>ResourceBundle</code>
 * for JarSigner.
 *
 */
public class Resources_zh_CN extends java.util.ListResourceBundle {

    private static final Object[][] contents = {

        // shared (from jarsigner)
        {"SPACE", " "},
        {"2SPACE", "  "},
        {"6SPACE", "      "},
        {"COMMA", ", "},

        {"provclass.not.a.provider", "%s\u4E0D\u662F\u63D0\u4F9B\u65B9"},
        {"provider.name.not.found", "\u672A\u627E\u5230\u540D\u4E3A \"%s\" \u7684\u63D0\u4F9B\u65B9"},
        {"provider.class.not.found", "\u672A\u627E\u5230\u63D0\u4F9B\u65B9 \"%s\""},
        {"jarsigner.error.", "jarsigner \u9519\u8BEF: "},
        {"Illegal.option.", "\u975E\u6CD5\u9009\u9879: "},
        {"This.option.is.deprecated", "\u6B64\u9009\u9879\u5DF2\u8FC7\u65F6: "},
        {".keystore.must.be.NONE.if.storetype.is.{0}",
                "\u5982\u679C -storetype \u4E3A {0}, \u5219 -keystore \u5FC5\u987B\u4E3A NONE"},
        {".keypass.can.not.be.specified.if.storetype.is.{0}",
                "\u5982\u679C -storetype \u4E3A {0}, \u5219\u4E0D\u80FD\u6307\u5B9A -keypass"},
        {"If.protected.is.specified.then.storepass.and.keypass.must.not.be.specified",
                "\u5982\u679C\u6307\u5B9A\u4E86 -protected, \u5219\u4E0D\u80FD\u6307\u5B9A -storepass \u548C -keypass"},
        {"If.keystore.is.not.password.protected.then.storepass.and.keypass.must.not.be.specified",
                 "\u5982\u679C\u5BC6\u94A5\u5E93\u672A\u53D7\u53E3\u4EE4\u4FDD\u62A4, \u5219\u4E0D\u80FD\u6307\u5B9A -storepass \u548C -keypass"},
        {"Usage.jarsigner.options.jar.file.alias",
                "\u7528\u6CD5: jarsigner [\u9009\u9879] jar-file \u522B\u540D"},
        {".jarsigner.verify.options.jar.file.alias.",
                "       jarsigner -verify [\u9009\u9879] jar-file [\u522B\u540D...]"},
        {".keystore.url.keystore.location",
                "[-keystore <url>]           \u5BC6\u94A5\u5E93\u4F4D\u7F6E"},
        {".storepass.password.password.for.keystore.integrity",
            "[-storepass <\u53E3\u4EE4>]         \u7528\u4E8E\u5BC6\u94A5\u5E93\u5B8C\u6574\u6027\u7684\u53E3\u4EE4"},
        {".storetype.type.keystore.type",
                "[-storetype <\u7C7B\u578B>]         \u5BC6\u94A5\u5E93\u7C7B\u578B"},
        {".keypass.password.password.for.private.key.if.different.",
                "[-keypass <\u53E3\u4EE4>]           \u79C1\u6709\u5BC6\u94A5\u7684\u53E3\u4EE4 (\u5982\u679C\u4E0D\u540C)"},
        {".certchain.file.name.of.alternative.certchain.file",
                "[-certchain <\u6587\u4EF6>]         \u66FF\u4EE3\u8BC1\u4E66\u94FE\u6587\u4EF6\u7684\u540D\u79F0"},
        {".sigfile.file.name.of.SF.DSA.file",
                "[-sigfile <\u6587\u4EF6>]           .SF/.DSA \u6587\u4EF6\u7684\u540D\u79F0"},
        {".signedjar.file.name.of.signed.JAR.file",
                "[-signedjar <\u6587\u4EF6>]         \u5DF2\u7B7E\u540D\u7684 JAR \u6587\u4EF6\u7684\u540D\u79F0"},
        {".digestalg.algorithm.name.of.digest.algorithm",
                "[-digestalg <\u7B97\u6CD5>]        \u6458\u8981\u7B97\u6CD5\u7684\u540D\u79F0"},
        {".sigalg.algorithm.name.of.signature.algorithm",
                "[-sigalg <\u7B97\u6CD5>]           \u7B7E\u540D\u7B97\u6CD5\u7684\u540D\u79F0"},
        {".verify.verify.a.signed.JAR.file",
                "[-verify]                   \u9A8C\u8BC1\u5DF2\u7B7E\u540D\u7684 JAR \u6587\u4EF6"},
        {".verbose.suboptions.verbose.output.when.signing.verifying.",
                "[-verbose[:suboptions]]     \u7B7E\u540D/\u9A8C\u8BC1\u65F6\u8F93\u51FA\u8BE6\u7EC6\u4FE1\u606F\u3002"},
        {".suboptions.can.be.all.grouped.or.summary",
                "                            \u5B50\u9009\u9879\u53EF\u4EE5\u662F all, grouped \u6216 summary"},
        {".certs.display.certificates.when.verbose.and.verifying",
                "[-certs]                    \u8F93\u51FA\u8BE6\u7EC6\u4FE1\u606F\u548C\u9A8C\u8BC1\u65F6\u663E\u793A\u8BC1\u4E66"},
        {".tsa.url.location.of.the.Timestamping.Authority",
                "[-tsa <url>]                \u65F6\u95F4\u6233\u9881\u53D1\u673A\u6784\u7684\u4F4D\u7F6E"},
        {".tsacert.alias.public.key.certificate.for.Timestamping.Authority",
                "[-tsacert <\u522B\u540D>]           \u65F6\u95F4\u6233\u9881\u53D1\u673A\u6784\u7684\u516C\u5171\u5BC6\u94A5\u8BC1\u4E66"},
        {".tsapolicyid.tsapolicyid.for.Timestamping.Authority",
                "[-tsapolicyid <oid>]        \u65F6\u95F4\u6233\u9881\u53D1\u673A\u6784\u7684 TSAPolicyID"},
        {".tsadigestalg.algorithm.of.digest.data.in.timestamping.request",
                "[-tsadigestalg <\u7B97\u6CD5>]      \u65F6\u95F4\u6233\u8BF7\u6C42\u4E2D\u7684\u6458\u8981\u6570\u636E\u7684\u7B97\u6CD5"},
        {".altsigner.class.class.name.of.an.alternative.signing.mechanism",
                "[-altsigner <\u7C7B>]           \u66FF\u4EE3\u7684\u7B7E\u540D\u673A\u5236\u7684\u7C7B\u540D\n                            (\u6B64\u9009\u9879\u5DF2\u8FC7\u65F6\u3002)"},
        {".altsignerpath.pathlist.location.of.an.alternative.signing.mechanism",
                "[-altsignerpath <\u8DEF\u5F84\u5217\u8868>] \u66FF\u4EE3\u7684\u7B7E\u540D\u673A\u5236\u7684\u4F4D\u7F6E\n                            (\u6B64\u9009\u9879\u5DF2\u8FC7\u65F6\u3002)"},
        {".internalsf.include.the.SF.file.inside.the.signature.block",
                "[-internalsf]               \u5728\u7B7E\u540D\u5757\u5185\u5305\u542B .SF \u6587\u4EF6"},
        {".sectionsonly.don.t.compute.hash.of.entire.manifest",
                "[-sectionsonly]             \u4E0D\u8BA1\u7B97\u6574\u4E2A\u6E05\u5355\u7684\u6563\u5217"},
        {".protected.keystore.has.protected.authentication.path",
                "[-protected]                \u5BC6\u94A5\u5E93\u5177\u6709\u53D7\u4FDD\u62A4\u9A8C\u8BC1\u8DEF\u5F84"},
        {".providerName.name.provider.name",
                "[-providerName <\u540D\u79F0>]      \u63D0\u4F9B\u65B9\u540D\u79F0"},
        {".add.provider.option",
                "[-addprovider <\u540D\u79F0>        \u6309\u540D\u79F0 (\u4F8B\u5982 SunPKCS11) \u6DFB\u52A0\u5B89\u5168\u63D0\u4F9B\u65B9"},
        {".providerArg.option.1",
                "  [-providerArg <\u53C2\u6570>]] ... \u914D\u7F6E -addprovider \u7684\u53C2\u6570"},
        {".providerClass.option",
                "[-providerClass <\u7C7B>     \u6309\u5168\u9650\u5B9A\u7C7B\u540D\u6DFB\u52A0\u5B89\u5168\u63D0\u4F9B\u65B9"},
        {".providerArg.option.2",
                "  [-providerArg <\u53C2\u6570>]] ... \u914D\u7F6E -providerClass \u7684\u53C2\u6570"},
        {".strict.treat.warnings.as.errors",
                "[-strict]                   \u5C06\u8B66\u544A\u89C6\u4E3A\u9519\u8BEF"},
        {".conf.url.specify.a.pre.configured.options.file",
                "[-conf <url>]               \u6307\u5B9A\u9884\u914D\u7F6E\u7684\u9009\u9879\u6587\u4EF6"},
        {".print.this.help.message",
                "[-? -h --help]              \u8F93\u51FA\u6B64\u5E2E\u52A9\u6D88\u606F"},
        {"Option.lacks.argument", "\u9009\u9879\u7F3A\u5C11\u53C2\u6570"},
        {"Please.type.jarsigner.help.for.usage", "\u8BF7\u952E\u5165 jarsigner --help \u4EE5\u4E86\u89E3\u7528\u6CD5"},
        {"Please.specify.jarfile.name", "\u8BF7\u6307\u5B9A jarfile \u540D\u79F0"},
        {"Please.specify.alias.name", "\u8BF7\u6307\u5B9A\u522B\u540D"},
        {"Only.one.alias.can.be.specified", "\u53EA\u80FD\u6307\u5B9A\u4E00\u4E2A\u522B\u540D"},
        {"This.jar.contains.signed.entries.which.is.not.signed.by.the.specified.alias.es.",
                 "\u6B64 jar \u5305\u542B\u672A\u7531\u6307\u5B9A\u522B\u540D\u7B7E\u540D\u7684\u5DF2\u7B7E\u540D\u6761\u76EE\u3002"},
        {"This.jar.contains.signed.entries.that.s.not.signed.by.alias.in.this.keystore.",
                  "\u6B64 jar \u5305\u542B\u672A\u7531\u6B64\u5BC6\u94A5\u5E93\u4E2D\u7684\u522B\u540D\u7B7E\u540D\u7684\u5DF2\u7B7E\u540D\u6761\u76EE\u3002"},
        {"s", "s"},
        {"m", "m"},
        {"k", "k"},
        {".and.d.more.", "(%d \u53CA\u4EE5\u4E0A)"},
        {".s.signature.was.verified.",
                "  s = \u5DF2\u9A8C\u8BC1\u7B7E\u540D "},
        {".m.entry.is.listed.in.manifest",
                "  m = \u5728\u6E05\u5355\u4E2D\u5217\u51FA\u6761\u76EE"},
        {".k.at.least.one.certificate.was.found.in.keystore",
                "  k = \u5728\u5BC6\u94A5\u5E93\u4E2D\u81F3\u5C11\u627E\u5230\u4E86\u4E00\u4E2A\u8BC1\u4E66"},
        {".X.not.signed.by.specified.alias.es.",
                "  X = \u672A\u7531\u6307\u5B9A\u522B\u540D\u7B7E\u540D"},
        {"no.manifest.", "\u6CA1\u6709\u6E05\u5355\u3002"},
        {".Signature.related.entries.","(\u4E0E\u7B7E\u540D\u76F8\u5173\u7684\u6761\u76EE)"},
        {".Unsigned.entries.", "(\u672A\u7B7E\u540D\u6761\u76EE)"},
        {"jar.is.unsigned",
                "jar \u672A\u7B7E\u540D\u3002"},
        {"jar.treated.unsigned",
                "\u8B66\u544A: \u7B7E\u540D\u65E0\u6CD5\u89E3\u6790\u6216\u9A8C\u8BC1, \u8BE5 jar \u5C06\u88AB\u89C6\u4E3A\u672A\u7B7E\u540D\u3002\u6709\u5173\u8BE6\u7EC6\u4FE1\u606F, \u8BF7\u5728\u542F\u7528\u8C03\u8BD5\u7684\u60C5\u51B5\u4E0B\u91CD\u65B0\u8FD0\u884C jarsigner (-J-Djava.security.debug=jar)\u3002"},
        {"jar.treated.unsigned.see.weak",
                "\u7531\u4E8E\u8BE5 jar \u662F\u4F7F\u7528\u76EE\u524D\u5DF2\u7981\u7528\u7684\u5F31\u7B97\u6CD5\u7B7E\u540D\u7684, \u56E0\u6B64\u8BE5 jar \u5C06\u88AB\u89C6\u4E3A\u672A\u7B7E\u540D\u3002\n\n\u6709\u5173\u8BE6\u7EC6\u4FE1\u606F, \u8BF7\u4F7F\u7528 -verbose \u9009\u9879\u91CD\u65B0\u8FD0\u884C jarsigner\u3002"},
        {"jar.treated.unsigned.see.weak.verbose",
                "\u8B66\u544A: \u8BE5 jar \u5C06\u88AB\u89C6\u4E3A\u672A\u7B7E\u540D, \u56E0\u4E3A\u5B83\u662F\u7531\u76EE\u524D\u5B89\u5168\u5C5E\u6027\u7981\u7528\u7684\u5F31\u7B97\u6CD5\u7B7E\u540D\u7684:"},
        {"jar.signed.", "jar \u5DF2\u7B7E\u540D\u3002"},
        {"jar.signed.with.signer.errors.", "jar \u5DF2\u7B7E\u540D, \u4F46\u51FA\u73B0\u7B7E\u540D\u8005\u9519\u8BEF\u3002"},
        {"jar.verified.", "jar \u5DF2\u9A8C\u8BC1\u3002"},
        {"jar.verified.with.signer.errors.", "jar \u5DF2\u9A8C\u8BC1, \u4F46\u51FA\u73B0\u7B7E\u540D\u8005\u9519\u8BEF\u3002"},

        {"history.with.ts", "- \u7531 \"%1$s\" \u7B7E\u540D\n    \u6458\u8981\u7B97\u6CD5: %2$s\n    \u7B7E\u540D\u7B97\u6CD5: %3$s, %4$s\n  \u7531 \"%6$s\" \u4E8E %5$tc \u52A0\u65F6\u95F4\u6233\n    \u65F6\u95F4\u6233\u6458\u8981\u7B97\u6CD5: %7$s\n    \u65F6\u95F4\u6233\u7B7E\u540D\u7B97\u6CD5: %8$s, %9$s"},
        {"history.without.ts", "- \u7531 \"%1$s\" \u7B7E\u540D\n    \u6458\u8981\u7B97\u6CD5: %2$s\n    \u7B7E\u540D\u7B97\u6CD5: %3$s, %4$s"},
        {"history.unparsable", "- \u65E0\u6CD5\u89E3\u6790\u7684\u4E0E\u7B7E\u540D\u76F8\u5173\u7684\u6587\u4EF6 %s"},
        {"history.nosf", "- \u7F3A\u5C11\u4E0E\u7B7E\u540D\u76F8\u5173\u7684\u6587\u4EF6 META-INF/%s.SF"},
        {"history.nobk", "- \u4E0E\u7B7E\u540D\u76F8\u5173\u7684\u6587\u4EF6 META-INF/%s.SF \u7F3A\u5C11\u5757\u6587\u4EF6"},

        {"with.weak", "%s (\u5F31)"},
        {"with.disabled", "%s\uFF08\u7981\u7528\uFF09"},
        {"key.bit", "%d \u4F4D\u5BC6\u94A5"},
        {"key.bit.weak", "%d \u4F4D\u5BC6\u94A5 (\u5F31)"},
        {"key.bit.disabled", "%d \u4F4D\u5BC6\u94A5\uFF08\u7981\u7528\uFF09"},
        {"unknown.size", "\u672A\u77E5\u5927\u5C0F"},

        {"jarsigner.", "jarsigner: "},
        {"signature.filename.must.consist.of.the.following.characters.A.Z.0.9.or.",
                "\u7B7E\u540D\u6587\u4EF6\u540D\u5FC5\u987B\u5305\u542B\u4EE5\u4E0B\u5B57\u7B26: A-Z, 0-9, _ \u6216 -"},
        {"unable.to.open.jar.file.", "\u65E0\u6CD5\u6253\u5F00 jar \u6587\u4EF6: "},
        {"unable.to.create.", "\u65E0\u6CD5\u521B\u5EFA: "},
        {".adding.", "   \u6B63\u5728\u6DFB\u52A0: "},
        {".updating.", " \u6B63\u5728\u66F4\u65B0: "},
        {".signing.", "  \u6B63\u5728\u7B7E\u540D: "},
        {"attempt.to.rename.signedJarFile.to.jarFile.failed",
                "\u5C1D\u8BD5\u5C06{0}\u91CD\u547D\u540D\u4E3A{1}\u65F6\u5931\u8D25"},
        {"attempt.to.rename.jarFile.to.origJar.failed",
                "\u5C1D\u8BD5\u5C06{0}\u91CD\u547D\u540D\u4E3A{1}\u65F6\u5931\u8D25"},
        {"unable.to.sign.jar.", "\u65E0\u6CD5\u5BF9 jar \u8FDB\u884C\u7B7E\u540D: "},
        {"Enter.Passphrase.for.keystore.", "\u8F93\u5165\u5BC6\u94A5\u5E93\u7684\u5BC6\u7801\u77ED\u8BED: "},
        {"keystore.load.", "\u5BC6\u94A5\u5E93\u52A0\u8F7D: "},
        {"certificate.exception.", "\u8BC1\u4E66\u5F02\u5E38\u9519\u8BEF: "},
        {"unable.to.instantiate.keystore.class.",
                "\u65E0\u6CD5\u5B9E\u4F8B\u5316\u5BC6\u94A5\u5E93\u7C7B: "},
        {"Certificate.chain.not.found.for.alias.alias.must.reference.a.valid.KeyStore.key.entry.containing.a.private.key.and",
                "\u627E\u4E0D\u5230{0}\u7684\u8BC1\u4E66\u94FE\u3002{1}\u5FC5\u987B\u5F15\u7528\u5305\u542B\u79C1\u6709\u5BC6\u94A5\u548C\u76F8\u5E94\u7684\u516C\u5171\u5BC6\u94A5\u8BC1\u4E66\u94FE\u7684\u6709\u6548\u5BC6\u94A5\u5E93\u5BC6\u94A5\u6761\u76EE\u3002"},
        {"File.specified.by.certchain.does.not.exist",
                "\u7531 -certchain \u6307\u5B9A\u7684\u6587\u4EF6\u4E0D\u5B58\u5728"},
        {"Cannot.restore.certchain.from.file.specified",
                "\u65E0\u6CD5\u4ECE\u6307\u5B9A\u7684\u6587\u4EF6\u8FD8\u539F certchain"},
        {"Certificate.chain.not.found.in.the.file.specified.",
                "\u5728\u6307\u5B9A\u7684\u6587\u4EF6\u4E2D\u627E\u4E0D\u5230\u8BC1\u4E66\u94FE\u3002"},
        {"found.non.X.509.certificate.in.signer.s.chain",
                "\u5728\u7B7E\u540D\u8005\u7684\u94FE\u4E2D\u627E\u5230\u975E X.509 \u8BC1\u4E66"},
        {"incomplete.certificate.chain", "\u8BC1\u4E66\u94FE\u4E0D\u5B8C\u6574"},
        {"Enter.key.password.for.alias.", "\u8F93\u5165{0}\u7684\u5BC6\u94A5\u53E3\u4EE4: "},
        {"unable.to.recover.key.from.keystore",
                "\u65E0\u6CD5\u4ECE\u5BC6\u94A5\u5E93\u4E2D\u6062\u590D\u5BC6\u94A5"},
        {"key.associated.with.alias.not.a.private.key",
                "\u4E0E{0}\u5173\u8054\u7684\u5BC6\u94A5\u4E0D\u662F\u79C1\u6709\u5BC6\u94A5"},
        {"you.must.enter.key.password", "\u5FC5\u987B\u8F93\u5165\u5BC6\u94A5\u53E3\u4EE4"},
        {"unable.to.read.password.", "\u65E0\u6CD5\u8BFB\u53D6\u53E3\u4EE4: "},
        {"certificate.is.valid.from", "\u8BC1\u4E66\u7684\u6709\u6548\u671F\u4E3A{0}\u81F3{1}"},
        {"certificate.expired.on", "\u8BC1\u4E66\u5230\u671F\u65E5\u671F\u4E3A {0}"},
        {"certificate.is.not.valid.until",
                "\u76F4\u5230{0}, \u8BC1\u4E66\u624D\u6709\u6548"},
        {"certificate.will.expire.on", "\u8BC1\u4E66\u5C06\u5728{0}\u5230\u671F"},
        {".Invalid.certificate.chain.", "[\u65E0\u6548\u7684\u8BC1\u4E66\u94FE: "},
        {".Invalid.TSA.certificate.chain.", "[\u65E0\u6548 TSA \u7684\u8BC1\u4E66\u94FE: "},
        {"requesting.a.signature.timestamp",
                "\u6B63\u5728\u8BF7\u6C42\u7B7E\u540D\u65F6\u95F4\u6233"},
        {"TSA.location.", "TSA \u4F4D\u7F6E: "},
        {"TSA.certificate.", "TSA \u8BC1\u4E66: "},
        {"no.response.from.the.Timestamping.Authority.",
                "\u65F6\u95F4\u6233\u9881\u53D1\u673A\u6784\u6CA1\u6709\u54CD\u5E94\u3002\u5982\u679C\u8981\u4ECE\u9632\u706B\u5899\u540E\u9762\u8FDE\u63A5, \u5219\u53EF\u80FD\u9700\u8981\u6307\u5B9A HTTP \u6216 HTTPS \u4EE3\u7406\u3002\u8BF7\u4E3A jarsigner \u63D0\u4F9B\u4EE5\u4E0B\u9009\u9879: "},
        {"or", "\u6216"},
        {"Certificate.not.found.for.alias.alias.must.reference.a.valid.KeyStore.entry.containing.an.X.509.public.key.certificate.for.the",
                "\u627E\u4E0D\u5230{0}\u7684\u8BC1\u4E66\u3002{1}\u5FC5\u987B\u5F15\u7528\u5305\u542B\u65F6\u95F4\u6233\u9881\u53D1\u673A\u6784\u7684 X.509 \u516C\u5171\u5BC6\u94A5\u8BC1\u4E66\u7684\u6709\u6548\u5BC6\u94A5\u5E93\u6761\u76EE\u3002"},
        {"using.an.alternative.signing.mechanism",
                "\u6B63\u5728\u4F7F\u7528\u66FF\u4EE3\u7684\u7B7E\u540D\u673A\u5236"},
        {"entry.was.signed.on", "\u6761\u76EE\u7684\u7B7E\u540D\u65E5\u671F\u4E3A {0}"},
        {"Warning.", "\u8B66\u544A: "},
        {"Error.", "\u9519\u8BEF: "},
        {"...Signer", ">>> \u7B7E\u540D\u8005"},
        {"...TSA", ">>> TSA"},
        {"trusted.certificate", "\u53EF\u4FE1\u8BC1\u4E66"},
        {"This.jar.contains.unsigned.entries.which.have.not.been.integrity.checked.",
                "\u6B64 jar \u5305\u542B\u5C1A\u672A\u8FDB\u884C\u5B8C\u6574\u6027\u68C0\u67E5\u7684\u672A\u7B7E\u540D\u6761\u76EE\u3002 "},
        {"This.jar.contains.entries.whose.signer.certificate.has.expired.",
                "\u6B64 jar \u5305\u542B\u7B7E\u540D\u8005\u8BC1\u4E66\u5DF2\u8FC7\u671F\u7684\u6761\u76EE\u3002 "},
        {"This.jar.contains.entries.whose.signer.certificate.will.expire.within.six.months.",
                "\u6B64 jar \u5305\u542B\u7B7E\u540D\u8005\u8BC1\u4E66\u5C06\u5728\u516D\u4E2A\u6708\u5185\u8FC7\u671F\u7684\u6761\u76EE\u3002 "},
        {"This.jar.contains.entries.whose.signer.certificate.is.not.yet.valid.",
                "\u6B64 jar \u5305\u542B\u7B7E\u540D\u8005\u8BC1\u4E66\u4ECD\u65E0\u6548\u7684\u6761\u76EE\u3002 "},
        {"This.jar.contains.entries.whose.signer.certificate.is.self.signed.",
                "\u6B64 jar \u5305\u542B\u5176\u7B7E\u540D\u8005\u8BC1\u4E66\u4E3A\u81EA\u7B7E\u540D\u8BC1\u4E66\u7684\u6761\u76EE\u3002"},
        {"Re.run.with.the.verbose.option.for.more.details.",
                "\u6709\u5173\u8BE6\u7EC6\u4FE1\u606F, \u8BF7\u4F7F\u7528 -verbose \u9009\u9879\u91CD\u65B0\u8FD0\u884C\u3002"},
        {"Re.run.with.the.verbose.and.certs.options.for.more.details.",
                "\u6709\u5173\u8BE6\u7EC6\u4FE1\u606F, \u8BF7\u4F7F\u7528 -verbose \u548C -certs \u9009\u9879\u91CD\u65B0\u8FD0\u884C\u3002"},
        {"The.signer.certificate.has.expired.",
                "\u7B7E\u540D\u8005\u8BC1\u4E66\u5DF2\u8FC7\u671F\u3002"},
        {"The.timestamp.expired.1.but.usable.2",
                "\u65F6\u95F4\u6233\u5230\u671F\u65E5\u671F\u4E3A %1$tY-%1$tm-%1$td\u3002\u4E0D\u8FC7\uFF0C\u5728\u7B7E\u540D\u8005\u8BC1\u4E66\u4E8E %2$tY-%2$tm-%2$td \u5230\u671F\u4E4B\u524D\uFF0CJAR \u5C06\u6709\u6548\u3002"},
        {"The.timestamp.has.expired.",
                "\u65F6\u95F4\u6233\u5DF2\u5230\u671F\u3002"},
        {"The.signer.certificate.will.expire.within.six.months.",
                "\u7B7E\u540D\u8005\u8BC1\u4E66\u5C06\u5728\u516D\u4E2A\u6708\u5185\u8FC7\u671F\u3002"},
        {"The.timestamp.will.expire.within.one.year.on.1",
                "\u65F6\u95F4\u6233\u5C06\u5728\u4E00\u5E74\u5185\u4E8E %1$tY-%1$tm-%1$td \u5230\u671F\u3002"},
        {"The.timestamp.will.expire.within.one.year.on.1.but.2",
                "\u65F6\u95F4\u6233\u5C06\u5728\u4E00\u5E74\u5185\u4E8E %1$tY-%1$tm-%1$td \u5230\u671F\u3002\u4E0D\u8FC7\uFF0C\u5728\u7B7E\u540D\u8005\u8BC1\u4E66\u4E8E %2$tY-%2$tm-%2$td \u5230\u671F\u4E4B\u524D\uFF0CJAR \u5C06\u6709\u6548\u3002"},
        {"The.signer.certificate.is.not.yet.valid.",
                "\u7B7E\u540D\u8005\u8BC1\u4E66\u4ECD\u65E0\u6548\u3002"},
        {"The.signer.certificate.s.KeyUsage.extension.doesn.t.allow.code.signing.",
                 "\u7531\u4E8E\u7B7E\u540D\u8005\u8BC1\u4E66\u7684 KeyUsage \u6269\u5C55\u800C\u65E0\u6CD5\u8FDB\u884C\u4EE3\u7801\u7B7E\u540D\u3002"},
        {"The.signer.certificate.s.ExtendedKeyUsage.extension.doesn.t.allow.code.signing.",
                 "\u7531\u4E8E\u7B7E\u540D\u8005\u8BC1\u4E66\u7684 ExtendedKeyUsage \u6269\u5C55\u800C\u65E0\u6CD5\u8FDB\u884C\u4EE3\u7801\u7B7E\u540D\u3002"},
        {"The.signer.certificate.s.NetscapeCertType.extension.doesn.t.allow.code.signing.",
                 "\u7531\u4E8E\u7B7E\u540D\u8005\u8BC1\u4E66\u7684 NetscapeCertType \u6269\u5C55\u800C\u65E0\u6CD5\u8FDB\u884C\u4EE3\u7801\u7B7E\u540D\u3002"},
        {"This.jar.contains.entries.whose.signer.certificate.s.KeyUsage.extension.doesn.t.allow.code.signing.",
                 "\u6B64 jar \u5305\u542B\u7531\u4E8E\u7B7E\u540D\u8005\u8BC1\u4E66\u7684 KeyUsage \u6269\u5C55\u800C\u65E0\u6CD5\u8FDB\u884C\u4EE3\u7801\u7B7E\u540D\u7684\u6761\u76EE\u3002"},
        {"This.jar.contains.entries.whose.signer.certificate.s.ExtendedKeyUsage.extension.doesn.t.allow.code.signing.",
                 "\u6B64 jar \u5305\u542B\u7531\u4E8E\u7B7E\u540D\u8005\u8BC1\u4E66\u7684 ExtendedKeyUsage \u6269\u5C55\u800C\u65E0\u6CD5\u8FDB\u884C\u4EE3\u7801\u7B7E\u540D\u7684\u6761\u76EE\u3002"},
        {"This.jar.contains.entries.whose.signer.certificate.s.NetscapeCertType.extension.doesn.t.allow.code.signing.",
                 "\u6B64 jar \u5305\u542B\u7531\u4E8E\u7B7E\u540D\u8005\u8BC1\u4E66\u7684 NetscapeCertType \u6269\u5C55\u800C\u65E0\u6CD5\u8FDB\u884C\u4EE3\u7801\u7B7E\u540D\u7684\u6761\u76EE\u3002"},
        {".{0}.extension.does.not.support.code.signing.",
                 "[{0} \u6269\u5C55\u4E0D\u652F\u6301\u4EE3\u7801\u7B7E\u540D]"},
        {"The.signer.s.certificate.chain.is.invalid.reason.1",
                "\u7B7E\u540D\u8005\u8BC1\u4E66\u94FE\u65E0\u6548\u3002\u539F\u56E0: %s"},
        {"The.tsa.certificate.chain.is.invalid.reason.1",
                "TSA \u8BC1\u4E66\u94FE\u65E0\u6548\u3002\u539F\u56E0: %s"},
        {"The.signer.s.certificate.is.self.signed.",
                "\u7B7E\u540D\u8005\u8BC1\u4E66\u4E3A\u81EA\u7B7E\u540D\u8BC1\u4E66\u3002"},
        {"The.1.algorithm.specified.for.the.2.option.is.considered.a.security.risk..This.algorithm.will.be.disabled.in.a.future.update.",
                "\u4E3A %2$s \u9009\u9879\u6307\u5B9A\u7684 %1$s \u7B97\u6CD5\u88AB\u89C6\u4E3A\u5B58\u5728\u5B89\u5168\u98CE\u9669\u3002\u6B64\u7B97\u6CD5\u5C06\u5728\u672A\u6765\u7684\u66F4\u65B0\u4E2D\u88AB\u7981\u7528\u3002"},
        {"The.1.algorithm.specified.for.the.2.option.is.considered.a.security.risk.and.is.disabled.",
                "\u4E3A %2$s \u9009\u9879\u6307\u5B9A\u7684 %1$s \u7B97\u6CD5\u88AB\u89C6\u4E3A\u5B58\u5728\u5B89\u5168\u98CE\u9669\u800C\u4E14\u88AB\u7981\u7528\u3002"},
        {"The.digest.algorithm.1.is.considered.a.security.risk..This.algorithm.will.be.disabled.in.a.future.update.",
                "%1$s \u6458\u8981\u7B97\u6CD5\u88AB\u89C6\u4E3A\u5B58\u5728\u5B89\u5168\u98CE\u9669\u3002\u6B64\u7B97\u6CD5\u5C06\u5728\u672A\u6765\u7684\u66F4\u65B0\u4E2D\u88AB\u7981\u7528\u3002"},
        {"The.signature.algorithm.1.is.considered.a.security.risk..This.algorithm.will.be.disabled.in.a.future.update.",
                "%1$s \u7B7E\u540D\u7B97\u6CD5\u88AB\u89C6\u4E3A\u5B58\u5728\u5B89\u5168\u98CE\u9669\u3002\u6B64\u7B97\u6CD5\u5C06\u5728\u672A\u6765\u7684\u66F4\u65B0\u4E2D\u88AB\u7981\u7528\u3002"},
        {"The.1.signing.key.has.a.keysize.of.2.which.is.considered.a.security.risk..This.key.size.will.be.disabled.in.a.future.update.",
                "%1$s \u7B7E\u540D\u5BC6\u94A5\u7684\u5BC6\u94A5\u5927\u5C0F %2$d \u88AB\u89C6\u4E3A\u5B58\u5728\u5B89\u5168\u98CE\u9669\u3002\u6B64\u5BC6\u94A5\u5927\u5C0F\u5C06\u5728\u672A\u6765\u7684\u66F4\u65B0\u4E2D\u88AB\u7981\u7528\u3002"},
        {"The.1.signing.key.has.a.keysize.of.2.which.is.considered.a.security.risk.and.is.disabled.",
                "%1$s \u7B7E\u540D\u5BC6\u94A5\u7684\u5BC6\u94A5\u5927\u5C0F %2$d \u88AB\u89C6\u4E3A\u5B58\u5728\u5B89\u5168\u98CE\u9669\u800C\u4E14\u88AB\u7981\u7528\u3002"},
        {"This.jar.contains.entries.whose.certificate.chain.is.invalid.reason.1",
                 "\u6B64 jar \u5305\u542B\u5176\u8BC1\u4E66\u94FE\u65E0\u6548\u7684\u6761\u76EE\u3002\u539F\u56E0: %s"},
        {"This.jar.contains.entries.whose.tsa.certificate.chain.is.invalid.reason.1",
                "\u6B64 jar \u5305\u542B\u5176 TSA \u8BC1\u4E66\u94FE\u65E0\u6548\u7684\u6761\u76EE\u3002\u539F\u56E0: %s"},
        {"no.timestamp.signing",
                "\u672A\u63D0\u4F9B -tsa \u6216 -tsacert, \u6B64 jar \u6CA1\u6709\u65F6\u95F4\u6233\u3002\u5982\u679C\u6CA1\u6709\u65F6\u95F4\u6233, \u5219\u5728\u7B7E\u540D\u8005\u8BC1\u4E66\u7684\u5230\u671F\u65E5\u671F (%1$tY-%1$tm-%1$td) \u4E4B\u540E, \u7528\u6237\u53EF\u80FD\u65E0\u6CD5\u9A8C\u8BC1\u6B64 jar\u3002"},
        {"invalid.timestamp.signing",
                "\u65F6\u95F4\u6233\u65E0\u6548\u3002\u5982\u679C\u6CA1\u6709\u6709\u6548\u7684\u65F6\u95F4\u6233\uFF0C\u5219\u5728\u7B7E\u540D\u8005\u8BC1\u4E66\u7684\u5230\u671F\u65E5\u671F (%1$tY-%1$tm-%1$td) \u4E4B\u540E\uFF0C\u7528\u6237\u53EF\u80FD\u65E0\u6CD5\u9A8C\u8BC1\u6B64 jar\u3002"},
        {"no.timestamp.verifying",
                "\u6B64 jar \u5305\u542B\u7684\u7B7E\u540D\u6CA1\u6709\u65F6\u95F4\u6233\u3002\u5982\u679C\u6CA1\u6709\u65F6\u95F4\u6233, \u5219\u5728\u5176\u4E2D\u4EFB\u4E00\u7B7E\u540D\u8005\u8BC1\u4E66\u5230\u671F (\u6700\u65E9\u4E3A %1$tY-%1$tm-%1$td) \u4E4B\u540E, \u7528\u6237\u53EF\u80FD\u65E0\u6CD5\u9A8C\u8BC1\u6B64 jar\u3002"},
        {"bad.timestamp.verifying",
                "\u6B64 jar \u5305\u542B\u5E26\u6709\u65E0\u6548\u65F6\u95F4\u6233\u7684\u7B7E\u540D\u3002\u5982\u679C\u6CA1\u6709\u6709\u6548\u65F6\u95F4\u6233, \u5219\u5728\u5176\u4E2D\u4EFB\u4E00\u7B7E\u540D\u8005\u8BC1\u4E66\u5230\u671F (\u6700\u65E9\u4E3A %1$tY-%1$tm-%1$td) \u4E4B\u540E, \u7528\u6237\u53EF\u80FD\u65E0\u6CD5\u9A8C\u8BC1\u6B64 jar\u3002\n\u6709\u5173\u8BE6\u7EC6\u4FE1\u606F, \u8BF7\u4F7F\u7528 -J-Djava.security.debug=jar \u91CD\u65B0\u8FD0\u884C jarsigner\u3002"},
        {"The.signer.certificate.will.expire.on.1.",
                "\u7B7E\u540D\u8005\u8BC1\u4E66\u5C06\u4E8E %1$tY-%1$tm-%1$td \u5230\u671F\u3002"},
        {"The.timestamp.will.expire.on.1.",
                "\u65F6\u95F4\u6233\u5C06\u4E8E %1$tY-%1$tm-%1$td \u5230\u671F\u3002"},
        {"signer.cert.expired.1.but.timestamp.good.2.",
                "\u7B7E\u540D\u8005\u8BC1\u4E66\u5230\u671F\u65E5\u671F\u4E3A %1$tY-%1$tm-%1$td\u3002\u4E0D\u8FC7\uFF0C\u5728\u65F6\u95F4\u6233\u4E8E %2$tY-%2$tm-%2$td \u5230\u671F\u4E4B\u524D\uFF0CJAR \u5C06\u6709\u6548\u3002"},
        {"Unknown.password.type.", "\u672A\u77E5\u53E3\u4EE4\u7C7B\u578B: "},
        {"Cannot.find.environment.variable.",
                "\u627E\u4E0D\u5230\u73AF\u5883\u53D8\u91CF: "},
        {"Cannot.find.file.", "\u627E\u4E0D\u5230\u6587\u4EF6: "},
    };

    /**
     * Returns the contents of this <code>ResourceBundle</code>.
     *
     * <p>
     *
     * @return the contents of this <code>ResourceBundle</code>.
     */
    @Override
    public Object[][] getContents() {
        return contents;
    }
}
