package com.amir.artifact.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.amir.artifact.Artifact;
import com.amir.artifact.dto.ArtifactDto;
import com.amir.wizard.converter.WizardToWizardDtoConverter;

@Component
public class ArtifactToArtifactDtoConverter implements Converter<Artifact, ArtifactDto> {

	private final WizardToWizardDtoConverter wizardToWizardDtoConverter;

	public ArtifactToArtifactDtoConverter(WizardToWizardDtoConverter wizardToWizardDtoConverter) {
		this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
	}

	@Override
	public ArtifactDto convert(Artifact source) {
		ArtifactDto artifactDto = new ArtifactDto(source.getId(), source.getName(), source.getDescription(),
				source.getImgUrl(),
				source.getOwner() != null ? this.wizardToWizardDtoConverter.convert(source.getOwner()) : null);
		return artifactDto;
	}

}
