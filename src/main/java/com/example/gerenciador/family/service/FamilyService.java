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
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.user.entity.User;
import com.example.gerenciador.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    // ================ GET ======================

    // ver todas as familias em que o usuario logado esta ou tem
    public List<FamilyResponse> getMyFamilies(){
        User loggedUser = securityService.getLoggedUser();

        // procura as familias em que o user logado esta
        List<FamilyMember> memberships = familyMemberRepository.findByUserId(loggedUser.getId());

        // pegar somente as famílias do FamilyMember
        return memberships.stream()
                .map(FamilyMember::getFamily)
                .map(familyMapper::toResponse)
                .toList();
    }

    // pegar somente uma familia pelo id
    public FamilyResponse getFamily(Long familyId){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario pertenca a aquela familia
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return familyMapper.toResponse(family);

    }


    // ver os membros de uma familia
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

        return familyMapper.toResponse(family);

    }

    // usuário Admin adcionar novos membros as famílias
    @Transactional
    public FamilyMemberResponse addNewMemberToFamily(Long familyId, Long memberId){

        User loggedUser = securityService.getLoggedUser();

        // encontra o membro novo
        User member = userRepository.findById(memberId)
                .orElseThrow(UserNotFoundException::new);

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se a familia não esta cheia antes de adcionar um novo membro
        if (familyMemberRepository.countByFamilyId(familyId) >= 12) {
            throw new MemberLimitExceededException();
        }


        // verifica se o novo usuario ja esta nessa família
        if (familyMemberRepository.existsByFamilyAndUser(family, member)){
            throw new UserAlreadyInFamilyException();
        }

        // verifica se o usuario que ta adcionando pertence aquela família e é admin
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // criar vinculo
        FamilyMember familyMember = new FamilyMember();

        familyMember.setUser(member);
        familyMember.setFamily(family);
        familyMember.setRole(FamilyRole.MEMBER);
        familyMember.setJoinedAt(LocalDateTime.now());

        familyMemberRepository.save(familyMember);

        return  familyMapper.toFamilyMemberResponse(familyMember);

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


        familyMemberRepository.delete(member);


    }






    // ================ TODOLIST ======================

    // todo - (X) criar metodo de admin da familia remover membros
    // todo - (X) criar metodo para um usuario sair de uma familia e caso ele seja admin, o cargo vai pro user mais antigo
    // todo - (X) criar metodo para um admin tornar um membro admin
    // todo - (X) criar metodo para editar dados da familia
    // todo - (x) criar verificação maxima de membros em uma familia - max
    // todo - (X) criar crud para usuario admins




}
