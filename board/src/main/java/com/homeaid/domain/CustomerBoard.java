package com.homeaid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "customer_board")
@AllArgsConstructor
@NoArgsConstructor
public class CustomerBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 작성자
    private Long userId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

//    @OneToOne(mappedBy = "customer_board", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
//    private AdminReply adminReply;

    public void updateBoard(String title, String content) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
    }
}
