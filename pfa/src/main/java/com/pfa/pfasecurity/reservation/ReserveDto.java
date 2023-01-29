package com.pfa.pfasecurity.reservation;

import java.util.Date;

public class ReserveDto {
    private int materialId;
    private int userId;
    private int quantity;
	//user must insert date_return
	private Date return_date;

    //getters and setters
	public int getMaterialId() {
		return materialId;
	}
	public void setMaterialId(int materialId) {
		this.materialId = materialId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
//	public Date getReturn_date() {
//		return return_date;
//	}
//	public void setReturn_date(Date return_date) {
//		this.return_date = return_date;
//	}
    
    
}
