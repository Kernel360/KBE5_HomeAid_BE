package com.example.homeaid.customerboard.entity;


import com.example.homeaid.customerboard.dto.request.UpdateBoardRequestDto;
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

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 작성일
    private LocalDateTime createdAt;

    // 수정일
    private LocalDateTime modifiedAt;


    public void updateBoard(String title, String content) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
    }
}
