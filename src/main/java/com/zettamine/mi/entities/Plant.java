package com.zettamine.mi.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "plantName"}))
public class Plant {
	
	@Id
	private String plantId;
	
	@NotBlank(message = "invalid plant name")
	private String plantName;
	
	@NotBlank(message = "please provide valid location")
	private String location;
	
	private boolean status = true;
	
	@JsonIgnore
	@OneToMany(mappedBy="plant", cascade = CascadeType.ALL)
	private List<InspectionLot> inspectionLot;
	
}
