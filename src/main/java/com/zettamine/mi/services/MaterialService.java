package com.zettamine.mi.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.zettamine.mi.entities.InspectionLot;
import com.zettamine.mi.entities.Material;
import com.zettamine.mi.entities.MaterialInspectionCharacteristics;
import com.zettamine.mi.requestdtos.MaterialCharDto;


public interface MaterialService {

	List<Material> getAllMaterials();

	Material getMaterial(String id);

	boolean deleteMaterial(String id);

	boolean addNewMaterial(Material material);

	boolean addNewMaterialCharacteristic(MaterialCharDto matChar);

	List<InspectionLot> getAllInspectionLots();
	
	List<MaterialInspectionCharacteristics> getMaterialCharByLotId(Integer id);
//
//	boolean createInspectionLot(InspectionLot lot);
//
//	List<Vendor> getAllVendors();
//
//	List<Plant> getAllPlants();
//
//	InspectionLot getInspectionLot(Integer id);
//
//	boolean saveInspActuals(InspectionActuals actuals);

	List<MaterialInspectionCharacteristics> getAllCharacteristicsOfMaterial(String id);

	boolean saveEditMaterial(Material material);

	List<Material> getAllActiveMaterials();
	
	boolean addListOfCharacteristicsForMaterial(MultipartFile file) throws Exception;

}
