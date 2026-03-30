package com.mgr.api.model.criteria;

import com.mgr.api.model.User;
import com.mgr.api.model.Account;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserCriteria implements Serializable {
    private Long id;
    private String username;
    private String fullName;
    private Integer gender;
    private Integer status;

    public Specification<User> getSpecification() {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }

            // Join với bảng Account để lấy thông tin đăng nhập
            Join<User, Account> joinAccount = root.join("account");

            if (!StringUtils.isEmpty(username)) {
                predicates.add(cb.like(cb.lower(joinAccount.get("username")), "%" + username.toLowerCase() + "%"));
            }
            if (!StringUtils.isEmpty(fullName)) {
                predicates.add(cb.like(cb.lower(joinAccount.get("fullName")), "%" + fullName.toLowerCase() + "%"));
            }
            if (gender != null) {
                predicates.add(cb.equal(root.get("gender"), gender));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            query.orderBy(cb.desc(root.get("createdDate")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}