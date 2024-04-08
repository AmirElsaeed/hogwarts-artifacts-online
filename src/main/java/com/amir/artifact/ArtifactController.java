package com.amir.artifact;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amir.artifact.converter.ArtifactDtoToArtifactConverter;
import com.amir.artifact.converter.ArtifactToArtifactDtoConverter;
import com.amir.artifact.dto.ArtifactDto;
import com.amir.system.Result;
import com.amir.system.StatusCode;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController {

	private final ArtifactService artifactService;
	private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;
	private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;
	private final MeterRegistry meterRegistry;

	public ArtifactController(ArtifactService artifactService,
							  ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter,
							  ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter, MeterRegistry meterRegistry) {
		this.artifactService = artifactService;
		this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
		this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
		this.meterRegistry = meterRegistry;
	}

	@GetMapping( "/{artifactId:^[a-zA-Z0-9]*$}")
	public Result findArtifactById(@PathVariable String artifactId) {
		Artifact foundArtifact = this.artifactService.findById(artifactId);
		meterRegistry.counter("artifact.id."+artifactId).increment();
		// to fix StackOverflow caused by bidirectional between owner and artifact
		ArtifactDto artifactDto = artifactToArtifactDtoConverter.convert(foundArtifact); 
		return new Result(true, StatusCode.SUCCESS, "Find One Success", artifactDto);
	}
	
	@GetMapping
	public Result findAllArtifacts(Pageable pageable) {
		Page<Artifact> artifactPage = this.artifactService.findAll(pageable);
		Page<ArtifactDto> artifactDtoPage = artifactPage
//										.map(foundArtifact -> this.artifactToArtifactDtoConverter.convert(foundArtifact))
									    .map(this.artifactToArtifactDtoConverter::convert);
		return new Result(true, StatusCode.SUCCESS, "Find All Success", artifactDtoPage);
		
	}
	
	@PostMapping
	public Result addArtifact(@Valid @RequestBody ArtifactDto artifactDto) {
		Artifact newArtifact = this.artifactDtoToArtifactConverter.convert(artifactDto);
		Artifact savedArtifact = this.artifactService.save(newArtifact);
		ArtifactDto savedArtifactDto = this.artifactToArtifactDtoConverter.convert(savedArtifact);
		return new Result(true, StatusCode.SUCCESS, "Add Success", savedArtifactDto);
	}
	
	
	@PutMapping("/{artifactId}")
	public Result updateArtifact(@PathVariable String artifactId, @Valid @RequestBody ArtifactDto artifactDto)  {
		Artifact update = this.artifactDtoToArtifactConverter.convert(artifactDto);
		Artifact updatedArtifact = this.artifactService.update(artifactId, update);
		ArtifactDto updatedArtifactDto = this.artifactToArtifactDtoConverter.convert(updatedArtifact);
		return new Result(true, StatusCode.SUCCESS, "Update Success", updatedArtifactDto);
	}
	
	@DeleteMapping("/{artifactId}")
	public Result deleteArtifact(@PathVariable String artifactId) {
		this.artifactService.delete(artifactId);
		return new Result(true, StatusCode.SUCCESS, "Delete Success");
	}

	@GetMapping("/summary")
	public Result summarizeArtifacts() throws JsonProcessingException {
		List<Artifact> foundArtifacts = this.artifactService.findAll();
		List<ArtifactDto> artifactDtos = foundArtifacts.stream()
//													   .map(foundArtifact -> this.artifactToArtifactDtoConverter.convert(foundArtifact))
				           							   .map(this.artifactToArtifactDtoConverter::convert)
				                                       .collect(Collectors.toList());
		String artifactSummary = this.artifactService.summarize(artifactDtos);
		return new Result(true, StatusCode.SUCCESS, "Summarize Success" ,artifactSummary);
	}
}
