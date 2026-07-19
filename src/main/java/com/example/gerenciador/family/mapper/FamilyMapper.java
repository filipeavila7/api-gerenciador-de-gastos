package com.example.gerenciador.family.mapper;

import com.example.gerenciador.family.dto.FamilyMemberResponse;
import com.example.gerenciador.family.dto.FamilyResponse;
import com.example.gerenciador.family.dto.MemberResponse;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.family.entity.FamilyMember;
import com.example.gerenciador.utils.uploads.service.FileUrlUtils;
import org.springframework.stereotype.Component;

@Component
public class FamilyMapper {
    public FamilyResponse toResponse(Family f){
        return new FamilyResponse(
                f.getId(),
                f.getName(),
                f.getCreatedAt(),
                FileUrlUtils.toPublicUrl(f.getProfileImg()),
                f.getUserMembres().size()
        );
    }

    public FamilyMemberResponse toFamilyMemberResponse(FamilyMember f){
        return new FamilyMemberResponse(
                f.getId(),
                f.getFamily().getId(),
                f.getUser().getId(),
                f.getUser().getName(),
                FileUrlUtils.toPublicUrl(f.getUser().getProfileImg()),
                f.getJoinedAt(),
                f.getRole()
        );
    }


    public MemberResponse toMemberResponse(FamilyMember f){
        return new MemberResponse(
                f.getId(),
                f.getUser().getName(),
                FileUrlUtils.toPublicUrl(f.getUser().getProfileImg()),
                f.getRole(),
                f.getJoinedAt()
        );
    }
}
