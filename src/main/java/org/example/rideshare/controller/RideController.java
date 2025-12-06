package org.example.rideshare.controller;

import jakarta.validation.Valid;
import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.dto.RideResponse;
import org.example.rideshare.service.RideService;
import org.example.rideshare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody CreateRideRequest request, Authentication authentication) {
        String username = authentication.getName();
        String userId = userService.getUserIdByUsername(username);
        RideResponse response = rideService.createRide(request, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(@PathVariable String rideId, Authentication authentication) {
        String username = authentication.getName();
        RideResponse response = rideService.completeRide(rideId, username);
        return ResponseEntity.ok(response);
    }

}


