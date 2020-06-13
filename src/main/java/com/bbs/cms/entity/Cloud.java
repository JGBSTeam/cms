package com.bbs.cms.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Cloud {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "MEDIUMINT(8) UNSIGNED")
    private int idx;

    @Column(length=50, unique = true, nullable = false, updatable = false)
    private String username;

    @Column(length = 50, nullable = false)
    private String cloudname;

    @Column(columnDefinition = "MEDIUMINT(8) UNSIGNED", nullable = false)
    private int innerport;

    @Column(columnDefinition = "MEDIUMINT(8) UNSIGNED", nullable = false)
    private int outerport;

    @Column(nullable = false, insertable = false, updatable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private Timestamp createDate;
}