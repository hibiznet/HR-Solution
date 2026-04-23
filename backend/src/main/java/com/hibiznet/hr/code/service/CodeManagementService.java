package com.hibiznet.hr.code.service;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.code.dto.CodeManagementDtos.*;
import com.hibiznet.hr.code.entity.CodeDetail;
import com.hibiznet.hr.code.entity.CodeGroup;
import com.hibiznet.hr.code.repository.CodeDetailRepository;
import com.hibiznet.hr.code.repository.CodeGroupRepository;
import com.hibiznet.hr.common.audit.annotation.Auditable;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.security.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodeManagementService {

    private final CodeGroupRepository codeGroupRepository;
    private final CodeDetailRepository codeDetailRepository;
    private final CurrentUserService currentUserService;

    public PageResponse<CodeGroupResponse> getGroups(AuthenticatedUser actor, String keyword, Boolean isActive, int page, int size) {
        currentUserService.assertCanManageCodes(actor);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "groupCode", "id"));
        return PageResponse.from(codeGroupRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("groupCode")), like), cb.like(cb.lower(root.get("groupName")), like), cb.like(cb.lower(root.get("description")), like)));
            }
            if (isActive != null) predicates.add(cb.equal(root.get("isActive"), isActive));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(this::toGroupResponse));
    }

    public CodeGroupResponse getGroup(AuthenticatedUser actor, Long id) {
        currentUserService.assertCanManageCodes(actor);
        return toGroupResponse(findGroup(id));
    }

    @Transactional
    @Auditable(action = "CREATE", target = "code_group")
    public CodeGroupResponse createGroup(AuthenticatedUser actor, CodeGroupRequest request) {
        currentUserService.assertCanManageCodes(actor);
        CodeGroup entity = codeGroupRepository.save(CodeGroup.builder()
            .groupCode(request.groupCode())
            .groupName(request.groupName())
            .description(request.description())
            .isActive(request.isActive())
            .build());
        return toGroupResponse(entity);
    }

    @Transactional
    @Auditable(action = "UPDATE", target = "code_group")
    public CodeGroupResponse updateGroup(AuthenticatedUser actor, Long id, CodeGroupRequest request) {
        currentUserService.assertCanManageCodes(actor);
        CodeGroup entity = findGroup(id);
        entity.update(request.groupCode(), request.groupName(), request.description(), request.isActive());
        return toGroupResponse(entity);
    }

    @Transactional
    @Auditable(action = "DELETE", target = "code_group")
    public void deleteGroup(AuthenticatedUser actor, Long id) {
        currentUserService.assertCanManageCodes(actor);
        codeGroupRepository.delete(findGroup(id));
    }

    public PageResponse<CodeDetailResponse> getDetails(AuthenticatedUser actor, String keyword, Long groupId, Boolean isActive, int page, int size) {
        currentUserService.assertCanManageCodes(actor);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "codeGroup.id", "sortOrder", "id"));
        return PageResponse.from(codeDetailRepository.findAll((root, query, cb) -> {
            //root.fetch("codeGroup", jakarta.persistence.criteria.JoinType.LEFT);
            //query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("detailCode")), like), cb.like(cb.lower(root.get("detailName")), like)));
            }
            if (groupId != null) predicates.add(cb.equal(root.get("codeGroup").get("id"), groupId));
            if (isActive != null) predicates.add(cb.equal(root.get("isActive"), isActive));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(this::toDetailResponse));
    }

    public CodeDetailResponse getDetail(AuthenticatedUser actor, Long id) {
        currentUserService.assertCanManageCodes(actor);
        return toDetailResponse(findDetail(id));
    }

    @Transactional
    @Auditable(action = "CREATE", target = "code_detail")
    public CodeDetailResponse createDetail(AuthenticatedUser actor, CodeDetailRequest request) {
        currentUserService.assertCanManageCodes(actor);
        CodeGroup group = findGroup(request.groupId());
        CodeDetail entity = codeDetailRepository.save(CodeDetail.builder()
            .codeGroup(group)
            .detailCode(request.detailCode())
            .detailName(request.detailName())
            .sortOrder(request.sortOrder())
            .isActive(request.isActive())
            .build());
        return toDetailResponse(entity);
    }

    @Transactional
    @Auditable(action = "UPDATE", target = "code_detail")
    public CodeDetailResponse updateDetail(AuthenticatedUser actor, Long id, CodeDetailRequest request) {
        currentUserService.assertCanManageCodes(actor);
        CodeGroup group = findGroup(request.groupId());
        CodeDetail entity = findDetail(id);
        entity.update(group, request.detailCode(), request.detailName(), request.sortOrder(), request.isActive());
        return toDetailResponse(entity);
    }

    @Transactional
    @Auditable(action = "DELETE", target = "code_detail")
    public void deleteDetail(AuthenticatedUser actor, Long id) {
        currentUserService.assertCanManageCodes(actor);
        codeDetailRepository.delete(findDetail(id));
    }

    private CodeGroup findGroup(Long id) {
        return codeGroupRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("코드 그룹을 찾을 수 없습니다."));
    }

    private CodeDetail findDetail(Long id) {
        return codeDetailRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("코드 상세를 찾을 수 없습니다."));
    }

    private CodeGroupResponse toGroupResponse(CodeGroup entity) {
        return new CodeGroupResponse(entity.getId(), entity.getGroupCode(), entity.getGroupName(), entity.getDescription(), entity.getIsActive());
    }

    private CodeDetailResponse toDetailResponse(CodeDetail entity) {
        return new CodeDetailResponse(entity.getId(), entity.getCodeGroup().getId(), entity.getCodeGroup().getGroupCode(), entity.getDetailCode(), entity.getDetailName(), entity.getSortOrder(), entity.getIsActive());
    }
}
