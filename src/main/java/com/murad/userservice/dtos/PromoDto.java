package com.murad.userservice.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class PromoDto {
    @Builder.Default
    private long promoId=0;
    @Builder.Default
    private String promoUsername="";
    @Builder.Default
    private String promoRole="";
}
