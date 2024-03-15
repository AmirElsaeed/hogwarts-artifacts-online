package com.amir.wizard;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.amir.artifact.Artifact;
import com.amir.system.StatusCode;
import com.amir.system.exception.ObjectNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "dev")
class WizardControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	WizardService wizardService;
	
	List<Wizard> wizards;
	
	@Value("${api.endpoint.base-url}")
	String baseUrl;
	
	@BeforeEach
	void setUp() throws Exception {
		this.wizards = new ArrayList<>();
		Wizard wizard = new Wizard();
		wizard.setId(1);
		wizard.setName("Albus Dumbledore");
		
		Artifact a = new Artifact();
		a.setId("1250808601744904192");
		a.setName("Invisibility Cloak");
		a.setDescription("An invisibility cloak is used to make the wearer invisible.");
		a.setImgUrl("ImageUrl");
		wizard.addArtifact(a);
		wizards.add(wizard);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testFindWizardByIdSuccess() throws Exception {
		// Given
		given(this.wizardService.findById(1)).willReturn(this.wizards.get(0));
		
		// When and Then
		this.mockMvc.perform(get(this.baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.flag").value(true))
		.andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
		.andExpect(jsonPath("$.message").value("Find One Success"))
		.andExpect(jsonPath("$.data.id").value(1))
		.andExpect(jsonPath("$.data.name").value("Albus Dumbledore"))
		.andExpect(jsonPath("$.data.numberOFArtifacts").value(1));
	}
	
	@Test
	void testFindWizardByIdNotFound() throws Exception {
		// Given
		given(this.wizardService.findById(1)).willThrow(new ObjectNotFoundException("wizard", 1));
		
		// When and Then
		this.mockMvc.perform(get(this.baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.flag").value(false))
		.andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
		.andExpect(jsonPath("$.message").value("Could not find wizard with Id 1"))
		.andExpect(jsonPath("$.data").isEmpty());
	}
	
	@Test
	void testFindWizardByIdArgumentTypeMismatch() throws Exception {
		// Given
		given(this.wizardService.findById(Mockito.any())).willThrow(MethodArgumentTypeMismatchException.class);
		
		// When and Then
		this.mockMvc.perform(get(this.baseUrl + "/wizards/x").accept(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.flag").value(false))
		.andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
		.andExpect(jsonPath("$.message").value("'wizardId' should be a valid 'Integer' and 'x' isn't"))
		.andExpect(jsonPath("$.data").isEmpty());
	}
}
