package com.example.gerenciador.family.service;

import com.example.gerenciador.exceptions.*;
import com.example.gerenciador.family.dto.*;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.family.entity.FamilyMember;
import com.example.gerenciador.family.entity.FamilyRole;
import com.example.gerenciador.family.mapper.FamilyMapper;
import com.example.gerenciador.family.repository.FamilyMemberRepository;
import com.example.gerenciador.family.repository.FamilyRepository;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.history.entity.HistoryAction;
import com.example.gerenciador.history.service.HistoryService;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.user.entity.User;
import com.example.gerenciador.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepository familyRepository;
    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyMapper familyMapper;
    private final GlobalHelperService globalHelperService;
    private final HistoryService historyService;


    // ================ GET ======================

    // ver todas as familias em que o usuario logado esta ou tem
    @Transactional(readOnly = true)
    public Page<FamilyResponse> getMyFamilies(Pageable pageable){
        User loggedUser = securityService.getLoggedUser();

        // procura as familias em  que o user logado esta


        // pegar somente as famílias do FamilyMember
        return familyMemberRepository.findByUserId(loggedUser.getId(), pageable)
                .map(FamilyMember::getFamily)
                .map(familyMapper::toResponse);
    }

    // pegar somente uma familia pelo id
    @Transactional(readOnly = true)
    public FamilyResponse getFamily(Long familyId){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario pertenca a aquela familia
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return familyMapper.toResponse(family);

    }


    // ver os membros de uma familia
    @Transactional(readOnly = true)
    public List<MemberResponse> getFamilyMembers(Long familyId){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // so pode ver os membros caso o usuario pertenca a aquela familia
        familyMemberRepository.findByFamilyAndUser(family, loggedUser)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));


        List<FamilyMember> memberships = familyMemberRepository.findByFamilyId(familyId);

        return memberships.stream()
                .map(familyMapper::toMemberResponse)
                .toList();

    }

    // metodo para pegar a role do user logado dependendo da familia
    public FamilyRole getMyRole (Long familyId){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se é da familia e retorna a role
        return globalHelperService.
                getMemberOrThrow(family, loggedUser)
                .getRole();
    }

    // ================ POST ======================

    // criar família com o user logado sendo o admin
    @Transactional
    public FamilyResponse createFamily (FamilyRequest request ) {
        User loggedUser = securityService.getLoggedUser();

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
        member.setJoinedAt(LocalDateTime.now());

        // adciona o membro do outro lado
        family.getUserMembres().add(member);

        familyRepository.save(family);

        String message = "criou a família";

        historyService.createHistory(message, family, loggedUser, HistoryAction.CREATED_FAMILY);

        return familyMapper.toResponse(family);

    }

    // usuário Admin adcionar novos membros as famílias
    // esse metodo será usado para outros fins
    @Transactional
    public FamilyMemberResponse addNewMemberToFamily(Long familyId, Long memberId){

        User loggedUser = securityService.getLoggedUser();


        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se a familia não esta cheia antes de adcionar um novo membro
        if (familyMemberRepository.countByFamilyId(familyId) >= 12) {
            throw new MemberLimitExceededException();
        }

        // verifica se o usuario que ta adcionando pertence aquela família e é admin
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // encontra o membro novo
        User member = userRepository.findById(memberId)
                .orElseThrow(UserNotFoundException::new);


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

        String message = "Adcionou um membro à família";

        historyService.createHistory(message, family, loggedUser, HistoryAction.ADDED_MEMBER);

        return familyMapper.toFamilyMemberResponse(familyMember);

    }

    // ================ PUT ======================

    @Transactional
    // admin trnasformar membros em admin
    public MemberResponse changeMemberToAdmin(Long familyId, Long userId){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

       // verifica se o usuario pertence aquela familia e é admin
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        FamilyMember member = familyMemberRepository.findByFamilyIdAndUserId(familyId, userId)
                .orElseThrow(UserNotFoundException::new);

        if (member.getRole() == FamilyRole.ADMIN){
            return familyMapper.toMemberResponse(member);
        }

        member.setRole(FamilyRole.ADMIN);

        familyMemberRepository.save(member);

        String message = "tornou um membro admnistrador da familia";

        historyService.createHistory(message, family, loggedUser, HistoryAction.CHANGE_MEMBER);

        return familyMapper.toMemberResponse(member);


    }


    // editar dados da familia
    @Transactional
    public FamilyResponse updateFamily (Long familyId, FamilyUpdateRequest request){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin e pértence aquela familia antes de editar
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // atualizar os dados
        if (request.name() != null){
            family.setName(request.name());
        }

        if (request.profileImg() != null){
            family.setProfileImg(request.profileImg());
        }

        familyRepository.save(family);

        String message = "editou dados da família";

        historyService.createHistory(message, family, loggedUser, HistoryAction.UPDATED_FAMILY);

        return familyMapper.toResponse(family);
    }




    // ================ DELETE ======================

    // apagar familia
    @Transactional
    public void deleteFamily(Long familyId){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuariom logado pertence aquela família e é admin
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);


        familyRepository.delete(family);

    }

    // usuario admin remover usuarios membros da familia
    @Transactional
    public void removeMemberFromFamily(Long memberId, Long familyId) {
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario pertence aquela família ou é admin
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // acha o membro que será removido
        FamilyMember memberToRemove = familyMemberRepository.findByFamilyIdAndUserId(familyId, memberId)
                .orElseThrow(UserNotFoundException::new);

        // so remove se ele não for admin
        if (memberToRemove.getRole() != FamilyRole.MEMBER) {
            throw new CannotRemoveAdminException();
        }

        String message = "removeu um membro da família";

        historyService.createHistory(message, family, loggedUser, HistoryAction.REMOVED_MEMBER);

        familyMemberRepository.delete(memberToRemove);

    }

    // usuario logado sair da familía e caso ele for admin, deixar cargo eadmin para o proximo membro mais antigo
    @Transactional
    public void exitFromFamily(Long familyId){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // caso ele seja o último da família, ele não pode sair
        List<FamilyMember> members = familyMemberRepository.findByFamilyId(familyId);

        if (members.size() == 1){
            throw new CannotLeaveFamilyException();
        }

        FamilyMember member = familyMemberRepository.findByFamilyIdAndUserId(familyId, loggedUser.getId())
                .orElseThrow(UserNotFoundException::new);


        if (member.getRole() == FamilyRole.ADMIN){
            // filtrar somente os usuarios que não são o logado
            List<FamilyMember> candidates = members.stream()
                    .filter(m -> !m.getUser().getId().equals(loggedUser.getId()))
                    .toList();

            // com os usuarios filtrados, passaremos os privilegios de admin para o mais antigo
            FamilyMember newAdmin = candidates.stream()
                    // pegar o menor valor do joinedAt, o mais antigo
                    .min(Comparator.comparing(FamilyMember::getJoinedAt))
                    .orElseThrow(() -> new IllegalStateException("Não há outro membro na família"));

            // promove ele
            newAdmin.setRole(FamilyRole.ADMIN);
            familyMemberRepository.save(newAdmin);
        }

        String message = "saiu da família ";

        historyService.createHistory(message, family, loggedUser, HistoryAction.EXIT_MEMBER);

        familyMemberRepository.delete(member);


    }


}
