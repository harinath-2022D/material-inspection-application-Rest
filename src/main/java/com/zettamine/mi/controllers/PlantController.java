package com.zettamine.mi.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zettamine.mi.entities.Plant;
import com.zettamine.mi.services.PlantService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/plant")
@Tag(name = "Plant Controller", description = "responsible for adding plant related data")
public class PlantController {
	private PlantService plantService;

	private Logger LOG = LoggerFactory.getLogger(PlantController.class);

	public PlantController(PlantService plantService) {
		super();
		this.plantService = plantService;
	}

	@PostMapping("/save")
	public ResponseEntity<?> addNewPlant(@Valid @RequestBody Plant plant) {

		boolean isPlantAdded = plantService.addNewPlant(plant);
		if (isPlantAdded) {

			LOG.info("new plant added successfully | {}", plant);

			return new ResponseEntity<>("plant saved", HttpStatus.CREATED);
		} else {

			return new ResponseEntity<>("plant saving failed", HttpStatus.CONFLICT);
		}

	}

	@GetMapping("/{plantId}")
	public ResponseEntity<?> getPlantDetails(@PathVariable String plantId) {
		Plant plant = plantService.getPlant(plantId);
		if (plant != null) {
			return new ResponseEntity<>(plant, HttpStatus.OK);
		}

		return new ResponseEntity<>("No plant available with id : " + plantId, HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/plants")
	public ResponseEntity<?> getAllPlant() {
		List<Plant> plantsList = plantService.getAllPlants();
		return new ResponseEntity<>(plantsList, HttpStatus.OK);
	}

	@PutMapping("/edit")
	public ResponseEntity<?> updatePlantDetails(@Valid @RequestBody Plant plant) {
		boolean isPlantUpdated = plantService.saveEditedPlant(plant);
		if (isPlantUpdated) {
			return new ResponseEntity<>("plant details are updated", HttpStatus.OK);
		}else {
			return new ResponseEntity<>("updation failed", HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/delete/{plantId}")
	public ResponseEntity<?> deletePlant(@PathVariable String plantId) {

		boolean isPlantDeleted = plantService.deletePlant(plantId);
		if (isPlantDeleted) {
			return new ResponseEntity<>("plant deleted", HttpStatus.OK);
		}

		return new ResponseEntity<>("invalid plant id : " + plantId, HttpStatus.BAD_REQUEST);
	}

}
