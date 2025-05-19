package com.example.homeaid.customerboard.service;

import com.example.homeaid.customerboard.entity.CustomerBoard;
import com.example.homeaid.customerboard.repository.CustomerBoardRepository;
import com.example.homeaid.global.exception.CustomException;
import com.example.homeaid.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerBoardServiceImpl implements CustomerBoardService {

    private final CustomerBoardRepository boardRepository;

    @Override
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    @Override
    public CustomerBoard getBoard(Long id) {
        CustomerBoard customerBoard = boardRepository.findById(id)
            .orElseThrow(() -> new CustomException(
                ErrorCode.RESOURCE_NOT_FOUND));

        return customerBoard;
    }

    @Override
    public CustomerBoard updateBoard(Long id) {
        CustomerBoard customerBoard = boardRepository.findById(id)
            .orElseThrow(() -> new CustomException(
                ErrorCode.RESOURCE_NOT_FOUND));
        return customerBoard;
    }

    @Override
    public CustomerBoard createBoard(CustomerBoard customerBoard) {
        return boardRepository.save(customerBoard);
    }
}
