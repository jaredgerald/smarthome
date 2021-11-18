package de.sidion.ausbildung.smarthome.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Data
@Getter
@Setter
public class MessageDTO {
    String topic;
    String message;
}
