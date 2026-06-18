package com.example.gerenciador.family.controller;

import com.example.gerenciador.family.dto.*;
import com.example.gerenciador.family.service.AdminFamilyService;
import com.example.gerenciador.family.service.FamilyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/families")
@RequiredArgsConstructor
public class FamilyController {

    private final AdminFamilyService adminFamilyService;
    private final FamilyService familyService;

    // ================ ROTAS ADMIN ======================

    // ================ GET ======================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/get")
    public ResponseEntity<Page<FamilyResponse>>  adminGetAllFamilies(@PageableDefault(size = 15)Pageable pageable){
        return ResponseEntity.ok(adminFamilyService.adminGetAllFamilies(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/get/family/{familyId}")
    public ResponseEntity<FamilyResponse> adminGetByFamilyId(@PathVariable Long familyId){
        return ResponseEntity.ok(adminFamilyService.adminGetByFamilyId(familyId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/get/family/{familyId}/members")
    public ResponseEntity<List<MemberResponse>> adminGetMembersByFamilyId(@PathVariable Long familyId){
        return ResponseEntity.ok(adminFamilyService.adminGetMembersByFamilyId(familyId));
    }


    // ================ POST ======================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/add/family/{familyId}/member/{memberId}")
    public ResponseEntity<FamilyMemberResponse> adminAddNewUserToFamily(@PathVariable Long familyId, @PathVariable Long memberId){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminFamilyService.adminAddNewUserToFamily(familyId, memberId));
    }

    // ================ PUT ======================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update/family/{familyId}")
    public ResponseEntity<FamilyResponse> adminUpdateFamily(@PathVariable Long familyId, @RequestBody FamilyUpdateRequest request){
        return ResponseEntity.ok(adminFamilyService.adminUpdateFamily(familyId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/change/family/{familyId}/member/{memberId}")
    public ResponseEntity<MemberResponse> dminChangeMemberToAdmin(@PathVariable Long familyId, @PathVariable Long memberId){
        return ResponseEntity.ok(adminFamilyService.adminChangeMemberToAdmin(familyId, memberId));
    }


    // ================ DELETE ======================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete/family/{familyId}")
    public ResponseEntity<Void> adminDeleteByFamilyId(@PathVariable long familyId){
        adminFamilyService.adminDeleteByFamilyId(familyId);
        return ResponseEntity.noContent().build();
    }


    // ================ ROTAS USUARIO ======================

    // ================ GET ======================

    @GetMapping("/my")
    public ResponseEntity<List<FamilyResponse>> getMyFamilies(){
        return ResponseEntity.ok(familyService.getMyFamilies());
    }

    @GetMapping("/my/family/{familyId}")
    public ResponseEntity<FamilyResponse> getFamily(@PathVariable Long familyId){
        return ResponseEntity.ok(familyService.getFamily(familyId));
    }


    @GetMapping("/my/family/{familyId}/members")
    public ResponseEntity<List<MemberResponse>> getFamilyMembers(@PathVariable Long familyId){
        return ResponseEntity.ok(familyService.getFamilyMembers(familyId));
    }


    // ================ POST ======================

    @PostMapping("/new")
    public ResponseEntity<FamilyResponse> createFamily(@Valid @RequestBody FamilyRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(familyService.createFamily(request));
    }

    @PostMapping("/add/family/{familyId}/member/{memberId}")
    public ResponseEntity<FamilyMemberResponse> addNewMemberToFamily(@PathVariable Long familyId, @PathVariable Long memberId){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(familyService.addNewMemberToFamily(familyId, memberId));
    }


    // ================ PUT ======================

    @PutMapping("/update/family/{familyId}")
    public ResponseEntity<FamilyResponse> updateFamily(@PathVariable Long familyId, @RequestBody FamilyUpdateRequest request){
        return ResponseEntity.ok(familyService.updateFamily(familyId, request));
    }

    @PutMapping("/update/family/{familyId}/member/{memberId}")
    public ResponseEntity<MemberResponse> changeMemberToAdmin(@PathVariable Long familyId, @PathVariable Long memberId){
        return ResponseEntity.ok(familyService.changeMemberToAdmin(familyId, memberId));
    }

    // ================ DELETE ======================

    @DeleteMapping("/delete/family/{familyId}")
    public ResponseEntity<Void> deleteFamily(@PathVariable Long familyId){
        familyService.deleteFamily(familyId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove/family/{familyId}/member/{memberId}")
    public ResponseEntity<Void> removeMemberFromFamily(@PathVariable Long familyId, @PathVariable Long memberId){
        familyService.removeMemberFromFamily(memberId, familyId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/exit/family/{familyId}")
    public ResponseEntity<Void> exitFromFamily(@PathVariable Long familyId){
        familyService.exitFromFamily(familyId);
        return ResponseEntity.noContent().build();
    }




}
