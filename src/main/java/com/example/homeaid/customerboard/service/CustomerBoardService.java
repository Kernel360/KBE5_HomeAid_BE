package com.example.homeaid.customerboard.service;


import com.example.homeaid.customerboard.entity.CustomerBoard;

public interface CustomerBoardService {

    CustomerBoard getBoard(Long id);

    void deleteBoard(Long id);

    CustomerBoard updateBoard(Long id);

    CustomerBoard createBoard(CustomerBoard customerBoard);


}
