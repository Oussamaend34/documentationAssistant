package com.ensah.assistant;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReferenceDocLoader {
    
    private final JdbcClient jdbcClient;
    private final VectorStore vectorStore;

    @Value ("classpath:/docs/spring-boot-reference.pdf")
    
    private Resource pdfResource;

    @PostConstruct
    public void init() {
        Integer count = jdbcClient.sql("SELECT COUNT(*) from vector_store")
            .query(Integer.class)
            .single();
        log.info("Current count of the Vector Store: {}", count);
        if (count == 0) {
            log.info("loading Spring Boot Reference PDF into Vector Storage");
            PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                    .withNumberOfTopTextLinesToDelete(0)
                    .build()
                )
                .withPagesPerDocument(1)
                .build();
            PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(pdfResource, pdfDocumentReaderConfig);
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> documents = pagePdfDocumentReader.read();
            List<Document> splittedDocuments = textSplitter.apply(documents);
            for (Document document : splittedDocuments) {
                vectorStore.add(List.of(document));
            }
            log.info("Spring Boot Reference PDF loaded into Vector Storage");
        }
    }



}
