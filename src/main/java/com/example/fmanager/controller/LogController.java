package com.example.fmanager.controller;

import com.example.fmanager.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@Tag(name = "Log Controller", description = "API for logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @Operation(summary = "Get logs for the specified date",
            description = "Creates and returns a file with logs for the specified date.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File successfully created and returned"),
        @ApiResponse(responseCode = "204", description = "No logs found for the specified date"),
        @ApiResponse(responseCode = "404", description = "File application.log not found"),
        @ApiResponse(responseCode = "500", description = "Server error processing request")
    })
    @GetMapping("/{date}")
    public ResponseEntity<Resource> getLogsByDate(@PathVariable String date) {
        try {
            String logFilePath = logService.generateLogFileForDate(date);
            Path filePath = Paths.get(logFilePath);
            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + filePath.getFileName().toString())
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
