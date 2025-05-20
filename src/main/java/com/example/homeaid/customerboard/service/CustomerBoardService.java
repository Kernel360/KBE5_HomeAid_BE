package com.example.homeaid.customerboard.service;


import com.example.homeaid.customerboard.dto.request.UpdateBoardRequestDto;
import com.example.homeaid.customerboard.dto.response.UpdateBoardResponseDto;
import com.example.homeaid.customerboard.entity.CustomerBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerBoardService {

    CustomerBoard createBoard(CustomerBoard customerBoard);

    CustomerBoard updateBoard(Long id, CustomerBoard customerBoard);

    CustomerBoard getBoard(Long id);

    Page searchBoard(String keyword, Pageable pageable);

    void deleteBoard(Long id);

}
