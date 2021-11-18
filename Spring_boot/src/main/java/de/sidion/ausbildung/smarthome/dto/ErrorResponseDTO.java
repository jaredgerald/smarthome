package de.sidion.ausbildung.smarthome.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Value
@AllArgsConstructor
public class ErrorResponseDTO {

    LocalDateTime time;
    int status;
    String error;
    String message;
    String details;

    public ErrorResponseDTO(LocalDateTime time, Exception ex, HttpStatus status, String details) {
        this.time = time;
        this.status = status.value();
        this.error = status.name();
        this.message = ex.getMessage();
        this.details = details;
    }
}
