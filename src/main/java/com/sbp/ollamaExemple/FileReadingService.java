package com.sbp.ollamaExemple;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Service
public class FileReadingService {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;


    @Autowired
    public FileReadingService(@Qualifier("webApplicationContext")ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    public String readInternalFileAsString(String filename) {

        StringBuilder content = new StringBuilder();
        Resource resource = getInternalResource(filename) ;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }


    public List<String> readInternalFileAsList(String filename) {

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(getInternalResource(filename).getFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public List<String> findAllFilesInInternalFolder(String folderName) {

        List<String> filesNames = new ArrayList<>();

        try{
            File[] listOfFiles = getInternalResource(folderName).getFile().listFiles();

            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        filesNames.add(file.getName()) ;
                    }
                }
            } else {
                System.out.println("The folder is empty or does not exist.");
            }
        } catch (IOException e) {
            System.out.println("- WARNING Impossible to find the folder: " + folderName + " WARNING -");
        }

        return filesNames ;

    }

    private Resource getInternalResource(String fileName) {
        return resourceLoader.getResource("classpath:" + fileName);
    }

    public <T> T readJson(String filename, Class<T> toMapClass) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filename);
        return objectMapper.readValue(resource.getFile(), toMapClass);
    }

    public Map<String,String> readJsonAsMap(String filename) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filename);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> mappedJson = new HashMap<>();
        try {
            mappedJson = mapper.readValue(resource.getFile(), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mappedJson;
    }

}