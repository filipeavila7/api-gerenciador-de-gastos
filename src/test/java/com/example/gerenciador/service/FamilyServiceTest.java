package com.example.gerenciador.service;

import com.example.gerenciador.exceptions.AccessDeniedException;
import com.example.gerenciador.exceptions.MemberLimitExceededException;
import com.example.gerenciador.family.dto.FamilyRequest;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.family.entity.FamilyMember;
import com.example.gerenciador.family.entity.FamilyRole;
import com.example.gerenciador.family.mapper.FamilyMapper;
import com.example.gerenciador.family.repository.FamilyMemberRepository;
import com.example.gerenciador.family.repository.FamilyRepository;
import com.example.gerenciador.family.service.FamilyService;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.user.entity.User;
import com.example.gerenciador.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FamilyServiceTest {

    @Mock private FamilyRepository familyRepository;
    @Mock private FamilyMemberRepository familyMemberRepository;
    @Mock private UserRepository userRepository;
    @Mock private SecurityService securityService;
    @Mock private FamilyMapper familyMapper;

    @InjectMocks
    private FamilyService familyService;


    @Test
    // teste de criar a familia com o user logado sendo o admin
    void shouldCreateFamilyAndMakeLoggedUserAdmin() {

        // given - prepara os dados
        User user = new User();
        user.setId(1L);

        // request que vai chegar para criar a familia
        FamilyRequest request = new FamilyRequest(
                "Family Test",
                "img.png"
        );

        // dizer ao mock que o ususario logado é o objeto user
        when(securityService.getLoggedUser()).thenReturn(user);

        // captura o objeto real salvo
        ArgumentCaptor<Family> familyCaptor = ArgumentCaptor.forClass(Family.class);

        // when - executa o metodo
        familyService.createFamily(request);

        // then - verifica o que aconteceu:

        // verifica se salvou a familia
        verify(familyRepository).save(familyCaptor.capture()); // garante que o save foi chamado

        Family savedFamily = familyCaptor.getValue(); // pega o objeto family

        assertEquals("Family Test", savedFamily.getName()); // verifica se tem nome
        assertEquals("img.png", savedFamily.getProfileImg()); // verifica se tem foto de perfil
        assertNotNull(savedFamily.getCreatedAt());

        // verifica se tem membro
        assertEquals(1, savedFamily.getUserMembres().size());

        // pega o usuario que criou
        FamilyMember member = savedFamily.getUserMembres().get(0);

        assertEquals(user, member.getUser()); //  verifica se foi ele que ta logado

        assertEquals(FamilyRole.ADMIN, member.getRole()); // verifica se ele é admin da familia

    }

    @Test
    // testa o metodo de adcionar novos membros a uma familia
    void shouldAddMemberWhenLoggedUserIsAdmin() {
        // admin
        User admin = new User();
        admin.setId(1L);

        // novo usuario
        User newUser = new User();
        newUser.setId(2L);

        // familia
        Family family = new Family();

        // criar a relação
        FamilyMember adminMembership = new FamilyMember();

        adminMembership.setUser(admin);
        adminMembership.setFamily(family);
        adminMembership.setRole(FamilyRole.ADMIN);

        // usuario logado é o admin
        when(securityService.getLoggedUser()).thenReturn(admin);

        // user repository deve encontrar o new user quando o id for 2
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(newUser));

        // family repository deve encontrar a familia criada quando o id for 1
        when(familyRepository.findById(1L))
                .thenReturn(Optional.of(family));

        // quando contar quantos membros tem na relação, family member, deve retornar apenas 1
        when(familyMemberRepository.countByFamilyId(1L))
                .thenReturn(1L);

        // o novo usuario não pode existir na familia, deve retornar falso
        when(familyMemberRepository.existsByFamilyAndUser(family, newUser))
                .thenReturn(false);

        // quando procurar pela familia e o usuario admin, deve retornar o usuario admin
        when(familyMemberRepository.findByFamilyAndUser(family, admin))
                .thenReturn(Optional.of(adminMembership));

        // capturar os eventos da classe
        ArgumentCaptor<FamilyMember> captor =
                ArgumentCaptor.forClass(FamilyMember.class);

        // usa a service de adcionar o novo usuario
        familyService.addNewMemberToFamily(1L, 2L);

        // verifica se salvou, ou seja o metodo cehgou no final certinho
        verify(familyMemberRepository).save(captor.capture());

        // pega o valor do obejto que foi salvo no caso o do novo usuario
        FamilyMember savedMember = captor.getValue();

        // e verifica se os seus atributos batem
        assertEquals(newUser, savedMember.getUser());
        assertEquals(family, savedMember.getFamily());
        assertEquals(FamilyRole.MEMBER, savedMember.getRole());
        assertNotNull(savedMember.getJoinedAt());
    }


    @Test
    // testar o metodo de verificar se o usuario logado é admin ou pertence aquela familia
    void shouldThrowWhenLoggedUserIsNotAdmin() {
        // cria um membro
        User member = new User();
        member.setId(1L);

        // cria um novo usuario
        User newUser = new User();
        newUser.setId(2L);

        // nova familia
        Family family = new Family();

        // nova relação
        FamilyMember membership = new FamilyMember();
        membership.setRole(FamilyRole.MEMBER);


        when(securityService.getLoggedUser()).thenReturn(member);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(newUser));

        when(familyRepository.findById(1L))
                .thenReturn(Optional.of(family));

        when(familyMemberRepository.countByFamilyId(1L))
                .thenReturn(1L);

        when(familyMemberRepository.existsByFamilyAndUser(family, newUser))
                .thenReturn(false);

        when(familyMemberRepository.findByFamilyAndUser(family, member))
                .thenReturn(Optional.of(membership));

        // se um usuario membro tentar adcionar alguem na familia, espera-se que retorne uma excessao
        assertThrows(
                AccessDeniedException.class,
                () -> familyService.addNewMemberToFamily(1L, 2L)
        );

        // verifica s enão salvou, pois é esperado que de erro
        verify(familyMemberRepository, never()).save(any());
    }

    @Test
    // teste de tentar adcionar membros em uma familia cheia
    void shouldThrowWhenFamilyIsFull() {

        User newUser = new User();
        newUser.setId(2L);

        Family family = new Family();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(newUser));

        when(familyRepository.findById(1L))
                .thenReturn(Optional.of(family));

        when(familyMemberRepository.countByFamilyId(1L))
                .thenReturn(12L);

        assertThrows(
                MemberLimitExceededException.class,
                () -> familyService.addNewMemberToFamily(1L, 2L)
        );
    }
}
