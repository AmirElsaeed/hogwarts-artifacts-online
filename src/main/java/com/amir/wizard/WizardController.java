package com.amir.wizard;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amir.system.Result;
import com.amir.system.StatusCode;
import com.amir.wizard.converter.WizardDtoToWizardConverter;
import com.amir.wizard.converter.WizardToWizardDtoConverter;
import com.amir.wizard.dto.WizardDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.endpoint.base-url}/wizards")
public class WizardController {
	private final WizardService wizardService;
	private final WizardToWizardDtoConverter wizardToWizardDtoConverter;
	private final WizardDtoToWizardConverter wizardDtoToWizardConverter; 

	public WizardController(WizardService wizardService, WizardToWizardDtoConverter wizardToWizardDtoConverter, WizardDtoToWizardConverter wizardDtoToWizardConverter) {		
		this.wizardService = wizardService;
		this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
		this.wizardDtoToWizardConverter = wizardDtoToWizardConverter;
	}

	@GetMapping("/{wizardId}")
	public Result findWizardById(@PathVariable Integer wizardId) {
		Wizard wizard = this.wizardService.findById(wizardId);
		WizardDto wizardDto = this.wizardToWizardDtoConverter.convert(wizard);
		return new Result(true, StatusCode.SUCCESS, "Find One Success", wizardDto);
	}
	
	@GetMapping
	public Result findAllWizards() {
		List<Wizard> foundWizards = this.wizardService.findAll();
		List<WizardDto> wizardDtos = foundWizards.stream()
//								  				 .map(foundWizard -> this.wizardToWizardDtoConverter.convert(foundWizard))
												 .map(this.wizardToWizardDtoConverter::convert)
												 .collect(Collectors.toList());
		return new Result(true, StatusCode.SUCCESS, "Find All Success", wizardDtos);
	}
	
	@PostMapping
	public Result addWizard(@Valid @RequestBody WizardDto wizardDto) {
		Wizard newWizard = this.wizardDtoToWizardConverter.convert(wizardDto);
		Wizard savedWizard = this.wizardService.save(newWizard);
		WizardDto saveWizardDto = this.wizardToWizardDtoConverter.convert(savedWizard);
		return new Result(true, StatusCode.SUCCESS, "Add Success", saveWizardDto);
	}
	
	@PutMapping("/{wizardId}")
	public Result updateWizard(@PathVariable Integer wizardId, @Valid @RequestBody WizardDto wizardDto) {
		Wizard update = this.wizardDtoToWizardConverter.convert(wizardDto);
		Wizard updatedWizard = this.wizardService.update(wizardId, update);
		WizardDto updateWizardDto = this.wizardToWizardDtoConverter.convert(updatedWizard);
		return new Result(true, StatusCode.SUCCESS, "Update Success", updateWizardDto);
	}
	
	@DeleteMapping("/{wizardId}")
    public Result deleteWizard(@PathVariable Integer wizardId) {
        this.wizardService.delete(wizardId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }
}
