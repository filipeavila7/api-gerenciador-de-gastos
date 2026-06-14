package com.example.gerenciador.family.service;

import com.example.gerenciador.exceptions.FamilyNotFoundException;
import com.example.gerenciador.family.dto.FamilyResponse;
import com.example.gerenciador.family.dto.FamilyUpdateRequest;
import com.example.gerenciador.family.dto.MemberResponse;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.family.entity.FamilyMember;
import com.example.gerenciador.family.mapper.FamilyMapper;
import com.example.gerenciador.family.repository.FamilyMemberRepository;
import com.example.gerenciador.family.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminFamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyMapper familyMapper;


    // ================ GET ======================

    // admin pode ver todas as familias
    public Page<FamilyResponse> adminGetAllFamilies(Pageable pageable){

        return familyRepository.findAll(pageable)
                .map(familyMapper::toResponse);

    }

    // admin pode ver somente uma familia
    public FamilyResponse adminGetByFamilyId(Long familyId){
        Family family = familyRepository.findById(familyId)
                .orElseThrow(FamilyNotFoundException::new);

        return familyMapper.toResponse(family);
    }

    // admin pode ver todos os membros de uma familia
    public List<MemberResponse> adminGetMembersByFamilyId(Long familyId){
        familyRepository.findById(familyId)
                .orElseThrow(FamilyNotFoundException::new);

        List<FamilyMember> memberships = familyMemberRepository.findByFamilyId(familyId);

        return memberships.stream()
                .map(familyMapper::toMemberResponse)
                .toList();
    }

    // ================ DELETE ======================

    // admin pode deletar familia
    @Transactional
    public void adminDeleteByFamilyId(Long familyId){
        Family family =familyRepository.findById(familyId)
                .orElseThrow(FamilyNotFoundException::new);

        familyRepository.delete(family);
    }

    // ================ UPDATE ======================

    // admin pode editar uma familia
    @Transactional
    public FamilyResponse adminUpdateFamily (Long familyId, FamilyUpdateRequest request){

        Family family = familyRepository.findById(familyId)
                .orElseThrow(FamilyNotFoundException::new);

        // atualizar os dados
        if (request.name() != null){
            family.setName(request.name());
        }

        if (request.profileImg() != null){
            family.setProfileImg(request.profileImg());
        }

        familyRepository.save(family);

        return familyMapper.toResponse(family);
    }



}
