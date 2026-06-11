package com.example.gerenciador.family;

import com.example.gerenciador.exceptions.*;
import com.example.gerenciador.family.dto.FamilyMemberResponse;
import com.example.gerenciador.family.dto.FamilyRequest;
import com.example.gerenciador.family.dto.FamilyResponse;
import com.example.gerenciador.family.repository.FamilyMemberRepository;
import com.example.gerenciador.family.repository.FamilyRepository;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.user.User;
import com.example.gerenciador.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepository familyRepository;
    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final FamilyMemberRepository familyMemberRepository;


    // criar família com o user logado sendo o admin dela
    public FamilyResponse createFamily (FamilyRequest request ) {
        User loggedUser = securityService.getLoggedUser();

        userRepository.findByEmail(loggedUser.getEmail())
                .orElseThrow(UserNotFoundException::new);

        long count = familyMemberRepository.countByUserAndRole(loggedUser, FamilyRole.ADMIN);

        if (count >= 3){
            throw new FamilyLimitExceededException();
        }

        // cria a família
        Family family = new Family();

        family.setName(request.name());
        family.setProfileImg(request.profileImg());
        family.setCreatedAt(LocalDateTime.now());

        // adciona o user logado como membro e admin
        FamilyMember member = new FamilyMember();

        member.setFamily(family);
        member.setUser(loggedUser);
        member.setRole(FamilyRole.ADMIN);

        // adciona o membro do outro lado
        family.getUserMembres().add(member);

        familyRepository.save(family);

        return toResponse(family);

    }

    @Transactional
    public void deleteFamily(Long familyId){
        User loggedUser = securityService.getLoggedUser();

        Family family = familyRepository.findById(familyId)
                .orElseThrow(FamilyNotFoundException::new);

        // verificações
        FamilyMember loggedMember = familyMemberRepository.findByFamilyAndUser(family, loggedUser)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));


        if (loggedMember.getRole() != FamilyRole.ADMIN){
            throw new AccessDeniedException("Access denied");
        }

        familyRepository.delete(family);

    }

    // usuário Admin adcionar novos membros as famílias
    public FamilyMemberResponse addNewMemberToFamily(Long familyId, Long memberId){
        User loggedUser = securityService.getLoggedUser();

        // encontra o membro novo
        User member = userRepository.findById(memberId)
                .orElseThrow(UserNotFoundException::new);

        // encontrar a familia
        Family family = familyRepository.findById(familyId)
                .orElseThrow(FamilyNotFoundException::new);


        // verificações:

        // verifica se o novo usuario ja esta nessa família
        if (familyMemberRepository.existsByFamilyAndUser(family, member)){
            throw new UserAlreadyInFamilyException();
        }

        // verifica se o usuario que ta adcionando pertence a aquela família
        FamilyMember loggedMember = familyMemberRepository.findByFamilyAndUser(family, loggedUser)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));


        // verifica se o usuario que ta adcionando, é o admin daquela familia
        if (loggedMember.getRole() != FamilyRole.ADMIN){
            throw new AccessDeniedException("Access denied");
        }

        // criar o novo vinculo
        FamilyMember familyMember = new FamilyMember();

        familyMember.setUser(member);
        familyMember.setFamily(family);
        familyMember.setRole(FamilyRole.MEMBER);
        familyMember.setJoinedAt(LocalDateTime.now());

        familyMemberRepository.save(familyMember);

        return toFamilyMemberResponse(familyMember);

    }




    //  mappers
    public FamilyResponse toResponse(Family f){
        return new FamilyResponse(
                f.getId(),
                f.getName(),
                f.getCreatedAt(),
                f.getProfileImg()
        );
    }

    public FamilyMemberResponse toFamilyMemberResponse(FamilyMember f){
        return new FamilyMemberResponse(
                f.getId(),
                f.getFamily(),
                f.getUser(),
                f.getJoinedAt(),
                f.getRole()
        );
    }
}
