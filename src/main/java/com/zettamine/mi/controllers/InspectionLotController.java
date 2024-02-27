package com.zettamine.mi.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zettamine.mi.entities.InspectionLot;
import com.zettamine.mi.requestdtos.DateRangeLotSearch;
import com.zettamine.mi.requestdtos.EditLotDto;
import com.zettamine.mi.requestdtos.LotActualDto;
import com.zettamine.mi.requestdtos.LotCreationDto;
import com.zettamine.mi.responsedtos.DateRangeLotResponse;
import com.zettamine.mi.responsedtos.LotActualsAndCharacteristics;
import com.zettamine.mi.servicesImpl.InspectionServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/insp")
public class InspectionLotController {
	private InspectionServiceImpl inspectionlotService;

	private Logger LOG = LoggerFactory.getLogger(InspectionLotController.class);

	public InspectionLotController(InspectionServiceImpl inspectionlotService) {
		super();
		this.inspectionlotService = inspectionlotService;
	}

	@PostMapping("/create/lot")
	public ResponseEntity<?> addInspectionLot(@Valid @RequestBody LotCreationDto lotDto) {

		boolean result = inspectionlotService.createInspectionLot(lotDto);
		Map<String, Object> response = new HashMap<>();
		if (result) {

			LOG.info("lot created successfully");
			response.put("message", "success");

			return new ResponseEntity<>(response,HttpStatus.OK);
		}

		LOG.info("lot creatioin failed");
		response.put("message", "fail");
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/lot/{id}")
	public ResponseEntity<?> fetchInspectionLotDetails(@PathVariable Integer id) {

		LOG.info("Searching lot having id : {}", id);
		Map<String, Object> response = new HashMap<>();
		InspectionLot inspLot = inspectionlotService.getLotDetails(id);
		
		if(inspLot == null) {
			response.put("message", "resource not found");
			return new ResponseEntity<>(response, HttpStatus.resolve(404));
		}

		LOG.info("Retunring lot details of id : {}", id);
		
		response.put("message", "success");
		response.put("data", inspLot);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/lot/actu")
	public ResponseEntity<?> getActualsAndCharacteristicsOfLot(@RequestParam Integer id) {

		LOG.info("Finding lot actuals and characteristics of lot id : {}", id);
		Map<String, Object> response = new HashMap<>();
		List<LotActualsAndCharacteristics> list = inspectionlotService.getActualAndOriginalOfLot(id);

		LOG.info("Returning list of lot actual and characteristics of lot id : {}", id);

		response.put("message", "success");
		response.put("data", list);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/save/lot/actu")
	public ResponseEntity<?> addInspectionActuals(@Valid @RequestBody LotActualDto actuals) {

		boolean response = inspectionlotService.saveInspActuals(actuals);
		Map<String, Object> resp = new HashMap<>();
		if (response) {
			
			resp.put("message", "success");
			LOG.info("new Inspection actual saved for lot id : {}", actuals.getLot());

			return new ResponseEntity<>(resp, HttpStatus.OK);
		} else {
			resp.put("message", "fail");
			LOG.info("Failed saving new inspection actual of lot id : {}", actuals.getLot());

			return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/lot/edit")
	public ResponseEntity<?> saveEditedLot(@Valid @RequestBody EditLotDto lot) {

		boolean result = inspectionlotService.updateInspectionLot(lot);
		Map<String, Object> resp = new HashMap<>();
		if (result) {
			resp.put("message", "success");
			
			LOG.info("Lot details are updated successfully lot id : {}", lot.getId());

			return new ResponseEntity<>(resp, HttpStatus.OK);

		} else {
			resp.put("message", "fail");
			LOG.info("Lot details are updated failed | lot id : {}", lot.getId());
			return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/lot/search")
	public ResponseEntity<?> DateRangeLotSearch(@Valid @RequestBody DateRangeLotSearch obj) {

		LOG.info("Searching lots ");
		Map<String, Object> response = new HashMap<>();
		List<DateRangeLotResponse> list = inspectionlotService.getAllLotsDetailsBetweenDateRange(obj);

		LOG.info("Returning lots having search criteria , size : {}", list.size());
		
		response.put("message", "success");
		response.put("data", list);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/*
	 * finding inspection lots whose inspection actuals not yet added
	 */
	@GetMapping("/lots")
	public ResponseEntity<?> getAllLotsWhoseInspectionActualNeedToAdded() {
		Map<String, Object> response = new HashMap<>();
		
		List<InspectionLot> list = inspectionlotService.getAllInspectionLots();
		
		response.put("message", "success");
		response.put("data", list);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/actu/edit")
	public ResponseEntity<?> editInspectionActuals(@Valid @RequestBody LotActualDto obj) {
		boolean isActuUpdated = inspectionlotService.saveInspActuals(obj);
		Map<String, Object> response = new HashMap<>();
		if (isActuUpdated) {
			response.put("message", "success");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		response.put("message", "fail");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
