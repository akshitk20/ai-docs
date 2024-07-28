package com.springboot.ai_docs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.core.io.Resource;
import org.springframework.shell.command.annotation.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command
public class SpringAssistantCommand {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    @Value("classpath:/prompts/spring-boot-reference.st") // taking prompt from external resource
    private Resource resource;

    public SpringAssistantCommand(ChatModel chatModel, VectorStore vectorStore) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new PromptChatMemoryAdvisor(new InMemoryChatMemory())) // advisor -> intercepts the request and modifies it. this is used to preserve the state of request
                .build();
        this.vectorStore = vectorStore;
    }

    @Command(command = "q")
    public String question(@DefaultValue(value = "What is spring boot") String message) {
        PromptTemplate promptTemplate = new PromptTemplate(resource);

        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", message);
        promptParameters.put("documents", String.join("\n", findSimilarDocuments(message)));

        Prompt prompt = promptTemplate.create(promptParameters);

        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    private List<String> findSimilarDocuments(String message) {
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(2));
        return documents.stream().map(Document::getContent).toList();
    }
}
