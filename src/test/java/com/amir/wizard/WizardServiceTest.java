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
import com.amir.system.exception.ObjectNotFoundException;

@ExtendWith(MockitoExtension.class)
class WizardServiceTest {
	
	@Mock
	WizardRepository wizardRepository;
	
	@InjectMocks
	WizardService wizardService;
	
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

}
