package com.zettamine.mi.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
public class InspectionLot {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lot_seq")
	@SequenceGenerator(name = "lot_seq", sequenceName = "lot_sequence", initialValue = 4000, allocationSize = 1)
	private Integer lotId;
	
	private LocalDate creationDate;
	
	private LocalDate inspectionStartDate;

	private LocalDate inspectionEndDate;

	private String result;

	private String remarks;

	@OneToMany(mappedBy = "inspectionLot", cascade = CascadeType.ALL)
	private List<InspectionActuals> inspectionActuals = new ArrayList<>();

	@ManyToOne
	private Material material;

	@ManyToOne
	private Vendor vendor;

	@ManyToOne
	private Plant plant;

	@ManyToOne
	@JsonIgnore
	private User user;
	
	transient private String username;

	public InspectionLot(Integer id) {
		this.lotId = id;
	}

	@Override
	public String toString() {
		return "InspectionLot [lotId=" + lotId + ", material=" + material + ", vendor=" + vendor + ", plant=" + plant
				+ ", creationDate=" + creationDate + "]";
	}

	public InspectionLot() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InspectionLot(Integer lotId, LocalDate creationDate, LocalDate inspectionStartDate,
			LocalDate inspectionEndDate, String result, String remarks, List<InspectionActuals> inspectionActuals,
			Material material, Vendor vendor, Plant plant, User user, String username) {
		super();
		this.lotId = lotId;
		this.creationDate = creationDate;
		this.inspectionStartDate = inspectionStartDate;
		this.inspectionEndDate = inspectionEndDate;
		this.result = result;
		this.remarks = remarks;
		this.inspectionActuals = inspectionActuals;
		this.material = material;
		this.vendor = vendor;
		this.plant = plant;
		this.user = user;
		this.username = username;
	}
	
	

}
