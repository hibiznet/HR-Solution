package com.hibiznet.hr.code.dto;

import jakarta.validation.constraints.*;

public class CodeManagementDtos {
    public record CodeGroupRequest(@NotBlank String groupCode, @NotBlank String groupName, String description, @NotNull Boolean isActive) {}
    public record CodeGroupResponse(Long id, String groupCode, String groupName, String description, Boolean isActive) {}
    public record CodeDetailRequest(@NotNull Long groupId, @NotBlank String detailCode, @NotBlank String detailName, Integer sortOrder, @NotNull Boolean isActive) {}
    public record CodeDetailResponse(Long id, Long groupId, String groupCode, String detailCode, String detailName, Integer sortOrder, Boolean isActive) {}
}
