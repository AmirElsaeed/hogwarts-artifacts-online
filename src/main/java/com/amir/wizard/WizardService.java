package com.amir.wizard;

import java.util.List;

import org.springframework.stereotype.Service;

import com.amir.artifact.Artifact;
import com.amir.artifact.ArtifactRepository;
import com.amir.system.exception.ObjectNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class WizardService {
	private final WizardRepository wizardRepository;
	private final ArtifactRepository artifactRepository;

	public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
		this.wizardRepository = wizardRepository;
		this.artifactRepository = artifactRepository;
	}
	
	public Wizard findById(Integer wizardId) {
		return this.wizardRepository.findById(wizardId)
				.orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
	}
	
	public List<Wizard> findAll() {
		return this.wizardRepository.findAll();
	}
	
	public Wizard save(Wizard newWizard) {
		return this.wizardRepository.save(newWizard);
	}
	
	public Wizard update(Integer wizardId, Wizard update) {
		return this.wizardRepository.findById(wizardId)
				.map(oldWizard -> {
					oldWizard.setName(update.getName());
					return this.wizardRepository.save(oldWizard);
				}).orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
	}
	
	public void delete(Integer wizardId) {
		Wizard wizardToBeDeleted = this.wizardRepository.findById(wizardId)
    		.orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
		
		// Before deletion, we will unassign this wizard's owned artifacts.
		wizardToBeDeleted.removeAllArtifacts();
		this.wizardRepository.deleteById(wizardId);
	}
	
	public void assignArtifact(Integer wizardId, String artifactId) {
		// find artifact by id from DB
		Artifact artifact = this.artifactRepository.findById(artifactId).orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
		
		// find wizard by id from DB
		Wizard wizard = this.wizardRepository.findById(wizardId).orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
		
		// artifact assignment
		// we need to see if artifact is already owned by some wizard
		if(artifact.getOwner() != null) {
			artifact.getOwner().removeArtifact(artifact);
		}
		
		wizard.addArtifact(artifact);
	}
}
