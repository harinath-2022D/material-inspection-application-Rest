package com.zettamine.mi.servicesImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.zettamine.mi.entities.Vendor;
import com.zettamine.mi.repositories.VendorRepository;
import com.zettamine.mi.services.VendorService;
import com.zettamine.mi.utils.StringUtil;

@Service
public class VendorServiceImpl implements VendorService {

	private VendorRepository vendorRepository;

	public VendorServiceImpl(VendorRepository vendorRepository) {
		this.vendorRepository = vendorRepository;
	}

	@Override
	public boolean addNewVendor(Vendor vendor) {

		vendor.setName(StringUtil.removeExtraSpaces(vendor.getName()).toUpperCase());
		vendor.setEmail(StringUtil.removeExtraSpaces(vendor.getEmail()));
		Vendor savedVendor = vendorRepository.save(vendor);
		if (savedVendor.getVendorId() > 0) {
			return true;
		}
		return false;

	}

	@Override
	public List<Vendor> getAllVendor() {
		List<Vendor> vendorsList = vendorRepository.findAll();
		return vendorsList;
	}

	@Override
	public List<Vendor> getAllActiveVendor() {
		List<Vendor> vendorsList = vendorRepository.findAllActiveVendors(true);
		return vendorsList;
	}

	@Override
	public Vendor getVendor(Integer id) {
		Optional<Vendor> vendor = vendorRepository.findById(id);
		if (vendor.isEmpty()) {
			return null;
		}
		return vendor.get();
	}

	@Override
	public boolean deleteVendor(Integer id) {

		Optional<Vendor> optVendor = vendorRepository.findById(id);
		if (optVendor.isEmpty()) {
			return false;
		}
		Vendor vendor = optVendor.get();
		vendor.setStatus(false);
		vendorRepository.save(vendor);
		return true;
	}

	@Override
	public boolean updateVendor(Vendor vendor) {
		Optional<Vendor> optVendor = vendorRepository.findById(vendor.getVendorId());
		if (optVendor.isPresent()) {
			Vendor prevVendor = optVendor.get();
			prevVendor.setEmail(StringUtil.removeExtraSpaces(vendor.getEmail()));
			prevVendor.setName(StringUtil.removeExtraSpaces(vendor.getName()).toUpperCase());
			vendorRepository.save(prevVendor);
			return true;
		}

		return false;
	}

}
