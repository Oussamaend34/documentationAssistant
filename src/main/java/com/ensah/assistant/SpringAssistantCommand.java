package com.ensah.assistant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.core.io.Resource;
import org.springframework.shell.command.annotation.Command;

@Command
public class SpringAssistantCommand {

    private final ChatClient chatClient;

    private final VectorStore vectorStore;

    @Value("classpath:prompts/spring-boot-reference-rag-prompt.st")
    private Resource ragPromptResource;
    
    public SpringAssistantCommand(ChatClient.Builder builder, VectorStore vectorStore) {
        chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @Command(command = "q")
    public String askQuestion(@DefaultValue(value = "What is Spring Boot?") String question) {
        PromptTemplate promptTemplate = new PromptTemplate(ragPromptResource);
        Map<String, Object> context = new HashMap<>();
        List<String> contentList = findSmilarDocuments(question);
        context.put("question", question);
        context.put("documents", String.join("\n", contentList));
        Prompt prompt = promptTemplate.create(context);
        return chatClient.prompt(prompt).call().content();
    }

    private List<String> findSmilarDocuments(String question) {
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest
                        .builder()
                        .topK(3)
                        .query(question)
                        .build()
        );
        if (similarDocuments == null) {
            return List.of();
        } else{
            List<String> contentList = similarDocuments.stream().map(Document::getText).toList();
            return contentList;
        }
    }
}
