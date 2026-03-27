package com.mgr.api.model.criteria;

import com.mgr.api.model.Category;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryCriteria implements Serializable {
    private String name;
    private Integer status;

    public Specification<Category> getSpecification() {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}