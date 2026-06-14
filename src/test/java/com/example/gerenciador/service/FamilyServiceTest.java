package com.example.gerenciador.service;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        Family savedFamily = familyCaptor.getValue();

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
}
