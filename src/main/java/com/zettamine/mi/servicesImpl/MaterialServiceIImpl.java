package com.zettamine.mi.servicesImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.zettamine.mi.entities.InspectionLot;
import com.zettamine.mi.entities.Material;
import com.zettamine.mi.entities.MaterialInspectionCharacteristics;
import com.zettamine.mi.helper.Transformers;
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

		Material material = isCharacteristicConditionSatisfy(matChar);

		if (material == null) {
			return false;
		}

		MaterialInspectionCharacteristics matCharObj = Transformers
				.convertMaterialCharDtoToMaterialInspectionCharObj(matChar, material);

		LOG.info("new Material characteristic adding {}", matChar);

		MaterialInspectionCharacteristics savedCharacteristic = materialCharReposotory.save(matCharObj);

		if (savedCharacteristic.getCharacteristicId() > 0) {

			return true;
		}

		return false;
	}

	private Material isCharacteristicConditionSatisfy(MaterialCharDto matChar) {

		Material material = getMaterial(matChar.getMatId());

		if (material == null) {
			return null;
		}

		if (Double.valueOf(matChar.getLtl()) > Double.valueOf(matChar.getUtl())) {
			return null;
		}

		for (MaterialInspectionCharacteristics matCharItem : material.getMaterialChar()) {

			if (StringUtil.removeExtraSpaces(matCharItem.getCharacteristicDescription()).toUpperCase()
					.equals(matChar.getCharDesc().toUpperCase())) {
				return null;
			}

		}

		return material;
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
	public List<MaterialInspectionCharacteristics> getMaterialCharByLotId(Integer id) {

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

		if (optMaterial == null) {
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

	@SuppressWarnings("resource")
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean addListOfCharacteristicsForMaterial(MultipartFile file) throws Exception {

		String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

		ArrayList<String> list = new ArrayList<>();
		list.add("xls");
		list.add("xlsx");
		list.add("csv");

		if (!list.contains(fileExtension)) {
			throw new Exception("please provide .xls or .xlsx or .csv file");
		}

		try {
			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			XSSFSheet sheet_0 = workbook.getSheetAt(0);

			for (int row = 1; row < sheet_0.getPhysicalNumberOfRows(); row++) {
				XSSFRow currRow = sheet_0.getRow(row);

				String materialId = currRow.getCell(0).getStringCellValue();
				String charDesc = currRow.getCell(1).getStringCellValue();
				double utl = currRow.getCell(2).getNumericCellValue();
				double ltl = currRow.getCell(3).getNumericCellValue();
				String uom = currRow.getCell(4).getStringCellValue();

				MaterialCharDto materialCharDto = MaterialCharDto.builder().matId(materialId).charDesc(charDesc)
						.uom(uom).utl(utl).ltl(ltl).build();

				Material material = isCharacteristicConditionSatisfy(materialCharDto);
				if (material == null) {
					LOG.warn(
							"material characteristic {} is already available or materila is not available with id of {}",
							charDesc, materialId);
					throw new Exception(
							"material characteristic is already available or materila is not available with id of : "
									+ materialId);
				} else {
					MaterialInspectionCharacteristics matChar = Transformers
							.convertMaterialCharDtoToMaterialInspectionCharObj(materialCharDto, material);
					material.getMaterialChar().add(matChar);
					materialRepository.save(material);
					LOG.info("new material characteristic {} saved for material id : {}", charDesc, materialId);
				}
			}
		} catch (IOException e) {
			LOG.warn("unable to read uploaded document");
		}
		return true;
	}

}
