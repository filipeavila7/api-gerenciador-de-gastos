package com.example.gerenciador.familyinvite.service;

import com.example.gerenciador.exceptions.*;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.family.entity.FamilyMember;
import com.example.gerenciador.family.entity.FamilyRole;
import com.example.gerenciador.family.repository.FamilyMemberRepository;
import com.example.gerenciador.familyinvite.dto.FamilyInviteDetailsResponse;
import com.example.gerenciador.familyinvite.dto.FamilyInviteResponse;
import com.example.gerenciador.familyinvite.entity.FamilyInvite;
import com.example.gerenciador.familyinvite.entity.InviteStatus;
import com.example.gerenciador.familyinvite.mapper.FamilyInviteMapper;
import com.example.gerenciador.familyinvite.repository.FamilyInviteRepository;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.history.entity.HistoryAction;
import com.example.gerenciador.history.service.HistoryService;
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
    private final FamilyInviteMapper familyInviteMapper;
    private final FamilyMemberRepository familyMemberRepository;
    private final HistoryService historyService;

    // metodo para gerar um token aleatório
    private String generateToken(){
        return UUID.randomUUID().toString().replace("-", "");

    }

    // metodo para validar convite
    private void validateInvite(FamilyInvite invite){

        // se o status do token for diferente de pendente
        if (invite.getStatus() != InviteStatus.PENDING ){
            throw new InvalidInviteException();
        }

        // se a data de expiração for antes da de hoje -> convite expirou
        if(invite.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new InviteExpiredException();
        }
    }

    // usuario admin criar link de convite para a família
    @Transactional
    public FamilyInviteResponse createInvite(Long familyId){
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

         return familyInviteMapper.
                 toFamilyInviteResponse(familyInviteRepository.save(familyInvite));
    }


    // retornar dados do token para o front end
    public FamilyInviteDetailsResponse getToken(String token){

        // verifica se o token existe
        FamilyInvite invite = familyInviteRepository.findByToken(token)
                .orElseThrow(InviteNotFoundException::new);

        // metodo de validação de convite
        validateInvite(invite);

        return familyInviteMapper.FamilyInviteDetailsResponse(invite);

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
    @Transactional
    public void acceptInvite(String token){
        User user = securityService.getLoggedUser();

        // verifica se o convite existe
        FamilyInvite invite = familyInviteRepository
                .findByToken(token)
                .orElseThrow(InviteNotFoundException::new);

        // valida
        validateInvite(invite);


        // pega a familia do convite
        Family family = invite.getFamily();

        // verifica se o usuário ja está nela
        if(familyMemberRepository.existsByFamilyAndUser(
                family,
                user
        )){
            throw new UserAlreadyInFamilyException();
        }

        // verifica se a família esta cheia
        if(familyMemberRepository.countByFamilyId(family.getId()) >= 12){
            throw new MemberLimitExceededException();
        }

        // cria o vinculo
        FamilyMember member = new FamilyMember();

        member.setFamily(family);
        member.setUser(user);
        member.setRole(FamilyRole.MEMBER);
        member.setJoinedAt(LocalDateTime.now());

        // salva
        familyMemberRepository.save(member);

        // marca o status como accepted
        invite.setStatus(InviteStatus.ACCEPTED);


        historyService.createHistory(
                user.getName() + " entrou na família",
                family,
                user,
                HistoryAction.ADDED_MEMBER
        );
    }

    // usuario admin que criou o convite pode cancelar ele, invalida o token
    @Transactional
    public void cancelInvite(String token) {

        User loggedUser = securityService.getLoggedUser();


        FamilyInvite invite = familyInviteRepository.findByToken(token)
                .orElseThrow(InviteNotFoundException::new);


        Family family = invite.getFamily();


        // verifica se quem cancelou é admin da família
        globalHelperService.getAdminMemberOrThrow(
                family,
                loggedUser
        );


        if(invite.getStatus() != InviteStatus.PENDING){
            throw new InvalidInviteException();
        }


        invite.setStatus(InviteStatus.CANCELLED);

    }

}
