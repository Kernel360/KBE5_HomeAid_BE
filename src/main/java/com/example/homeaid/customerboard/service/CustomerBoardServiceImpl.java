package com.example.homeaid.customerboard.service;

import com.example.homeaid.customerboard.dto.request.UpdateBoardRequestDto;
import com.example.homeaid.customerboard.dto.response.CreateBoardResponseDto;
import com.example.homeaid.customerboard.entity.CustomerBoard;
import com.example.homeaid.customerboard.repository.CustomerBoardRepository;
import com.example.homeaid.global.exception.CustomException;
import com.example.homeaid.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerBoardServiceImpl implements CustomerBoardService {

  private final CustomerBoardRepository customerBoardRepository;

  @Override
  public CustomerBoard createBoard(CustomerBoard customerBoard) {
    return customerBoardRepository.save(customerBoard);
  }

  /**
   * TODO 게시글 수정 - 서비스 단에서 Dto 처리 문제
   */
  @Override
  public CustomerBoard updateBoard(Long id, CustomerBoard customerBoard) {
    CustomerBoard board = customerBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(
            ErrorCode.RESOURCE_NOT_FOUND
        ));

    board.updateBoard(
        customerBoard.getTitle(),
        customerBoard.getContent()
    );

    return customerBoardRepository.save(board);

  }

  @Override
  public void deleteBoard(Long id) {
    customerBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(
            ErrorCode.RESOURCE_NOT_FOUND
        ));

    customerBoardRepository.deleteById(id);
  }

  @Override
  public CustomerBoard getBoard(Long id) {
    return customerBoardRepository.findById(id)
        .orElseThrow(() -> new CustomException(
            ErrorCode.RESOURCE_NOT_FOUND
        ));
  }

  @Override
  public Page<CreateBoardResponseDto> searchBoard(String keyword, Pageable pageable) {

    Page<CustomerBoard> boardPage;

    if (keyword == null || keyword.trim().isEmpty()) {
      boardPage = customerBoardRepository.findAll(pageable);
    } else {
      boardPage = customerBoardRepository.findByTitleContainingOrContentContaining(
          keyword, keyword, pageable
      );
    }

    return boardPage.map(CreateBoardResponseDto::toDto);
  }

}
