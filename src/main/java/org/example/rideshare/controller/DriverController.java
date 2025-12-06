package org.example.rideshare.controller;

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
@RequestMapping("/api/v1/driver")
@PreAuthorize("hasAuthority('ROLE_DRIVER')")
public class DriverController {

    @Autowired
    private RideService rideService;

    @Autowired
    private UserService userService;

    @GetMapping("/rides/requests")
    public ResponseEntity<List<RideResponse>> getPendingRides() {
        List<RideResponse> rides = rideService.getPendingRides();
        return ResponseEntity.ok(rides);
    }

    @PostMapping("/rides/{rideId}/accept")
    public ResponseEntity<RideResponse> acceptRide(@PathVariable String rideId, Authentication authentication) {
        String username = authentication.getName();
        String driverId = userService.getUserIdByUsername(username);
        RideResponse response = rideService.acceptRide(rideId, driverId);
        return ResponseEntity.ok(response);
    }

}


