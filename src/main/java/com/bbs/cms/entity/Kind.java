package com.bbs.cms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Kind {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "MEDIUMINT(8) UNSIGNED")
    private int idx;

    @Column(length=20, unique = true, nullable = false, updatable = false)
    private String kindName;

    @Column(length=20, unique = true, nullable = false, updatable = false)
    private String image;

    @Column(length=20, nullable = false)
    private String tag;

    @Column(columnDefinition = "MEDIUMINT(8) UNSIGNED")
    private int innerPort;
}