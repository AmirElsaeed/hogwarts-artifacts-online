package com.amir.artifact.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.amir.artifact.Artifact;
import com.amir.artifact.dto.ArtifactDto;

@Component
public class ArtifactDtoToArtifactConverter implements Converter<ArtifactDto, Artifact>{

	@Override
	public Artifact convert(ArtifactDto source) {
		Artifact artifact = new Artifact();
		artifact.setId(source.id());
		artifact.setName(source.name());
		artifact.setDescription(source.description());
		artifact.setImgUrl(source.imgUrl());
		return artifact;
	}

}
