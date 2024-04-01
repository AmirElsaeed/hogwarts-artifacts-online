package com.amir.artifact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.amir.system.exception.ObjectNotFoundException;
import com.amir.system.utils.IdWorker;
import com.amir.wizard.Wizard;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = "dev")
class ArtifactServiceTest {

	@Mock
	ArtifactRepository artifactRepository;

	@Mock
	IdWorker idWorker;
	
	@InjectMocks
	ArtifactService artifactService;
	
	final List<Artifact> artifacts = new ArrayList<>();
	
	@BeforeEach
	void setUp() throws Exception {
		Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImgUrl("ImageUrl");
        this.artifacts.add(a1);

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a2.setImgUrl("ImageUrl");
        this.artifacts.add(a2);
	}

	@Test
	void testFindByIdSuccess() {
		// Given
		Artifact a = new Artifact();
		a.setId("1250808601744904192");
		a.setName("Invisibility Cloak");
		a.setDescription("An invisibility cloak is used to make the wearer invisible.");
		a.setImgUrl("ImageUrl");

		Wizard w = new Wizard();
		w.setId(2);
		w.setName("Harry Potter");

		a.setOwner(w);

		given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));

		// When
		Artifact returnedArtifact = artifactService.findById("1250808601744904192");

		// Then
		assertThat(returnedArtifact.getId()).isEqualTo(a.getId());
		assertThat(returnedArtifact.getName()).isEqualTo(a.getName());
		assertThat(returnedArtifact.getDescription()).isEqualTo(a.getDescription());
		assertThat(returnedArtifact.getImgUrl()).isEqualTo(a.getImgUrl());
		verify(artifactRepository, times(1)).findById("1250808601744904192");
	}

	@Test
	void testFindByIdNotFound() {
		String id = "1250808601744904191";

		// Given
		given(artifactRepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

		// When
		Throwable thrown = catchThrowable(() -> {
			Artifact returnedArtifact = artifactService.findById(id);
		});

		// Then
		assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
				.hasMessage("Could not find artifact with Id " + id);
		verify(artifactRepository, times(1)).findById(id);
	}
	
	@Test
	void testFindAllSuccess() {
		// Given
		given(artifactRepository.findAll()).willReturn(this.artifacts);
		
		// When
		List<Artifact> actualArtifacts = artifactService.findAll();
		
		// Then
		assertThat(actualArtifacts.size()).isEqualTo(this.artifacts.size());
		verify(artifactRepository, times(1)).findAll();
	}
	
	@Test
	void testSaveSuccess() {
		//Given
		Artifact newArtifact = new Artifact();
		newArtifact.setName("Name");
		newArtifact.setDescription("Description...");
		newArtifact.setImgUrl("ImageUrl");
		
		given(idWorker.nextId()).willReturn(123456L);
		given(artifactRepository.save(newArtifact)).willReturn(newArtifact);
		
		// When
		Artifact savedArtifact = artifactService.save(newArtifact);
		
		// Then
		assertThat(savedArtifact.getId()).isEqualTo("123456");
		assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
		assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
		assertThat(savedArtifact.getImgUrl()).isEqualTo(newArtifact.getImgUrl());
		verify(artifactRepository, times(1)).save(newArtifact);
	}
	
	@Test
	void testUpdateSuccess() {
		// Given
		// simulate record in database
		Artifact oldArtifact = new Artifact();
		oldArtifact.setId("1250808601744904192");
		oldArtifact.setName("Invisibility Cloak");
		oldArtifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
		oldArtifact.setImgUrl("ImageUrl");        
        
		// simulate requestBody in front-end
		Artifact update = new Artifact();
//		update.setId("1250808601744904192");
		update.setName("Invisibility Cloak");
		update.setDescription("A new description...");
		update.setImgUrl("ImageUrl");

		given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(oldArtifact));
		given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);
		
		// When
		Artifact updatedArtifact = artifactService.update("1250808601744904192", update);
		
		// Then
		assertThat(updatedArtifact.getId()).isEqualTo("1250808601744904192");
		assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());
		verify(artifactRepository, times(1)).findById("1250808601744904192");
		verify(artifactRepository, times(1)).save(oldArtifact);
	}
	
	@Test
	void testUpdateNotFound() {
		// Given
		Artifact update = new Artifact();
		update.setName("Invisibility Cloak");
		update.setDescription("A new description...");
		update.setImgUrl("ImageUrl");
		
		given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());
		
		// When
		assertThrows(ObjectNotFoundException.class, () -> {
			artifactService.update("1250808601744904192", update);
		});
		
		// Then
		verify(artifactRepository, times(1)).findById("1250808601744904192");
	}
	
	@Test
	void testDeleteSuccess() {
		// Given
		Artifact a = new Artifact();
		a.setId("1250808601744904192");
		a.setName("Invisibility Cloak");
		a.setDescription("An invisibility cloak is used to make the wearer invisible.");
		a.setImgUrl("ImageUrl");
		
		given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));
		doNothing().when(artifactRepository).deleteById("1250808601744904192");
		
		// When
		artifactService.delete("1250808601744904192");
		
		// Then
		verify(artifactRepository, times(1)).deleteById("1250808601744904192");
	}
	
	@Test
	void testDeleteNotFound() {
		//Given
		given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());
		
		// When
		assertThrows(ObjectNotFoundException.class, () -> {
			artifactService.delete("1250808601744904192");
		});
		
		// Then
		verify(artifactRepository, times(1)).findById("1250808601744904192");
	}
}
