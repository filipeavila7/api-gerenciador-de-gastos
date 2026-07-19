package com.example.gerenciador.familyinvite.service;

import com.example.gerenciador.exceptions.InvalidInviteException;
import com.example.gerenciador.exceptions.InviteNotFoundException;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.familyinvite.dto.FamilyInviteDetailsResponse;
import com.example.gerenciador.familyinvite.dto.FamilyInviteResponse;
import com.example.gerenciador.familyinvite.entity.FamilyInvite;
import com.example.gerenciador.familyinvite.entity.InviteStatus;
import com.example.gerenciador.familyinvite.mapper.FamilyInviteMapper;
import com.example.gerenciador.familyinvite.repository.FamilyInviteRepository;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FamilyInviteService {
    private final FamilyInviteRepository familyInviteRepository;
    private final SecurityService securityService;
    private final GlobalHelperService globalHelperService;
    private final FamilyInviteMapper familyInviteMaper;

    // metodo para gerar um token aleatório
    private String generateToken(){
        return UUID.randomUUID().toString().replace("-", "");

    }

    // metodo para validar convite
    public void validateInvite(FamilyInvite invite){

        // se o status do token for diferente de pendente
        if (invite.getStatus() != InviteStatus.PENDING ){
            throw new InvalidInviteException();
        }

        // se a data de expiração for antes da de hoje -> convite expirou
        if(invite.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new InvalidInviteException();
        }
    }

    // usuario admin criar link de convite para a família
    private FamilyInviteResponse createInvite(Long familyId){
        User loggedUser = securityService.getLoggedUser();

        // verifica se a família existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // gera o token
        String token = generateToken();

        // salva no banco
        FamilyInvite familyInvite = new FamilyInvite();

        familyInvite.setToken(token);
        familyInvite.setFamily(family);
        familyInvite.setCreatedAt(LocalDateTime.now());
        familyInvite.setExpiresAt(LocalDateTime.now().plusDays(7)); // vence daqui 7 dias
        familyInvite.setStatus(InviteStatus.PENDING);
        familyInvite.setCreatedBy(loggedUser);

         return familyInviteMaper.
                 toFamilyInviteResponse(familyInviteRepository.save(familyInvite));
    }


    // retornar dados do token para o front end
    public FamilyInviteDetailsResponse getToken(String token){

        // verifica se o token existe
        FamilyInvite invite = familyInviteRepository.findByToken(token)
                .orElseThrow(InviteNotFoundException::new);

        // metodo de validação de convite
        validateInvite(invite);

        return familyInviteMaper.FamilyInviteDetailsResponse(invite);

    }


    // verifica se tem token expirado de 1 em 1 hora e expira automaticamente
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void expireInvites(){

        List<FamilyInvite> invites =
                familyInviteRepository.findByStatusAndExpiresAtBefore(
                        InviteStatus.PENDING,
                        LocalDateTime.now()
                );


        invites.forEach(invite ->
                invite.setStatus(InviteStatus.EXPIRED)
        );

    }


    // aceitar o convite



}
