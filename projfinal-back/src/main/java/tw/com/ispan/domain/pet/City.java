package tw.com.ispan.domain.pet;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "City")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "cityId")
public class City {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer cityId;

	@Column(name = "city", columnDefinition = "NVARCHAR(5)", nullable = false)
	private String city;
	
	// 和DistrictArea表雙向一對多
	@OneToMany(mappedBy = "city", cascade = CascadeType.PERSIST)
	@JsonIgnore
	private List<DistrictArea> districtAreas;

	// 和RescueCase表雙向一對多
	@OneToMany(mappedBy = "city", cascade = CascadeType.PERSIST)
	@JsonIgnore
	private List<RescueCase> rescueCases;

	// 和LostCase表雙向一對多
	@OneToMany(mappedBy = "city", cascade = CascadeType.PERSIST)
	@JsonIgnore
	private List<LostCase> lostCases;

	// 和adoptionCase表雙向一對多
	@OneToMany(mappedBy = "city", cascade = CascadeType.PERSIST)
	@JsonIgnore
	private List<AdoptionCase> adoptionCases;

	public City() {
		super();
	}

	public City(String city) {
		super();
		this.city = city;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<RescueCase> getRescueCases() {
		return rescueCases;
	}

	public void setRescueCases(List<RescueCase> rescueCases) {
		this.rescueCases = rescueCases;
	}

	public List<DistrictArea> getDistrictAreas() {
		return districtAreas;
	}

	public void setDistrictAreas(List<DistrictArea> districtAreas) {
		this.districtAreas = districtAreas;
	}

	public List<LostCase> getLostCases() {
		return lostCases;
	}

	public void setLostCases(List<LostCase> lostCases) {
		this.lostCases = lostCases;
	}

	public List<AdoptionCase> getAdoptionCases() {
		return adoptionCases;
	}

	public void setAdoptionCases(List<AdoptionCase> adoptionCases) {
		this.adoptionCases = adoptionCases;
	}

}
