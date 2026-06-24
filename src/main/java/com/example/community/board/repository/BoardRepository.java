package com.example.community.board.repository;

import com.example.community.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 생성한 날짜 기준 전체 조회
    List<Board> findAllByOrderByCreatedAtDesc();

}
