package com.zettamine.mi.servicesImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.zettamine.mi.entities.Plant;
import com.zettamine.mi.repositories.PlantRepository;
import com.zettamine.mi.services.PlantService;
import com.zettamine.mi.utils.StringUtil;

@Service
public class PlantServceImpl implements PlantService {
	private PlantRepository plantRepository;

	public PlantServceImpl(PlantRepository plantReposotory) {
		super();
		this.plantRepository = plantReposotory;
	}

	@Override
	public boolean addNewPlant(Plant plant) {
		Optional<Plant> optPlantId = plantRepository.findById(plant.getPlantId());
		Optional<Plant> optPlantName = plantRepository.findByPlantName(plant.getPlantName());

		if (optPlantId.isPresent() || optPlantName.isPresent()) {
			return false;
		}

		plant.setPlantId(StringUtil.removeAllSpaces(plant.getPlantId()).toUpperCase());

		plant.setLocation(StringUtil.removeExtraSpaces(plant.getLocation()).toUpperCase());

		plant.setPlantName(StringUtil.removeExtraSpaces(plant.getPlantName()).toUpperCase());

		plantRepository.save(plant);
		return true;
	}

	@Override
	public List<Plant> getAllPlants() {
		List<Plant> plantsList = plantRepository.findAll();
		return plantsList;
	}

	@Override
	public Plant getPlant(String id) {
		Optional<Plant> optPlant = plantRepository.findById(id.toUpperCase());
		if (optPlant.isEmpty()) {
			return null;
		}
		Plant plant = optPlant.get();
		return plant;
	}

	@Override
	public boolean saveEditedPlant(Plant plant) {

		plant.setPlantId(StringUtil.removeExtraSpaces(plant.getPlantId()).toUpperCase());
		
		System.out.println(plant.getPlantId());
		
		plant.setLocation(StringUtil.removeExtraSpaces(plant.getLocation()).toUpperCase());

		plant.setPlantName(StringUtil.removeExtraSpaces(plant.getPlantName()).toUpperCase());

		Plant savedPlant = plantRepository.save(plant);
		if (savedPlant.getPlantId() != "") {
			return true;
		}
		return false;
	}

	@Override
	public boolean deletePlant(String plantId) {
		Plant plant = getPlant(plantId);

		if (plant == null) {
			return false;
		}
		plant.setStatus(false);
		plantRepository.save(plant);
		return true;
	}

}
