package com.zettamine.mi.servicesImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zettamine.mi.entities.InspectionLot;
import com.zettamine.mi.entities.Material;
import com.zettamine.mi.entities.MaterialInspectionCharacteristics;
import com.zettamine.mi.repositories.InspectionLotRepository;
import com.zettamine.mi.repositories.MaterialCharRepository;
import com.zettamine.mi.repositories.MaterialRepository;
import com.zettamine.mi.requestdtos.MaterialCharDto;
import com.zettamine.mi.services.MaterialService;
import com.zettamine.mi.utils.StringUtil;

@Service
public class MaterialServiceIImpl implements MaterialService {

	private MaterialRepository materialRepository;

	private MaterialCharRepository materialCharReposotory;

	private InspectionLotRepository inspectionLotRepo;

	private Logger LOG = LoggerFactory.getLogger(MaterialServiceIImpl.class);

	public MaterialServiceIImpl(MaterialRepository materialRepository, MaterialCharRepository materialCharReposotory,
			InspectionLotRepository inspectionLotRepo) {
		super();
		this.materialRepository = materialRepository;

		this.materialCharReposotory = materialCharReposotory;

		this.inspectionLotRepo = inspectionLotRepo;

	}

	@Override
	public List<Material> getAllMaterials() {

		LOG.info("finding all materials");

		List<Material> materialList = materialRepository.findAll();

		LOG.info("returing all materials list");

		return materialList;
	}

	@Override
	public Material getMaterial(String id) {

		LOG.info("finding material with id : {}", id);

		Optional<Material> optMaterial = materialRepository.findById(id.toUpperCase());

		if (optMaterial.isEmpty()) {

			LOG.info("no material associated with id : {}", id);

			return null;
		}

		LOG.info("returing material with id : {}", id);

		return optMaterial.get();
	}

	@Override
	public boolean deleteMaterial(String id) {

		LOG.info("finding material with id : {}", id);

		Optional<Material> optMaterial = materialRepository.findById(id);

		if (optMaterial.isEmpty()) {

			LOG.info("no material associated with id : {}", id);

			return false;
		}

		Material material = optMaterial.get();

		LOG.info("setting material status to INACTIVE");

		material.setStatus(false);

		LOG.info("saving material of id : {}", id);

		materialRepository.save(material);

		LOG.info("returning true");

		return true;
	}

	@Override
	public boolean addNewMaterial(Material material) {

		Optional<Material> optMaterial = materialRepository.findById(material.getMaterialId());
		Optional<Material> optMaterialDesc = materialRepository.findByMaterialDesc(material.getMaterialDesc());

		if (optMaterial.isPresent() || optMaterialDesc.isPresent()) {
			return false;
		} else {

			material.setMaterialId(StringUtil.removeAllSpaces(material.getMaterialId()).toUpperCase());

			material.setMaterialDesc(StringUtil.removeExtraSpaces(material.getMaterialDesc()).toUpperCase());

			material.setType(StringUtil.removeExtraSpaces(material.getType().toUpperCase()));

			Material savedMaterial = materialRepository.save(material);

			if (savedMaterial.getMaterialId() == null) {
				return false;
			}

			LOG.info("new material saved with id : {}", savedMaterial.getMaterialId());

			return true;
		}
	}

	@Override
	public boolean addNewMaterialCharacteristic(MaterialCharDto matChar) {

		if (Double.valueOf(matChar.getLtl()) > Double.valueOf(matChar.getUtl())) {
			return false;
		}
		
		Material material = getMaterial(matChar.getMatId());
		
		if(material == null) {
			return false;
		}

		for (MaterialInspectionCharacteristics matCharItem : material.getMaterialChar()) {

			if (StringUtil.removeExtraSpaces(matCharItem.getCharacteristicDescription()).toUpperCase().equals(matChar.getCharDesc().toUpperCase())) {
				return false;
			}

		}

		MaterialInspectionCharacteristics matCharObj = new MaterialInspectionCharacteristics();
		
		matCharObj.setCharacteristicDescription(StringUtil.removeExtraSpaces(matChar.getCharDesc()).toUpperCase());
		matCharObj.setUnitOfMeasure(StringUtil.removeAllSpaces(matChar.getUom()));
		matCharObj.setLowerToleranceLimit(matChar.getLtl());
		matCharObj.setUpperToleranceLimit(matChar.getUtl());
		matCharObj.setMaterial(material);

		LOG.info("new Material characteristic adding {}", matChar);

		MaterialInspectionCharacteristics savedCharacteristic = materialCharReposotory.save(matCharObj);

		if (savedCharacteristic.getCharacteristicId() > 0) {

			return true;
		}

		return false;
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
//		return lots;
	}

	@Override
	public List<MaterialInspectionCharacteristics> getMaterialCharByLotId(String id) {

		Optional<InspectionLot> optLot = inspectionLotRepo.findById(id);

		if (optLot.isEmpty()) {
			return null;

		} else {

			Material material = optLot.get().getMaterial();

			List<Integer> inspActualsList = optLot.get().getInspectionActuals().stream()
					.map(inspAct -> inspAct.getMaterialInspectionCharacteristics().getCharacteristicId())
					.collect(Collectors.toList());

			List<MaterialInspectionCharacteristics> charList = material.getMaterialChar();

			List<MaterialInspectionCharacteristics> repsonseList = new LinkedList<>();

			for (MaterialInspectionCharacteristics item : charList) {
				if (inspActualsList.contains(item.getCharacteristicId())) {

				} else {

					LOG.info("getting all material characteristics of lot {}", id);

					repsonseList.add(item);
				}
			}
			return repsonseList;
		}
	}

	@Override
	public List<MaterialInspectionCharacteristics> getAllCharacteristicsOfMaterial(String id) {
		Material material = getMaterial(StringUtil.removeAllSpaces(id));

		List<MaterialInspectionCharacteristics> list = new LinkedList<>();

		if (material != null) {

			list = material.getMaterialChar();
		}
		return list;
	}

	@Override
	public boolean saveEditMaterial(Material material) {
		
		Material optMaterial = getMaterial(StringUtil.removeAllSpaces(material.getMaterialId()).toUpperCase());
		
		if(optMaterial == null) {
			return false;
		}

		optMaterial.setMaterialId(StringUtil.removeAllSpaces(material.getMaterialId()).toUpperCase());

		optMaterial.setMaterialDesc(StringUtil.removeExtraSpaces(material.getMaterialDesc()).toUpperCase());

		optMaterial.setType(StringUtil.removeExtraSpaces(material.getType().toUpperCase()));

		Material savedMaterial = materialRepository.save(optMaterial);

		if (savedMaterial.getMaterialId() == null) {
			return false;
		}

		LOG.info("material updation saved with id : {}", savedMaterial.getMaterialId());

		return true;
	}

	@Override
	public List<Material> getAllActiveMaterials() {
		List<Material> materialList = materialRepository.findAllByStatus(true);
		return materialList;
	}


}
