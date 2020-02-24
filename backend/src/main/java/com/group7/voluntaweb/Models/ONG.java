package com.group7.voluntaweb.Models;



import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


@Entity
@Table(name="ngos")
public class ONG {
	

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String name;
    
    
    @Column(unique = true)
    private String email;
    
    private String responsible_name;
    private String responsible_surname;
    @Lob
    private String description;
	
	private String address;
	private String telephone;
	private String postal;
	private String password;
	private String image;

    
   

	

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getResponsible_name() {
		return responsible_name;
	}

	public void setResponsible_name(String responsible_name) {
		this.responsible_name = responsible_name;
	}

	public String getResponsible_surname() {
		return responsible_surname;
	}

	public void setResponsible_surname(String responsible_surname) {
		this.responsible_surname = responsible_surname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getPostal() {
		return postal;
	}

	public void setPostal(String postal) {
		this.postal = postal;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public ONG() {
    }

public ONG(String name, String email, String responsible_name, String responsible_surname,String description, String address,String telephone, String postal, String image, String password) {

		this.name = name;
		this.email = email;
		this.responsible_name = responsible_name;
		this.responsible_surname = responsible_surname;
		this.description = description;
		this.address = address;
		this.telephone = telephone;
		this.postal = postal;
		this.image = image;
		this.password = password;


	}


}