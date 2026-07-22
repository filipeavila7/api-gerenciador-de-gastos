package com.example.gerenciador.familyinvite.mapper;

import com.example.gerenciador.familyinvite.dto.FamilyInviteDetailsResponse;
import com.example.gerenciador.familyinvite.dto.FamilyInviteResponse;
import com.example.gerenciador.familyinvite.entity.FamilyInvite;
import com.example.gerenciador.utils.uploads.service.FileUrlUtils;
import org.springframework.stereotype.Component;

@Component
public class FamilyInviteMapper {

    public FamilyInviteResponse toFamilyInviteResponse(FamilyInvite f){
        return new FamilyInviteResponse(
                f.getToken(),
                f.getExpiresAt()
        );
    }

    public FamilyInviteDetailsResponse FamilyInviteDetailsResponse(FamilyInvite f){
        return new FamilyInviteDetailsResponse(
                f.getFamily().getName(),
                f.getCreatedBy().getName(),
                f.getExpiresAt(),
                f.getStatus(),
                f.getFamily().getId(),
                FileUrlUtils.toPublicUrl(f.getFamily().getProfileImg())
        );
    }
}
