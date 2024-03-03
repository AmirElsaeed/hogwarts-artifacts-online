package com.amir.wizard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amir.artifact.Artifact;
import com.amir.artifact.ArtifactRepository;
import com.amir.artifact.ArtifactService;
import com.amir.system.exception.ObjectNotFoundException;

@ExtendWith(MockitoExtension.class)
class WizardServiceTest {
	
	@Mock
	WizardRepository wizardRepository;
	
	@Mock
	ArtifactRepository artifactRepository;
	
	@InjectMocks
	WizardService wizardService;
	
	@InjectMocks
	ArtifactService artifactService;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testFindByIdSuccess() {
		// Given
		Wizard wizard = new Wizard();
		wizard.setId(1);
		wizard.setName("Albus Dumbledore");
		
		Artifact a = new Artifact();
		a.setId("1250808601744904192");
		a.setName("Invisibility Cloak");
		a.setDescription("An invisibility cloak is used to make the wearer invisible.");
		a.setImgUrl("ImageUrl");
		
		wizard.addArtifact(a);
		
		given(this.wizardRepository.findById(1)).willReturn(Optional.of(wizard));
		
		// When
		Wizard returnedwizard = this.wizardService.findById(1);
		
		// Then
		assertThat(returnedwizard.getId()).isEqualTo(wizard.getId());
		assertThat(returnedwizard.getName()).isEqualTo(wizard.getName());
		assertThat(returnedwizard.getNumberOfArtifacts()).isEqualTo(wizard.getNumberOfArtifacts());
		verify(wizardRepository, times(1)).findById(1);
	}
	
	@Test
	void testFindByIdNotFound() {
		// Given
		given(this.wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());
		
		// When
		Throwable thrown = catchThrowable(() -> {
			Wizard returnedwizard = this.wizardService.findById(1);
		});
		
		// Then
		assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
		.hasMessage("Could not find wizard with Id 1");
		verify(wizardRepository, times(1)).findById(1);
	}
	
	@Test
	void testAssignArtifactSuccess() {
		// Given
		Artifact a = new Artifact();
		a.setId("1250808601744904192");
		a.setName("Invisibility Cloak");
		a.setDescription("An invisibility cloak is used to make the wearer invisible.");
		a.setImgUrl("ImageUrl");
		
		Wizard w2 = new Wizard();
		w2.setId(2);
		w2.setName("Harry Potter");
		w2.addArtifact(a);
		
		Wizard w3 = new Wizard();
		w3.setId(3);
		w3.setName("Neville Longbottom");
		w3.addArtifact(a);
		
		given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));
		given(this.wizardRepository.findById(3)).willReturn(Optional.of(w3));
		
		// When
		this.wizardService.assignArtifact(3, "1250808601744904192");
		
		// Then
		assertThat(a.getOwner().getId()).isEqualTo(3);
		assertThat(w3.getArtifacts()).contains(a);
		
	}

}
