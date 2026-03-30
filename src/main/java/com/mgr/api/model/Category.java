package com.mgr.api.model;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;


@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = TablePrefix.PREFIX_TABLE + "category")
@Entity
public class Category extends Auditable<String>{
    private String name;

    @Column(name = "description", columnDefinition = "Text" )//biến đổi thành kiểu Text
    private String description;
}
