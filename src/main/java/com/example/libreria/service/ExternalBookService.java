package com.example.libreria.service;

import com.example.libreria.dto.ExternalBookDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ExternalBookService {
    // TODO: completar llamada a la API externa (ver bien todo el proyecto...)
    
    private final RestTemplate restTemplate;
    
    @Value("${external.api.books.url}")
    private String externalApiUrl;
    
    public ExternalBookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public List<ExternalBookDTO> fetchAllBooks() {
        try {
            if (externalApiUrl == null || externalApiUrl.isEmpty()) {
                log.warn("External API URL is not configured, using fallback books data");
                return getFallbackBooks();
            }
            
            log.info("Fetching books from external API: {}", externalApiUrl);
            ResponseEntity<List<ExternalBookDTO>> response = restTemplate.exchange(
                    externalApiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ExternalBookDTO>>() {}
            );
            
            List<ExternalBookDTO> books = response.getBody();
            log.info("Successfully fetched {} books from external API", books != null ? books.size() : 0);
            return books != null ? books : Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Error fetching books from external API: {}, using fallback", e.getMessage());
            return getFallbackBooks();
        }
    }
    
    private List<ExternalBookDTO> getFallbackBooks() {
        ExternalBookDTO book1 = new ExternalBookDTO();
        book1.setId(258027L);
        book1.setTitle("The Lord of the Rings");
        book1.setAuthorName(List.of("J. R. R. Tolkien"));
        book1.setFirstPublishYear(1954);
        book1.setEditionCount(120);
        book1.setHasFulltext(true);
        book1.setPrice(new BigDecimal("15.99"));

        ExternalBookDTO book2 = new ExternalBookDTO();
        book2.setId(140081L);
        book2.setTitle("The Hitchhiker's Guide to the Galaxy");
        book2.setAuthorName(List.of("Douglas Adams"));
        book2.setFirstPublishYear(1979);
        book2.setEditionCount(85);
        book2.setHasFulltext(false);
        book2.setPrice(new BigDecimal("20.99"));

        ExternalBookDTO book3 = new ExternalBookDTO();
        book3.setId(90150L);
        book3.setTitle("One Hundred Years of Solitude");
        book3.setAuthorName(List.of("Gabriel García Márquez"));
        book3.setFirstPublishYear(1967);
        book3.setEditionCount(250);
        book3.setHasFulltext(true);
        book3.setPrice(new BigDecimal("22.99"));

        ExternalBookDTO book4 = new ExternalBookDTO();
        book4.setId(50012L);
        book4.setTitle("Pride and Prejudice");
        book4.setAuthorName(List.of("Jane Austen"));
        book4.setFirstPublishYear(1813);
        book4.setEditionCount(75);
        book4.setHasFulltext(true);
        book4.setPrice(new BigDecimal("12.99"));

        return List.of(book1, book2, book3, book4);
    }
    
    public ExternalBookDTO fetchBookById(Long id) {
        try {
            log.info("Fetching book with id {} from external API", id);
            String url = externalApiUrl + "/" + id;
            ExternalBookDTO book = restTemplate.getForObject(url, ExternalBookDTO.class);
            log.info("Successfully fetched book: {}", book != null ? book.getTitle() : "null");
            return book;
        } catch (RestClientException e) {
            log.error("Error fetching book {} from external API: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el libro de la API externa: " + e.getMessage(), e);
        }
    }
}

