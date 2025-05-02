package com.murad.userservice.models;

import com.murad.userservice.entities.RolesEntity;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class User {
    private long             id;
    private String           username;
    private Set<RolesEntity> role;
    private String           email;
    private String           friendPromoCode;
    private long             friendPromoId;
    private String           myPromoCode;
    private String           friendPromoRole;
    private String           firstName;
    private String           lastName;
}
