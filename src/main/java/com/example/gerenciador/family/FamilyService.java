package com.example.gerenciador.family;

import com.example.gerenciador.exceptions.UserNotFoundException;
import com.example.gerenciador.family.dto.FamilyRequest;
import com.example.gerenciador.family.dto.FamilyResponse;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.user.User;
import com.example.gerenciador.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepository familyRepository;
    private final SecurityService securityService;
    private final UserRepository userRepository;


    // criar família com o user logado sendo o admin dela
    public FamilyResponse createFamily (FamilyRequest request ) {
        User loggedUser = securityService.getLoggedUser();

        userRepository.findByEmail(loggedUser.getEmail())
                .orElseThrow(UserNotFoundException::new);

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

    public FamilyResponse toResponse(Family f){
        return new FamilyResponse(
                f.getId(),
                f.getName(),
                f.getCreatedAt(),
                f.getProfileImg()
        );
    }
}
