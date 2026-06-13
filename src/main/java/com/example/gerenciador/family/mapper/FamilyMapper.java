package com.example.gerenciador.family.mapper;

import com.example.gerenciador.family.dto.FamilyMemberResponse;
import com.example.gerenciador.family.dto.FamilyResponse;
import com.example.gerenciador.family.dto.MemberResponse;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.family.entity.FamilyMember;
import org.springframework.stereotype.Component;

@Component
public class FamilyMapper {
    public FamilyResponse toResponse(Family f){
        return new FamilyResponse(
                f.getId(),
                f.getName(),
                f.getCreatedAt(),
                f.getProfileImg(),
                f.getUserMembres().size()
        );
    }

    public FamilyMemberResponse toFamilyMemberResponse(FamilyMember f){
        return new FamilyMemberResponse(
                f.getId(),
                f.getFamily().getId(),
                f.getUser().getId(),
                f.getUser().getName(),
                f.getUser().getProfileImg(),
                f.getJoinedAt(),
                f.getRole()
        );
    }


    public MemberResponse toMemberResponse(FamilyMember f){
        return new MemberResponse(
                f.getUser().getName(),
                f.getUser().getProfileImg(),
                f.getRole(),
                f.getJoinedAt()
        );
    }
}
