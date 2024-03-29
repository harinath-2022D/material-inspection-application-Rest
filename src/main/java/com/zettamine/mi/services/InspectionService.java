package com.zettamine.mi.services;

import java.util.List;

import com.zettamine.mi.entities.InspectionLot;
import com.zettamine.mi.entities.Material;
import com.zettamine.mi.entities.Plant;
import com.zettamine.mi.entities.Vendor;
import com.zettamine.mi.requestdtos.DateRangeLotSearch;
import com.zettamine.mi.requestdtos.EditLotDto;
import com.zettamine.mi.requestdtos.LotActualDto;
import com.zettamine.mi.requestdtos.LotCreationDto;
import com.zettamine.mi.responsedtos.DateRangeLotResponseDto;
import com.zettamine.mi.responsedtos.LotActualsAndCharacteristicsResponseDto;

public interface InspectionService {
	InspectionLot getLotDetails(Integer lot);
	
	List<LotActualsAndCharacteristicsResponseDto> getActualAndOriginalOfLot(Integer id);
	
	List<InspectionLot> getAllInspectionLots();
	
	boolean saveInspActuals(LotActualDto actuals);
	
	List<DateRangeLotResponseDto> getAllLotsDetailsBetweenDateRange(DateRangeLotSearch obj);
	
	boolean updateInspectionLot(EditLotDto lot);
	
	boolean createInspectionLot(LotCreationDto lot);

	List<Vendor> getAllVendors();

	List<Plant> getAllPlants();
	
	List<Material> getAllMaterials();
		
}
