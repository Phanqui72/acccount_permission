package com.mgr.api.model;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = TablePrefix.PREFIX_TABLE + "user")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User extends Auditable<String> {

    @OneToOne
    @MapsId // Dùng ID của Account làm ID của User
    @JoinColumn(name = "id")
    private Account account;

    private Integer gender; // 0: Nữ, 1: Nam, 2: Khác

    @Temporal(TemporalType.DATE)
    private Date birthday;
}