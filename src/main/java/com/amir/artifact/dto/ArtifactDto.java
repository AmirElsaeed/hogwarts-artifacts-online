package com.amir.artifact.dto;

import com.amir.wizard.dto.WizardDto;

import jakarta.validation.constraints.NotEmpty;

public record ArtifactDto(String id,
						  @NotEmpty String name, //@Pattern @Length
						  @NotEmpty String description,
						  @NotEmpty(message = "can't be empty") String imgUrl,
                          WizardDto owner) {

}
