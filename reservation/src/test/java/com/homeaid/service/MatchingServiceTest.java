package com.homeaid.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.homeaid.domain.Manager;
import com.homeaid.domain.Matching;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.GenderType;
import com.homeaid.domain.enumerate.Weekday;
import com.homeaid.dto.request.MatchingManagerResponseDto.ManagerAction;
import com.homeaid.exception.CustomException;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.repository.MatchingRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.serviceoption.domain.ServiceOption;
import com.homeaid.serviceoption.domain.ServiceSubOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;


@TestPropertySource("classpath:application-test.yml")
@ContextConfiguration(classes = TestConfiguration.class)
@DataJpaTest
class MatchingServiceTest {

  @InjectMocks
  private MatchingServiceImpl matchingService;

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private ManagerRepository managerRepository;

  @Mock
  private MatchingRepository matchingRepository;


  @Test
  @DisplayName("매니저와 예약으로 매칭 생성")
  void createMatching() {
    // given
    Long managerId = 1L;
    Long reservationId = 10L;
    Long expectedMatchingId = 100L;

    Manager manager = new Manager(
        "manager@example.com",
        "password",
        "김매니저",
        "010-1111-2222",
        LocalDate.of(1990, 1, 1),
        GenderType.MALE,
        "경력사항",
        "소개",
        "image.jpg"
    );

    Reservation reservation = Reservation.builder()
        .customerId(2L)
        .requestedDate(LocalDate.of(2025, 6, 3))
        .requestedTime(LocalTime.of(10, 0))
        .build();

    Matching matching = Matching.builder().build();

    given(managerRepository.findById(managerId)).willReturn(Optional.of(manager));
    given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
    given(matchingRepository.save(any(Matching.class)))
        .willAnswer(invocation -> {
          Matching m = invocation.getArgument(0);
          ReflectionTestUtils.setField(m, "id", expectedMatchingId); // 매칭 ID 설정
          return m;
        });

    // when
    Long result = matchingService.createMatching(managerId, reservationId, matching);

    // then
    assertThat(result).isEqualTo(expectedMatchingId);
    assertThat(matching.getReservation()).isEqualTo(reservation);
    assertThat(matching.getManager().getId()).isEqualTo(manager.getId());
    assertThat(matching.getMatchingRound()).isNotNull();
  }

  @Test
  @DisplayName("예약 정보 기반 매니저 추천")
  void recommendManagers() {
    // given
    Long reservationId = 1L;

    ServiceOption serviceOption = ServiceOption.builder()
        .name("가사 서비스")
        .build();

    ServiceSubOption subOption = ServiceSubOption.builder()
        .name("청소")
        .description("기본 청소 서비스")
        .durationMinutes(90)
        .basePrice(30000)
        .option(serviceOption)
        .build();


    Reservation reservation = Reservation.builder()
        .customerId(1L)
        .requestedDate(LocalDate.of(2025, 6, 3))  // 화요일
        .requestedTime(LocalTime.of(14, 0))
        .build();

    reservation.addItem(subOption);

    List<Manager> managerList = List.of(
        new Manager(
            "manager1@example.com",
            "encoded-password1",
            "김매니저1",
            "010-1111-2222",
            LocalDate.of(1990, 1, 1),
            GenderType.MALE,
            "career",
            "experience",
            "profileImage"
        ),
        new Manager(
            "manager2@example.com",
            "encoded-password2",
            "이매니저2",
            "010-3333-4444",
            LocalDate.of(1988, 5, 12),
            GenderType.FEMALE,
            "career",
            "experience",
            "profileImage"
        ),
        new Manager(
            "manager3@example.com",
            "encoded-password3",
            "박매니저3",
            "010-5555-6666",
            LocalDate.of(1995, 10, 20),
            GenderType.MALE,
            "career",
            "experience",
            "profileImage"
        )
    );


    given(reservationRepository.findById(reservationId))
        .willReturn(Optional.of(reservation));
    given(managerRepository.findMatchingManagers(eq(Weekday.TUESDAY), any(), any(), eq("청소")))
        .willReturn(managerList);

    // when
    List<Manager> result = matchingService.recommendManagers(reservationId);

    // then
    assertThat(result).hasSize(3);
  }

  @Test
  @DisplayName("매칭이 없으면 예외 발생")
  void respondToMatchingAsManager() {
    // given
    Long matchingId = 1L;
    given(matchingRepository.findById(matchingId)).willReturn(Optional.empty());
    Long userId = 1L;

    // when & then
    assertThatThrownBy(() ->
        matchingService.respondToMatchingAsManager(userId, matchingId, ManagerAction.ACCEPT, null)
    ).isInstanceOf(CustomException.class)
        .hasMessageContaining("매칭 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("수락시 acceptByManager 호출")
  void respondToMatchingAsManager_수락시_acceptByManager_호출() {
    // given
    Long matchingId = 1L;
    Long userId = 1L;

    Matching matching = mock(Matching.class);
    Manager manager = mock(Manager.class);

    given(matchingRepository.findById(matchingId)).willReturn(Optional.of(matching));
    given(matching.getManager()).willReturn(manager);
    given(manager.getId()).willReturn(userId);

    // when
    matchingService.respondToMatchingAsManager(userId, matchingId, ManagerAction.ACCEPT, null);

    // then
    verify(matching).acceptByManager();
    verify(matching, never()).rejectByManager(anyString());
  }

  @Test
  @DisplayName("거절시 memo가 없으면 예외 발생")
  void respondToMatchingAsManager_거절시_memo가_없으면_예외_발생() {
    // given
    Long matchingId = 1L;
    Long userId = 1L;

    Matching matching = mock(Matching.class);
    Manager manager = mock(Manager.class);

    given(matchingRepository.findById(matchingId)).willReturn(Optional.of(matching));
    given(matching.getManager()).willReturn(manager);
    given(manager.getId()).willReturn(userId);

    // when & then
    assertThatThrownBy(() ->
        matchingService.respondToMatchingAsManager(userId, matchingId, ManagerAction.REJECT, null)
    )
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("거절 시에는 메모를 작성해야 합니다.");
  }

  @Test
  @DisplayName("거절시 memo가 있으면 rejectByManager 호출")
  void respondToMatchingAsManager_거절시_memo가_있으면_rejectByManager_호출() {
    // given
    Long matchingId = 1L;
    String memo = "사정이 있어 수락이 어렵습니다.";
    Long userId = 1L;

    Matching matching = mock(Matching.class);
    Manager manager = mock(Manager.class);
    Reservation reservation = mock(Reservation.class); // ✅ Reservation mock 추가

    given(matchingRepository.findById(matchingId)).willReturn(Optional.of(matching));
    given(matching.getManager()).willReturn(manager);
    given(manager.getId()).willReturn(userId);
    given(matching.getReservation()).willReturn(reservation); // ✅ 여기 설정 필수

    // when
    matchingService.respondToMatchingAsManager(userId, matchingId, ManagerAction.REJECT, memo);

    // then
    verify(matching).rejectByManager(memo);
    verify(matching, never()).acceptByManager();
    verify(reservation).failedMatching(); // ✅ 호출 여부도 검증 가능
  }
}
