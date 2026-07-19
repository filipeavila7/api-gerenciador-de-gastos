package com.example.gerenciador.familyinvite.controller;

import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.familyinvite.dto.FamilyInviteDetailsResponse;
import com.example.gerenciador.familyinvite.dto.FamilyInviteResponse;
import com.example.gerenciador.familyinvite.service.FamilyInviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/invite")
@RestController
public class FamilyInviteController {
    private final FamilyInviteService familyInviteService;

    // ================ GET ======================

    @GetMapping("/token/{token}")
    public ResponseEntity<FamilyInviteDetailsResponse> getInvite(
            @PathVariable String token
    ){
        return ResponseEntity.ok(familyInviteService.getToken(token));
    }


    // ================ POST ======================

    @PostMapping("/family/{familyId}/new")
    public ResponseEntity<FamilyInviteResponse> createInvite(
            @PathVariable Long familyId
    ){
        return ResponseEntity.status(HttpStatus.CREATED).
                body(familyInviteService.createInvite(familyId));
    }


    @PostMapping("/token/{token}/cancel")
    public ResponseEntity<Void> cancelInvite(
            @PathVariable String token
    ){
        familyInviteService.cancelInvite(token);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/token/{token}/accept")
    public ResponseEntity<Void> acceptInvite(
            @PathVariable String token
    ){
        familyInviteService.acceptInvite(token);
        return ResponseEntity.noContent().build();
    }




}
