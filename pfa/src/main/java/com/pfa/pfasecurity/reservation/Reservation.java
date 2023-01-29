package com.pfa.pfasecurity.reservation;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfa.pfasecurity.material.Material;
import com.pfa.pfasecurity.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Reservation {
    @Id
    @GeneratedValue
    private Integer id;
    private Date dueDate;
    private Date reservationDate;
    private int quantity;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "material_id", referencedColumnName = "id")
    private Material material;
    
    
    
	public Reservation() {
		super();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public Date getReservationDate() {
		return reservationDate;
	}
	public void setReservationDate(Date reservationDate) {
		this.reservationDate = reservationDate;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
}
