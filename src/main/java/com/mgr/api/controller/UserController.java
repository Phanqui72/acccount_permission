package com.mgr.api.controller;

import com.mgr.api.constant.MgrConstant;
import com.mgr.api.dto.ApiMessageDto;
import com.mgr.api.dto.ErrorCode;
import com.mgr.api.dto.ResponseListDto;
import com.mgr.api.dto.user.UserDto;
import com.mgr.api.exception.BadRequestException;
import com.mgr.api.exception.NotFoundException;
import com.mgr.api.form.user.CreateUserForm;
import com.mgr.api.form.user.UpdateUserForm;
import com.mgr.api.mapper.UserMapper;
import com.mgr.api.model.Account;
import com.mgr.api.model.Group;
import com.mgr.api.model.User;
import com.mgr.api.model.criteria.UserCriteria;
import com.mgr.api.repository.AccountRepository;
import com.mgr.api.repository.GroupRepository;
import com.mgr.api.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class UserController extends ABasicController {

    @Autowired private UserRepository userRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private GroupRepository groupRepository;
    @Autowired private UserMapper userMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER_C')")
    @Transactional
    public ApiMessageDto<UserDto> create(@Valid @RequestBody CreateUserForm form, BindingResult bindingResult) {
        // 1. Kiểm tra username tồn tại
        if (accountRepository.findFirstByUsername(form.getUsername()).isPresent()) {
            throw new BadRequestException("Username existed", ErrorCode.ACCOUNT_ERROR_USERNAME_EXISTED);
        }

        // 2. Tìm Group (mặc định cho User thường)
        Group group = groupRepository.findById(form.getGroupId())
                .orElseThrow(() -> new NotFoundException("Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND));

        // 3. Tạo Account
        Account account = new Account();
        account.setUsername(form.getUsername());
        account.setPassword(passwordEncoder.encode(form.getPassword()));
        account.setFullName(form.getFullName());
        account.setEmail(form.getEmail());
        account.setPhone(form.getPhone());
        account.setKind(MgrConstant.USER_KIND_USER); // Kind = 2
        account.setGroup(group);
        account.setStatus(MgrConstant.STATUS_ACTIVE);
        accountRepository.save(account);

        // 4. Tạo User chi tiết (MapsId sẽ dùng chung ID với account)
        User user = new User();
        user.setAccount(account);
        user.setGender(form.getGender());
        user.setBirthday(form.getBirthday());
        user.setStatus(MgrConstant.STATUS_ACTIVE);

        return makeSuccessResponse(userMapper.fromEntityToDto(userRepository.save(user)), "Create user success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER_U')")
    @Transactional
    public ApiMessageDto<UserDto> update(@Valid @RequestBody UpdateUserForm form, BindingResult bindingResult) {
        User user = userRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("User not found", "ERROR-USER-001"));

        Account account = user.getAccount();

        // Cập nhật Account
        account.setFullName(form.getFullName());
        account.setEmail(form.getEmail());
        account.setPhone(form.getPhone());
        if (StringUtils.isNoneBlank(form.getPassword())) {
            account.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        account.setStatus(form.getStatus());
        accountRepository.save(account);

        // Cập nhật User
        user.setGender(form.getGender());
        user.setBirthday(form.getBirthday());
        user.setStatus(form.getStatus());

        return makeSuccessResponse(userMapper.fromEntityToDto(userRepository.save(user)), "Update user success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER_V')")
    public ApiMessageDto<UserDto> get(@PathVariable("id") Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", "ERROR-USER-001"));
        return makeSuccessResponse(userMapper.fromEntityToDto(user), "Get user success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER_L')")
    public ApiMessageDto<ResponseListDto<List<UserDto>>> list(UserCriteria criteria, Pageable pageable) {
        Page<User> page = userRepository.findAll(criteria.getSpecification(), pageable);
        ResponseListDto<List<UserDto>> response = makeResponseListDto(page, list -> userMapper.fromEntityListToDtoList(list));
        return makeSuccessResponse(response, "Get list user success");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER_D')")
    @Transactional
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", "ERROR-USER-001"));

        // Xóa User chi tiết trước, sau đó xóa Account
        userRepository.delete(user);
        accountRepository.delete(user.getAccount());

        return makeSuccessResponse("Delete user success");
    }
}