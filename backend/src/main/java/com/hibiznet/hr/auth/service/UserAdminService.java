package com.hibiznet.hr.auth.service;

import com.hibiznet.hr.auth.dto.UserAdminDtos.*;
import com.hibiznet.hr.auth.entity.Role;
import com.hibiznet.hr.auth.entity.UserAccount;
import com.hibiznet.hr.auth.entity.UserRole;
import com.hibiznet.hr.auth.repository.RoleRepository;
import com.hibiznet.hr.auth.repository.UserAccountRepository;
import com.hibiznet.hr.auth.repository.UserRoleRepository;
import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.audit.annotation.Auditable;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.security.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAdminService {

    private final UserAccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public PageResponse<UserSearchResponse> getUsers(AuthenticatedUser actor, String keyword, Boolean isActive, int page, int size) {
        currentUserService.assertCanManageUsers(actor);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username", "id"));
        return PageResponse.from(userAccountRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("username")), like), cb.like(cb.lower(root.get("email")), like)));
            }
            if (isActive != null) predicates.add(cb.equal(root.get("isActive"), isActive));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(this::toSearchResponse));
    }

    public UserDetailResponse getUser(AuthenticatedUser actor, Long id) {
        currentUserService.assertCanManageUsers(actor);
        return toDetailResponse(findUser(id));
    }

    public List<RoleResponse> getRoles(AuthenticatedUser actor) {
        currentUserService.assertCanManageUsers(actor);
        return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "roleCode")).stream()
            .map(r -> new RoleResponse(r.getId(), r.getRoleCode(), r.getRoleName(), r.getDescription()))
            .toList();
    }

    @Transactional
    @Auditable(action = "CREATE", target = "user_account")
    public UserDetailResponse createUser(AuthenticatedUser actor, UserUpsertRequest request) {
        currentUserService.assertCanManageUsers(actor);
        if (userAccountRepository.existsByUsername(request.username())) throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        if (userAccountRepository.existsByEmail(request.email())) throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        String password = (request.password() == null || request.password().isBlank()) ? "ChangeMe123!" : request.password();
        UserAccount user = userAccountRepository.save(UserAccount.builder()
            .username(request.username())
            .email(request.email())
            .passwordHash(passwordEncoder.encode(password))
            .isActive(request.isActive())
            .build());
        replaceRoles(user, request.roleCodes());
        return toDetailResponse(user);
    }

    @Transactional
    @Auditable(action = "UPDATE", target = "user_account")
    public UserDetailResponse updateUser(AuthenticatedUser actor, Long id, UserUpsertRequest request) {
        currentUserService.assertCanManageUsers(actor);
        UserAccount user = findUser(id);
        if (userAccountRepository.existsByUsernameAndIdNot(request.username(), id)) throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        if (userAccountRepository.existsByEmailAndIdNot(request.email(), id)) throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        user.update(request.username(), request.email(), request.isActive());
        if (request.password() != null && !request.password().isBlank()) {
            user.updatePasswordHash(passwordEncoder.encode(request.password()));
        }
        replaceRoles(user, request.roleCodes());
        return toDetailResponse(user);
    }

    @Transactional
    @Auditable(action = "DELETE", target = "user_account")
    public void deleteUser(AuthenticatedUser actor, Long id) {
        currentUserService.assertCanManageUsers(actor);
        UserAccount user = findUser(id);
        userRoleRepository.deleteByUserAccount(user);
        userAccountRepository.delete(user);
    }

    private void replaceRoles(UserAccount user, List<String> roleCodes) {
        List<Role> roles = roleRepository.findByRoleCodeIn(roleCodes);
        if (roles.isEmpty()) throw new IllegalArgumentException("유효한 역할이 없습니다.");
        userRoleRepository.deleteByUserAccount(user);
        userRoleRepository.saveAll(roles.stream().map(role -> UserRole.builder().userAccount(user).role(role).build()).toList());
    }

    private UserAccount findUser(Long id) {
        return userAccountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("사용자 계정을 찾을 수 없습니다."));
    }

    private List<String> roleCodes(UserAccount user) {
        return userRoleRepository.findByUserAccount(user).stream().map(ur -> ur.getRole().getRoleCode()).sorted().collect(Collectors.toList());
    }

    private UserSearchResponse toSearchResponse(UserAccount user) {
        return new UserSearchResponse(user.getId(), user.getUsername(), user.getEmail(), user.getIsActive(), roleCodes(user));
    }

    private UserDetailResponse toDetailResponse(UserAccount user) {
        return new UserDetailResponse(user.getId(), user.getUsername(), user.getEmail(), user.getIsActive(), roleCodes(user));
    }
}
