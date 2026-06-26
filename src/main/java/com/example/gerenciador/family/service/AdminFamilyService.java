package com.example.gerenciador.family.service;

import com.example.gerenciador.exceptions.FamilyNotFoundException;
import com.example.gerenciador.exceptions.MemberLimitExceededException;
import com.example.gerenciador.exceptions.UserAlreadyInFamilyException;
import com.example.gerenciador.exceptions.UserNotFoundException;
import com.example.gerenciador.family.dto.FamilyMemberResponse;
import com.example.gerenciador.family.dto.FamilyResponse;
import com.example.gerenciador.family.dto.FamilyUpdateRequest;
import com.example.gerenciador.family.dto.MemberResponse;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.family.entity.FamilyMember;
import com.example.gerenciador.family.entity.FamilyRole;
import com.example.gerenciador.family.mapper.FamilyMapper;
import com.example.gerenciador.family.repository.FamilyMemberRepository;
import com.example.gerenciador.family.repository.FamilyRepository;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.user.entity.User;
import com.example.gerenciador.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminFamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyMapper familyMapper;
    private final UserRepository userRepository;
    private final GlobalHelperService globalHelperService;

    // ================ GET ======================

    // admin pode ver todas as familias
    @Transactional(readOnly = true)
    public Page<FamilyResponse> adminGetAllFamilies(Pageable pageable){

        return familyRepository.findAll(pageable)
                .map(familyMapper::toResponse);

    }

    // admin pode ver somente uma familia
    @Transactional(readOnly = true)
    public FamilyResponse adminGetByFamilyId(Long familyId){


        Family family = globalHelperService.getFamilyOrThrow(familyId);

        return familyMapper.toResponse(family);
    }

    // admin pode ver todos os membros de uma familia
    @Transactional(readOnly = true)
    public List<MemberResponse> adminGetMembersByFamilyId(Long familyId){

        // encontrar a familia
        globalHelperService.getFamilyOrThrow(familyId);

        List<FamilyMember> memberships = familyMemberRepository.findByFamilyId(familyId);

        return memberships.stream()
                .map(familyMapper::toMemberResponse)
                .toList();
    }

    // ================ POST ======================

    // admin geral pode adcionar membros a uma familia
    @Transactional
        public FamilyMemberResponse adminAddNewUserToFamily(Long familyId, Long userId){

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        User member =  userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // verifica se a familia não esta cheia antes de adcionar um novo membro
        if (familyMemberRepository.countByFamilyId(familyId) >= 12) {
            throw new MemberLimitExceededException();
        }

        // verifica se o novo usuario ja esta nessa família
        if (familyMemberRepository.existsByFamilyAndUser(family, member)){
            throw new UserAlreadyInFamilyException();
        }

        // criar vinculo
        FamilyMember familyMember = new FamilyMember();

        familyMember.setUser(member);
        familyMember.setFamily(family);
        familyMember.setRole(FamilyRole.MEMBER);
        familyMember.setJoinedAt(LocalDateTime.now());

        familyMemberRepository.save(familyMember);

        return  familyMapper.toFamilyMemberResponse(familyMember);
    }

    // ================ DELETE ======================

    // admin pode deletar familia
    @Transactional
    public void adminDeleteByFamilyId(Long familyId){
        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        familyRepository.delete(family);
    }

    // ================ PUT ======================

    // admin pode editar uma familia
    @Transactional
    public FamilyResponse adminUpdateFamily (Long familyId, FamilyUpdateRequest request){

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

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


    @Transactional
    // admin trnasformar membros em admin
    public MemberResponse adminChangeMemberToAdmin(Long familyId, Long memberId){

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        FamilyMember member = familyMemberRepository.findByFamilyIdAndUserId(familyId, memberId)
                .orElseThrow(UserNotFoundException::new);

        if (member.getRole() == FamilyRole.ADMIN){
            return familyMapper.toMemberResponse(member);
        }

        member.setRole(FamilyRole.ADMIN);

        familyMemberRepository.save(member);

        return familyMapper.toMemberResponse(member);


    }



}
