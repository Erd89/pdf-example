package com.example.pdfgenerator.pdfthymeleaf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;

public class PDFThymeleafExample {
    private static final String PDF_OUTPUT = "src/main/resources/misto.pdf";

    public static void main(String[] args) throws IOException {
        PDFThymeleafExample thymeleaf2Pdf = new PDFThymeleafExample();
        String html = thymeleaf2Pdf.parseThymeleafTemplate();

        Document doc = createWellFormedHtml(new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)));
        xhtmlToPdf(doc, PDF_OUTPUT);
        //thymeleaf2Pdf.generatePdfFromHtml(html);
    }

    private String parseThymeleafTemplate() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("to", "Baeldung.com");

        return templateEngine.process("quote", context);
    }

    private static Document createWellFormedHtml(InputStream inputHTML) throws IOException {
        Document document = Jsoup.parse(inputHTML, "UTF-8", "ciccio");
        document.outputSettings()
                .syntax(Document.OutputSettings.Syntax.xml);
        return document;
    }

    private static void xhtmlToPdf(Document doc, String outputPdf) throws IOException {
        try (OutputStream os = new FileOutputStream(outputPdf)) {
            String baseUri = FileSystems.getDefault()
                    .getPath("src/main/resources/")
                    .toUri()
                    .toString();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withUri(outputPdf);
            builder.toStream(os);
            builder.withW3cDocument(new W3CDom().fromJsoup(doc), baseUri);
            builder.run();
        }
    }

    //    public void generatePdfFromHtml(String html) throws IOException, DocumentException {
//        String outputFolder = System.getProperty("user.home") + File.separator + "thymeleaf.pdf";
//        OutputStream outputStream = new FileOutputStream(outputFolder);
//
//        ITextRenderer renderer = new ITextRenderer();
//        renderer.setDocumentFromString(html);
//        renderer.layout();
//        renderer.createPDF(outputStream);
//
//        outputStream.close();
//    }

}
