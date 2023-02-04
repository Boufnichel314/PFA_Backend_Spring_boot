package com.pfa.pfasecurity.reservation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.pfasecurity.material.Material;
import com.pfa.pfasecurity.material.MaterialRepo;
import com.pfa.pfasecurity.user.User;
import com.pfa.pfasecurity.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationController {
	
	private final UserRepository repository;
    private final reservationRepository reservationRepository;
    private final MaterialRepo materialRepository;
	
	@GetMapping("/reservations")
    public ResponseEntity<List<ReservationDto>> getReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        List<ReservationDto> reservationDtos = new ArrayList<>();
        for (Reservation reservation : reservations) {
            User user = repository.findById(reservation.getUser().getId()).orElse(null);
            Material material = materialRepository.findById(reservation.getMaterial().getId()).orElse(null);
            ReservationDto reservationDto = new ReservationDto();
            reservationDto.setId(reservation.getId());
            reservationDto.setDueDate(reservation.getDueDate());
            reservationDto.setReservationDate(reservation.getReservationDate());
            reservationDto.setQuantity(reservation.getQuantity());
            reservationDto.setUsername(user.getFirstname()+' '+user.getLastname());
            reservationDto.setMaterial(material.getTitre());
            reservationDtos.add(reservationDto);
        }
        return ResponseEntity.ok(reservationDtos);
    }
	@DeleteMapping("reservations/{id}")
    public String deleteReservation(@PathVariable int id) {
        reservationRepository.deleteById(id);
        return "deleted !";
    }
}
