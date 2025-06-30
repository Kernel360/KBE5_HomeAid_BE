package com.homeaid.domain.enumerate;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
// 알림 상태
public enum NotificationStatus {
    UNREAD("읽지 않음"),
    READ("읽음"),
    DELETED("삭제됨");

    private final String description;
}