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
@RequestMapping("/api/v1/user")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class UserController {

    @Autowired
    private RideService rideService;

    @Autowired
    private UserService userService;

    @GetMapping("/rides")
    public ResponseEntity<List<RideResponse>> getUserRides(Authentication authentication) {
        String username = authentication.getName();
        String userId = userService.getUserIdByUsername(username);
        List<RideResponse> rides = rideService.getUserRides(userId);
        return ResponseEntity.ok(rides);
    }

}


