package com.zettamine.mi.servicesImpl;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zettamine.mi.entities.InspectionActuals;
import com.zettamine.mi.entities.InspectionLot;
import com.zettamine.mi.entities.Material;
import com.zettamine.mi.entities.MaterialInspectionCharacteristics;
import com.zettamine.mi.entities.Plant;
import com.zettamine.mi.entities.User;
import com.zettamine.mi.entities.Vendor;
import com.zettamine.mi.repositories.InspectionActualsRepository;
import com.zettamine.mi.repositories.InspectionLotRepository;
import com.zettamine.mi.repositories.UserRepository;
import com.zettamine.mi.requestdtos.DateRangeLotSearch;
import com.zettamine.mi.requestdtos.EditLotDto;
import com.zettamine.mi.requestdtos.LotActualDto;
import com.zettamine.mi.requestdtos.LotCreationDto;
import com.zettamine.mi.responsedtos.DateRangeLotResponse;
import com.zettamine.mi.responsedtos.LotActualsAndCharacteristics;
import com.zettamine.mi.services.InspectionService;
import com.zettamine.mi.services.MaterialService;
import com.zettamine.mi.services.PlantService;
import com.zettamine.mi.services.VendorService;
import com.zettamine.mi.utils.StringUtil;

@Service
public class InspectionServiceImpl implements InspectionService {
	private InspectionLotRepository inspectionLotRepo;

	private InspectionActualsRepository inspectionActRepo;

	private UserRepository userRepo;

	private PlantService plantService;

	private VendorService vendorService;

	private MaterialService materialService;

	private Logger LOG = LoggerFactory.getLogger(InspectionServiceImpl.class);

	public InspectionServiceImpl(InspectionLotRepository inspectionLotRepo,
			InspectionActualsRepository inspectionActRepo, UserRepository userRepo, VendorService vendorService,
			PlantService plantService, MaterialService materialService) {

		super();
		this.inspectionLotRepo = inspectionLotRepo;
		this.inspectionActRepo = inspectionActRepo;
		this.userRepo = userRepo;
		this.vendorService = vendorService;
		this.plantService = plantService;
		this.materialService = materialService;
	}

	@Override
	public InspectionLot getLotDetails(Integer id) {
		Optional<InspectionLot> optInsp = inspectionLotRepo.findById(id);

		if (optInsp.isPresent()) {

			LOG.info("Finding lot with id is success : {}", id);

			InspectionLot lot = optInsp.get();
			if (lot.getUser() != null) {
				lot.setUsername(lot.getUser().getUsername());
			}

			return lot;
		}
		return null;
	}

	@Override
	public List<LotActualsAndCharacteristics> getActualAndOriginalOfLot(Integer id) {

		InspectionLot lot = getLotDetails(id);

		Material material = lot.getMaterial();

		LOG.info("Getting lot charactesristics and actuals of lot id : {}", id);

		List<MaterialInspectionCharacteristics> characteristics = material.getMaterialChar();

		List<InspectionActuals> actuals = lot.getInspectionActuals();

		List<LotActualsAndCharacteristics> list = new LinkedList<>();

		for (int start = 0; start < characteristics.size(); start++) {

			LotActualsAndCharacteristics lotActOrg = LotActualsAndCharacteristics.builder()

					.lotId(id)

					.sNo(start + 1)

					.characteristicId(characteristics.get(start).getCharacteristicId())

					.characteristicDesc(characteristics.get(start).getCharacteristicDescription())

					.upperToleranceLimit(characteristics.get(start).getUpperToleranceLimit())

					.lowerToleranceLimit(characteristics.get(start).getLowerToleranceLimit())

					.unitOfMeasure(characteristics.get(start).getUnitOfMeasure()).build();

			list.add(lotActOrg);
		}

		LOG.info("Arrenging lot characteristics and actuals together of lot id : {}", id);

		for (int start = 0; start < list.size(); start++) {

			Integer charId = list.get(start).getCharacteristicId();

			for (int act = 0; act < actuals.size(); act++) {

				if (actuals.get(act).getMaterialInspectionCharacteristics().getCharacteristicId() == charId) {

					list.get(start).setActualUtl(actuals.get(act).getMaximumMeasurement());

					list.get(start).setActualLtl(actuals.get(act).getMinimumMeasurement());
				}
			}
		}

		LOG.info("returnig Lot Actuals and characteristics as a list");
		return list;
	}

	@Override
	public List<InspectionLot> getAllInspectionLots() {

		LOG.info("getting all lots");

		List<InspectionLot> lots = inspectionLotRepo.findAll();

		List<InspectionLot> responseList = new LinkedList<>();

		for (InspectionLot lot : lots) {

			if (lot.getMaterial().getMaterialChar().size() != lot.getInspectionActuals().size()) {

				LOG.info("adding lots those have not done all inspection actuals");

				responseList.add(lot);
			}
		}

		LOG.info("returing response list");

		return responseList;
	}

	@Override
	public boolean saveInspActuals(LotActualDto actualsDto) {

		InspectionLot lot = getLotDetails(actualsDto.getLot());
		
		List<MaterialInspectionCharacteristics> totalReqChar = lot.getMaterial().getMaterialChar();
		List<InspectionActuals> actualChar = lot.getInspectionActuals();

		InspectionActuals optActuals = inspectionActRepo
				.findByInspectionLotAndmaterialInspectionCharacteristics(actualsDto.getLot(), actualsDto.getCharId());

		if (optActuals == null) {

			InspectionActuals actuals = InspectionActuals.builder().inspectionLot(lot)
					.maximumMeasurement(actualsDto.getMaxMeas()).minimumMeasurement(actualsDto.getMinMeas()).build();

			for (MaterialInspectionCharacteristics matChar : totalReqChar) {
				if (matChar.getCharacteristicId() == actualsDto.getCharId()) {
					actuals.setMaterialInspectionCharacteristics(matChar);
					break;
				}
			}

			lot.getInspectionActuals().add(actuals);
		}else {
			optActuals.setMaximumMeasurement(actualsDto.getMaxMeas());
			optActuals.setMinimumMeasurement(actualsDto.getMinMeas());
			lot.getInspectionActuals().add(optActuals);
		}
		
		inspectionLotRepo.save(lot);

		LOG.info("new inpsection actuals saving for lot id : {}", actualsDto.getLot());

		boolean result = false;

		if (totalReqChar.size() == actualChar.size()) {

			LOG.info("Evaluating lot for MARKING APPROVEL");

			for (InspectionActuals actual : actualChar) {

				Double actualUpperTolerance = actual.getMaximumMeasurement();

				Double actualLowerTolerance = actual.getMinimumMeasurement();

				for (int i = 0; i < totalReqChar.size(); i++) {

					Double reqUpperTolerance = totalReqChar.get(i).getUpperToleranceLimit();

					Double reqLowerTolerance = totalReqChar.get(i).getLowerToleranceLimit();

					if (actual.getMaterialInspectionCharacteristics().getCharacteristicId() == totalReqChar.get(i)
							.getCharacteristicId()) {

						if (actualUpperTolerance > reqUpperTolerance || actualUpperTolerance < reqLowerTolerance) {

							LOG.info(
									"MARKING APPROVAL is rejected due to actuals did not meet charactestics of lot id : {}",
									lot.getLotId());

							result = true;
						}
						if (actualLowerTolerance < reqLowerTolerance || actualLowerTolerance > reqUpperTolerance) {

							LOG.info(
									"MARKING APPROVAL is rejected due to actuals did not meet charactestics of lot id : {}",
									lot.getLotId());

							result = true;
						}
					}
				}
			}
			if (result == false) {

				lot.setResult("MARKED FOR APPROVAL");

				LOG.info("lot marked for approvel of id : {}", lot.getLotId());

				inspectionLotRepo.save(lot);
			} else {
				lot.setResult("INSP");
				inspectionLotRepo.save(lot);
			}
		}

		return true;
	}

	@Override
	public List<DateRangeLotResponse> getAllLotsDetailsBetweenDateRange(DateRangeLotSearch obj) {

		List<InspectionLot> inspList = inspectionLotRepo.findAllBycreationDateBetween(obj.getFromDate(),
				obj.getToDate());

		List<Predicate<InspectionLot>> searchCriteriaList = new LinkedList<>();

		if (obj.getMaterialId() != null) {

			LOG.info("filtering lots having material id as {}", obj.getMaterialId());
			searchCriteriaList.add(lot -> (lot.getMaterial().getMaterialId()
					.equals(StringUtil.removeAllSpaces(obj.getMaterialId()).toUpperCase())));
		}

		if (obj.getPlantId() != null) {

			LOG.info("filtering lots having plant id as {}", obj.getPlantId());
			searchCriteriaList.add(lot -> (lot.getPlant().getPlantId()
					.equals(StringUtil.removeExtraSpaces(obj.getPlantId()).toUpperCase())));
		}

		if (obj.getStatus() != null) {

			LOG.info("filtering lots having status as {}", obj.getStatus());
			searchCriteriaList.add(lot -> (lot.getResult().equals(obj.getStatus())));
		}

		if (obj.getVendorId() != 0) {

			LOG.info("filtering lots having vendor as {}", obj.getVendorId());
			searchCriteriaList.add(lot -> (lot.getVendor().getVendorId() == obj.getVendorId()));
		}

		Predicate<InspectionLot> searchCriteria = lot -> true;

		for (Predicate predicate : searchCriteriaList) {
			searchCriteria = searchCriteria.and(predicate);
		}

		List<InspectionLot> requiredList = inspList.stream().filter(searchCriteria).collect(Collectors.toList());

		List<DateRangeLotResponse> responseList = new LinkedList<>();

		for (InspectionLot lot : requiredList) {

			DateRangeLotResponse respLot = DateRangeLotResponse.builder().createdOn(lot.getCreationDate())
					.endOn(lot.getInspectionEndDate()).startOn(lot.getInspectionStartDate()).result(lot.getResult())
					.inspectedBy(lot.getUser() != null ? lot.getUser().getUsername() : null)
					.material(lot.getMaterial().getMaterialDesc()).lotId(lot.getLotId()).build();

			responseList.add(respLot);
		}

		LOG.info("Returing lots meets filter criteria of size : {}", responseList.size());
		return responseList;
	}

	@Override
	public boolean updateInspectionLot(EditLotDto lot) {

		Optional<InspectionLot> optInsp = inspectionLotRepo.findById(lot.getId());

		if (optInsp.isEmpty()) {
			return false;
		}

		Optional<User> optUser = userRepo.findById(lot.getUserid());
		if (optUser.isEmpty()) {
			return false;
		}

		InspectionLot originalLot = optInsp.get();

		originalLot.setInspectionEndDate(lot.getDate());
		originalLot.setResult(StringUtil.removeExtraSpaces(lot.getResult()));

		originalLot.setRemarks(StringUtil.removeExtraSpaces(lot.getRemarks()));
		originalLot.setUser(optUser.get());

		LOG.info("updating lot result is successfull of lot id : {}", lot.getId());
		inspectionLotRepo.save(originalLot);

		return true;
	}

	@Override
	public boolean createInspectionLot(LotCreationDto lotDto) {

		boolean isValidDates = validateStDateAndCrDate(lotDto.getStDt(), lotDto.getCrDt());

		if (isValidDates == false) {
//			throw new RuntimeException("Invalid Start Date or Create Date");
			return false;
		}

		Material material = materialService.getMaterial(StringUtil.removeAllSpaces(lotDto.getMatId()));
		Plant plant = plantService.getPlant(StringUtil.removeAllSpaces(lotDto.getPlantId()));
		Vendor vendor = vendorService.getVendor(lotDto.getVendorId());

		if (material == null || plant == null || vendor == null) {
			return false;
		}

		InspectionLot lot = InspectionLot.builder().result("INSP").creationDate(lotDto.getCrDt())
				.inspectionStartDate(lotDto.getStDt()).material(material).plant(plant).vendor(vendor).build();

		InspectionLot savedLot = inspectionLotRepo.save(lot);

		if (savedLot.getLotId() > 0) {

			LOG.info("new lot Created with id : {}", savedLot.getLotId());

			return true;
		}

		return false;
	}

	private boolean validateStDateAndCrDate(LocalDate stDt, LocalDate crDt) {
		if (stDt.isBefore(crDt)) {
			return false;
		}

		return true;
	}

	@Override
	public List<Vendor> getAllVendors() {
		LOG.info("getting all vendors");

		List<Vendor> vendorList = vendorService.getAllActiveVendor();

		return vendorList;
	}

	@Override
	public List<Plant> getAllPlants() {
		LOG.info("getting all plants");

		List<Plant> plantList = plantService.getAllPlants();

		return plantList;
	}

	@Override
	public List<Material> getAllMaterials() {
		LOG.info("finding all materials");

		List<Material> materialList = materialService.getAllMaterials();

		LOG.info("returing all materials list");

		return materialList;
	}

	

}
