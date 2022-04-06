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


        //prova di rendering con altra libreria
//        Path path = Paths.get("/home/nunzio/Documenti/Progetti/tutorials/pdf/src/main/resources/output.txt");
//        byte[] bytes = html.getBytes();
//        Files.write(path, bytes);
//        File file = new File(path.toString());

        Document doc = createWellFormedHtml(new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)));
//        Document doc = createWellFormedHtml(new ByteArrayInputStream(html.getBytes()));
        xhtmlToPdf(doc, PDF_OUTPUT);


//        thymeleaf2Pdf.generatePdfFromHtml(html);
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

    private String parseThymeleafTemplate() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("to", "Baeldung.com");

        return templateEngine.process("preventivo", context);
    }


//    private void generateHtmlToPdf() throws IOException {
//        File inputHTML = new File(HTML_INPUT);
//        Document doc = createWellFormedHtml(inputHTML);
//        xhtmlToPdf(doc, PDF_OUTPUT);
//    }

//    private static Document createWellFormedHtml(File inputHTML) throws IOException {
//        Document document = Jsoup.parse(inputHTML, "UTF-8");
//        document.outputSettings()
//                .syntax(Document.OutputSettings.Syntax.xml);
//        return document;
//    }

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


}
