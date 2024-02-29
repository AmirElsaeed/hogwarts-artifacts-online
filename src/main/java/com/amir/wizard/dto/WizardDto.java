package com.amir.wizard.dto;

import jakarta.validation.constraints.NotEmpty;

public record WizardDto(Integer id,
						@NotEmpty(message = "can't be empty") String name, 
						Integer numberOFArtifacts) {

}
