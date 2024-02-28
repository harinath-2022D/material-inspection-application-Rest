package com.zettamine.mi.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zettamine.mi.entities.Material;
import com.zettamine.mi.entities.MaterialInspectionCharacteristics;
import com.zettamine.mi.requestdtos.MaterialCharDto;
import com.zettamine.mi.services.MaterialService;
import com.zettamine.mi.utils.ApplicationConstants;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/material")
@Tag(name = "Material Controller", description = "responsible for adding material related data")
public class MaterialController {

	private MaterialService materialService;

	private Logger LOG = LoggerFactory.getLogger(MaterialController.class);

	public MaterialController(MaterialService materialService) {

		this.materialService = materialService;

	}

	@PostMapping("/save")
	public ResponseEntity<?> addNewMaterial(@Valid @RequestBody Material material) {

		LOG.info("new material saving {}", material);
		
		Map<String, Object> response = new HashMap<>();

		boolean isMaterialSaved = materialService.addNewMaterial(material);

		if (isMaterialSaved) {

			LOG.info("new material succesfully saved");
			
			response.put(ApplicationConstants.MSG, ApplicationConstants.SUCCESS_MSG);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			LOG.info("new material adding failed due to duplicate id or description");
			response.put(ApplicationConstants.MSG, ApplicationConstants.FAIL_MSG);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

		}
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllActiveMaterials() {

		LOG.info("calling material service for all material list");
		Map<String, Object> response = new HashMap<>();

		List<Material> materialsList = materialService.getAllActiveMaterials();

		LOG.info("returing all active material list to view");
		
		response.put(ApplicationConstants.MSG, ApplicationConstants.SUCCESS_MSG);
		response.put(ApplicationConstants.DATA, materialsList);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getMaterial(@PathVariable String id) {

		Material material = materialService.getMaterial(id);
		Map<String, Object> response = new HashMap<>();
		if (material != null) {
			response.put(ApplicationConstants.MSG, ApplicationConstants.SUCCESS_MSG);
			response.put(ApplicationConstants.DATA, material);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.put(ApplicationConstants.MSG, ApplicationConstants.NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.resolve(404));
		}
	}

	@PutMapping("/edit")
	public ResponseEntity<?> editMaterialSave(@Valid @RequestBody Material material) {

		LOG.info("material updation saving {}", material);

		boolean result = materialService.saveEditMaterial(material);
		Map<String, Object> response = new HashMap<>();

		if (result) {

			LOG.info("material updation succesfully saved");
			response.put(ApplicationConstants.MSG, ApplicationConstants.SUCCESS_MSG);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		response.put(ApplicationConstants.MSG, ApplicationConstants.FAIL_MSG);
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}
	
	@DeleteMapping("/delete/{materialId}")
	public ResponseEntity<?> deleteVendor(@PathVariable String materialId) {

		boolean result = materialService.deleteMaterial(materialId);
		Map<String, Object> response = new HashMap<>();
		if (result) {
			response.put(ApplicationConstants.MSG, ApplicationConstants.SUCCESS_MSG);
			LOG.info("redirecting to  all material view form");

			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		response.put(ApplicationConstants.MSG, ApplicationConstants.FAIL_MSG);
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}
	
/* 
 * material characteristics section
 * 
 */
	@GetMapping("/view/char")
	public ResponseEntity<?> viewCharacteristics(@RequestParam String materialId) {

		LOG.info("calling material service for material characteristics if material id : {}", materialId);
		Map<String, Object> response = new HashMap<>();
		List<MaterialInspectionCharacteristics> list = materialService.getAllCharacteristicsOfMaterial(materialId);
		response.put(ApplicationConstants.MSG, ApplicationConstants.SUCCESS_MSG);
		response.put(ApplicationConstants.DATA, list);
		LOG.info("returning characteristics list of material id, {}", materialId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	

	@PostMapping("/save/char")
	public ResponseEntity<?> addMaterialCharacteristics(@Valid @RequestBody MaterialCharDto matChar) {

		LOG.info("new material characteristics adding for material id : {}", matChar.getMatId());

		boolean result = materialService.addNewMaterialCharacteristic(matChar);
		Map<String, Object> response = new HashMap<>();

		if (result) {
			response.put(ApplicationConstants.MSG, ApplicationConstants.SUCCESS_MSG);
			LOG.info("redirecting to material characteristics form");

			return new ResponseEntity<>(response,HttpStatus.OK);
		} else {
			LOG.info("saving failed | redirecting to material characteristics form");
			response.put(ApplicationConstants.MSG, ApplicationConstants.FAIL_MSG);
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}

	}

	@GetMapping("/rem/char/lot")
	public ResponseEntity<?> getLotCurrentCharacteristicsOfAssociatedMaterial(@RequestParam Integer id) {
		
		List<MaterialInspectionCharacteristics> characteristicsList = materialService.getMaterialCharByLotId(id);
		
		Map<String, Object> response = new HashMap<>();
		response.put(ApplicationConstants.MSG, ApplicationConstants.SUCCESS_MSG);
		if(characteristicsList.size() == 0) {
			response.put("info", "all characteristics are inspected");
		}else {
			response.put(ApplicationConstants.DATA, characteristicsList);
		}
		
		LOG.info("returing lot inspection characteristics needs to be added");

		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
	@PostMapping("/char/upload")
	public ResponseEntity<?> addMaterialCharacteristics(@RequestParam MultipartFile file) throws Exception{
		
		Map<String, Object> response = new HashMap<>();
		
		boolean isCharacteristicsAdded = materialService.addListOfCharacteristicsForMaterial(file);
		
		if(isCharacteristicsAdded) {
			response.put(ApplicationConstants.MSG, ApplicationConstants.SUCCESS_MSG);
		}else {
			response.put(ApplicationConstants.MSG, ApplicationConstants.FAIL_MSG);
		}
		
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
