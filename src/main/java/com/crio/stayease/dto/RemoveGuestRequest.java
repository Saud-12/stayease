package com.crio.stayease.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveGuestRequest {
    @Size(min=1,max=2,message="provide 1 or 2 guests to be removed")
    private List<Long> guestIds=new ArrayList<>();
}
