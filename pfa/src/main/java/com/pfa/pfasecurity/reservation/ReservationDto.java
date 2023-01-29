package com.pfa.pfasecurity.reservation;

import com.pfa.pfasecurity.material.Material;
import com.pfa.pfasecurity.user.User;

public class ReservationDto {
    private Reservation reservation;
    private User user;
    private Material material;
    //getters and setters
	public Reservation getReservation() {
		return reservation;
	}
	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
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
    
}