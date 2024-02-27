package com.zettamine.mi.controllers;

import java.util.List;

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

import com.zettamine.mi.entities.Vendor;
import com.zettamine.mi.services.VendorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/vendor")
public class VendorController {

	private VendorService vendorService;

	public VendorController(VendorService vendorService) {
		this.vendorService = vendorService;
	}

	@PostMapping("/save")
	public ResponseEntity<?> addNewVendor(@Valid @RequestBody Vendor vendor) {

		boolean isVendorAdded = vendorService.addNewVendor(vendor);
		if (isVendorAdded) {
			return new ResponseEntity<>("new vendor added , | " + vendor.getName(), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("vendor saving failed", HttpStatus.CONFLICT);
		}

	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getVendorDetails(@PathVariable Integer id) {
		Vendor vendor = vendorService.getVendor(id);
		if (vendor != null) {
			return new ResponseEntity<>(vendor, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("no vendor available with id | " + id, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllVendors() {
		List<Vendor> vendorsList = vendorService.getAllVendor();
		return new ResponseEntity<>(vendorsList, HttpStatus.OK);
	}

	@PutMapping("/edit")
	public ResponseEntity<?> editVendor(@Valid @RequestBody Vendor vendor) {
		boolean isVendorUpdateed = vendorService.updateVendor(vendor);
		if (isVendorUpdateed) {
			return new ResponseEntity<>("vendor updated | id " + vendor.getName(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>("vendor updation failed", HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteVendor(@PathVariable Integer id) {

		boolean isVendorDeleted = vendorService.deleteVendor(id);
		if (isVendorDeleted) {
			return new ResponseEntity<>("vendor deleted", HttpStatus.OK);
		}else {
			return new ResponseEntity<>("vendor deleion failed", HttpStatus.BAD_REQUEST);
		}
	}

}
