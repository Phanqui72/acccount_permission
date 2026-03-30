package com.mgr.api.model.criteria;

import com.mgr.api.model.News;
import com.mgr.api.model.Category;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class NewsCriteria implements Serializable {
    private Long id;
    private String title;
    private Long categoryId;
    private Integer status;

    public Specification<News> getSpecification() {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            if (!StringUtils.isEmpty(title)) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (categoryId != null) {
                // Join với bảng Category để lọc theo ID
                Join<News, Category> joinCategory = root.join("category");
                predicates.add(cb.equal(joinCategory.get("id"), categoryId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Sắp xếp mặc định tin mới nhất lên đầu
            query.orderBy(cb.desc(root.get("createdDate")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}