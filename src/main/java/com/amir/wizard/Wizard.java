package com.amir.wizard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.amir.artifact.Artifact;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Wizard implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String name;
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "owner")
	private List<Artifact> artifacts = new ArrayList<>();

	public Wizard() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Artifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(List<Artifact> artifacts) {
		this.artifacts = artifacts;
	}
	
	public void addArtifact(Artifact artifact) {
		artifact.setOwner(this);
		this.artifacts.add(artifact);
	}
	
	public Integer getNumberOfArtifacts() {
        return this.artifacts.size();
    }

	public void removeAllArtifacts() {
        this.artifacts.stream().forEach(artifact -> artifact.setOwner(null));
        this.artifacts = new ArrayList<>();
    }

	public void removeArtifact(Artifact artifact) {
		// remove artifact owner
		artifact.setOwner(null);
		this.artifacts.remove(artifact); // because it's bi-directional 
	}
}
