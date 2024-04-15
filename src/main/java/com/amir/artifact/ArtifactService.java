package com.amir.artifact;

import java.util.List;
import java.util.Map;

import com.amir.artifact.dto.ArtifactDto;
import com.amir.client.ai.chat.ChatClient;
import com.amir.client.ai.chat.dto.ChatRequest;
import com.amir.client.ai.chat.dto.ChatResponse;
import com.amir.client.ai.chat.dto.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.amir.system.exception.ObjectNotFoundException;
import com.amir.system.utils.IdWorker;

import jakarta.transaction.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class ArtifactService {
	
	private final ArtifactRepository artifactRepository;
	
	private final IdWorker idWorker;

	private final ChatClient chatClient;

	public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker, @Qualifier("openAIChatClient") ChatClient chatClient) {
		this.artifactRepository = artifactRepository;
		this.idWorker = idWorker;
		this.chatClient = chatClient;
	}
	
	public Artifact findById(String artifactId) {
		return this.artifactRepository.findById(artifactId)
				.orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
	}

	// used in metrics
	@Timed("findAllArtifactsService.time")
	public List<Artifact> findAll() {
		return this.artifactRepository.findAll();
	}
	
	public Artifact save(Artifact newArtifact) {
		newArtifact.setId(idWorker.nextId() + "");
		return this.artifactRepository.save(newArtifact);
	}
	
	public Artifact update(String artifactId, Artifact update) {
		return this.artifactRepository.findById(artifactId)
				.map(oldArtifact -> {
					oldArtifact.setName(update.getName());
					oldArtifact.setDescription(update.getDescription());
					oldArtifact.setImgUrl(update.getImgUrl());
					return this.artifactRepository.save(oldArtifact);
				}).orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
	}
	
	public void delete(String artifactId) {
		this.artifactRepository.findById(artifactId)
							   .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
		this.artifactRepository.deleteById(artifactId);
	}

	public String summarize(List<ArtifactDto> artifactDtos) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonArray = objectMapper.writeValueAsString(artifactDtos);

		// prepare messages for summarizing.
		List<Message> messages = List.of(
				new Message("system", "generate short summary for the given json array, don't include json array"),
				new Message("user", jsonArray)
		);

		ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo", messages);
		ChatResponse chatResponse = this.chatClient.generate(chatRequest);

		return chatResponse.choises().get(0).message().content();
	}

    public Page<Artifact> findAll(Pageable pageable) {
		return this.artifactRepository.findAll(pageable);
    }

    public Page<Artifact> findByCriteria(Map<String, String> searchCriteria, Pageable pageable) {
		// create base specification to start with
		Specification<Artifact> spec = Specification.where(null);

		if(StringUtils.hasLength(searchCriteria.get("id"))) {
			spec = spec.and(ArtifactSpecs.hasId(searchCriteria.get("id")));
		}

		if(StringUtils.hasLength(searchCriteria.get("name"))) {
			spec = spec.and(ArtifactSpecs.containsName(searchCriteria.get("name")));
		}

		if(StringUtils.hasLength(searchCriteria.get("description"))) {
			spec = spec.and(ArtifactSpecs.containsDescription(searchCriteria.get("description")));
		}

		if(StringUtils.hasLength(searchCriteria.get("ownerName"))) {
			spec = spec.and(ArtifactSpecs.hasOwnerName(searchCriteria.get("ownerName")));
		}

		return this.artifactRepository.findAll(spec, pageable);
    }
}
