package com.sbp.ollamaExemple.service;

import com.sbp.ollamaExemple.repository.ChatResponseRepository;
import com.sbp.ollamaExemple.entity.ChatResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.sbp.ollamaExemple.FileReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class ChatService {
    @Autowired
    private ChatResponseRepository chatResponseRepository;

    private final FileReadingService fileReadingService;
    private final OllamaChatModel chatModel;

    private final List<Message> conversationHistory = new ArrayList<>();

    public String askDoraAQuestion(String question){
        String prompt = fileReadingService.readInternalFileAsString("prompts/promptDora.txt");

        if (conversationHistory.isEmpty()) {
            conversationHistory.add(new SystemMessage("<start_of_turn>" + prompt + "<end_of_turn>"));
        }

        conversationHistory.add(new UserMessage("<start_of_turn>" + question + "<end_of_turn>"));

        Prompt promptToSend = new Prompt(new ArrayList<>(conversationHistory));
        Flux<ChatResponse> chatResponses = chatModel.stream(promptToSend);

        String response = chatResponses.collectList().block().stream()
                .map(chatResponse -> chatResponse.getResult().getOutput().getContent())
                .collect(Collectors.joining(""));

        conversationHistory.add(new SystemMessage("<start_of_turn>" + response + "<end_of_turn>"));

        // Enregistrer dans la base de données
        ChatResponseEntity entity = new ChatResponseEntity();
        entity.setQuestion(question);
        entity.setResponse(response);
        chatResponseRepository.save(entity);

        return response;
    }
}
