package com.homeaid.domain.enumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserType {
    CUSTOMER("고객"),
    MANAGER("매니저"),
    ADMIN("관리자"),
    SYSTEM("시스템");

    private final String description;
}