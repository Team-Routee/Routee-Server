package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.entity.activity.ActivityType;
import org.sopt.routee.activity.internal.entity.timeline.Timeline;
import org.sopt.routee.activity.internal.entity.timeline.TimelineStatus;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.repository.RouteRepository;
import org.sopt.routee.activity.internal.repository.TimelineRepository;
import org.sopt.routee.activity.internal.service.dto.command.CompleteActivityCommand;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;
import org.sopt.routee.activity.internal.service.dto.result.ActivityCreationTransactionResult;
import org.sopt.routee.activity.internal.service.dto.result.CreateActivityResult;
import org.sopt.routee.activity.internal.service.dto.vo.TrackPoint;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileNameValidator;
import org.sopt.routee.external.api.command.FileDeleteDirectoryCommand;
import org.sopt.routee.external.api.port.FileDeletePort;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.sopt.routee.external.api.result.FileImageAccessUrlResult;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

	private static final Long MEMBER_ID = 1L;
	private static final EnumSet<ActivityStatus> ACTIVE_STATUSES = EnumSet.of(
		ActivityStatus.ACTIVITY_IN_PROGRESS,
		ActivityStatus.ACTIVITY_PAUSED
	);

	@Mock
	private ActivityRepository activityRepository;

	@Mock
	private TimelineRepository timelineRepository;

	@Mock
	private ActivityDailySummaryService activityDailySummaryService;

	@Mock
	private ActivityImageFileNameValidator activityImageFileNameValidator;

	@Mock
	private FileUploadPresignPort fileUploadPresignPort;

	@Mock
	private FileImageAccessUrlPort fileImageAccessUrlPort;

	@Mock
	private FileDeletePort fileDeletePort;

	@Mock
	private RouteRepository routeRepository;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Mock
	private TransactionTemplate transactionTemplate;

	private ActivityService activityService;

	@BeforeEach
	void setUp() {
		activityService = new ActivityService(
			activityRepository,
			timelineRepository,
			activityDailySummaryService,
			activityImageFileNameValidator,
			fileUploadPresignPort,
			fileImageAccessUrlPort,
			fileDeletePort,
			routeRepository,
			applicationEventPublisher,
			transactionTemplate
		);
	}

	private void stubTransactionTemplateToRunCallback() {
		when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
			TransactionCallback<ActivityCreationTransactionResult> callback = invocation.getArgument(0);
			return callback.doInTransaction(mock(TransactionStatus.class));
		});
	}

	private CreateActivityCommand createCommand(ActivityType activityType) {
		return new CreateActivityCommand(
			MEMBER_ID,
			activityType,
			LocalDateTime.of(2026, 7, 7, 15, 30),
			ZoneId.of("Pacific/Kiritimati")
		);
	}

	private Activity savedActivity(Long activityId, ActivityType activityType) {
		return Activity.builder()
			.id(activityId)
			.title("2026.07.07 기록")
			.activityType(activityType)
			.activityStatus(ActivityStatus.ACTIVITY_IN_PROGRESS)
			.memberId(MEMBER_ID)
			.build();
	}

	private CompleteActivityCommand completeCommand(Long activityId) {
		return new CompleteActivityCommand(
			activityId,
			MEMBER_ID,
			ZoneId.of("Asia/Seoul"),
			"북한산 기록",
			5400,
			3600,
			836,
			"https://example.com/map.png",
			List.of(
				new TrackPoint(37.566, 126.978, 20, 0),
				new TrackPoint(37.567, 126.979, 25, 10)
			),
			LocalDateTime.of(2026, 7, 7, 16, 30)
		);
	}

	@Test
	void create_활성_활동이_없으면_기존_데이터를_삭제하지_않고_새_활동을_생성한다() {
		Activity savedActivity = savedActivity(10L, ActivityType.RUNNING);
		when(activityRepository.findIdsByMemberIdAndActivityStatusIn(MEMBER_ID, ACTIVE_STATUSES))
			.thenReturn(List.of());
		when(activityRepository.save(any(Activity.class))).thenReturn(savedActivity);
		stubTransactionTemplateToRunCallback();

		CreateActivityCommand command = createCommand(ActivityType.RUNNING);
		Instant expectedStartedAt = command.startedAt().atZone(command.timeZone()).toInstant();
		CreateActivityResult result = activityService.create(command);

		assertThat(result.activityId()).isEqualTo(10L);
		assertThat(result.title()).isEqualTo("2026.07.07 기록");

		ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
		verify(activityRepository).save(activityCaptor.capture());
		Activity activity = activityCaptor.getValue();
		assertThat(activity.getTitle()).isEqualTo("2026.07.07 기록");
		assertThat(activity.getStartedAt()).isEqualTo(expectedStartedAt);
		assertThat(activity.getActivityType()).isEqualTo(ActivityType.RUNNING);
		assertThat(activity.getActivityStatus()).isEqualTo(ActivityStatus.ACTIVITY_IN_PROGRESS);
		assertThat(activity.getMemberId()).isEqualTo(MEMBER_ID);

		verify(timelineRepository, never()).deleteByActivityIdIn(anyList());
		verify(activityRepository, never()).deleteByIdIn(anyList());
		verifyNoInteractions(fileDeletePort);
	}

	@Test
	void create_활성_활동이_있으면_기존_활동을_폐기하고_새_활동을_생성한_뒤_이미지_디렉터리_삭제를_요청한다() throws InterruptedException {
		List<Long> activeActivityIds = List.of(10L, 11L);
		when(activityRepository.findIdsByMemberIdAndActivityStatusIn(MEMBER_ID, ACTIVE_STATUSES))
			.thenReturn(activeActivityIds);
		when(activityRepository.save(any(Activity.class))).thenReturn(savedActivity(20L, ActivityType.RUNNING));
		stubTransactionTemplateToRunCallback();

		CountDownLatch latch = new CountDownLatch(activeActivityIds.size());
		doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(fileDeletePort).deleteDirectory(any());

		CreateActivityResult result = activityService.create(createCommand(ActivityType.RUNNING));

		assertThat(result.activityId()).isEqualTo(20L);
		InOrder inOrder = inOrder(activityRepository, timelineRepository);
		inOrder.verify(activityRepository).findIdsByMemberIdAndActivityStatusIn(MEMBER_ID, ACTIVE_STATUSES);
		inOrder.verify(timelineRepository).deleteByActivityIdIn(activeActivityIds);
		inOrder.verify(activityRepository).deleteByIdIn(activeActivityIds);
		inOrder.verify(activityRepository).save(any(Activity.class));
		assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();

		ArgumentCaptor<FileDeleteDirectoryCommand> commandCaptor = ArgumentCaptor.forClass(FileDeleteDirectoryCommand.class);
		verify(fileDeletePort, times(2)).deleteDirectory(commandCaptor.capture());
		assertThat(commandCaptor.getAllValues()).containsExactly(
			new FileDeleteDirectoryCommand(MEMBER_ID.toString(), "10"),
			new FileDeleteDirectoryCommand(MEMBER_ID.toString(), "11")
		);
	}

	@Test
	void create_일부_S3_삭제가_실패해도_나머지_삭제를_계속하고_예외를_전파하지_않는다() throws InterruptedException {
		List<Long> activeActivityIds = List.of(10L, 11L);
		when(activityRepository.findIdsByMemberIdAndActivityStatusIn(MEMBER_ID, ACTIVE_STATUSES))
			.thenReturn(activeActivityIds);
		when(activityRepository.save(any(Activity.class))).thenReturn(savedActivity(20L, ActivityType.RUNNING));
		stubTransactionTemplateToRunCallback();

		CountDownLatch latch = new CountDownLatch(activeActivityIds.size());
		AtomicInteger invocationCount = new AtomicInteger();
		doAnswer(invocation -> {
			latch.countDown();
			if (invocationCount.getAndIncrement() == 0) {
				throw new ActivityNotFoundException();
			}
			return null;
		}).when(fileDeletePort).deleteDirectory(any());

		CreateActivityResult result = activityService.create(createCommand(ActivityType.RUNNING));

		assertThat(result.activityId()).isEqualTo(20L);
		assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
		verify(fileDeletePort, times(2)).deleteDirectory(any());
	}

	@Test
	void create_새_활동_저장에_실패하면_S3_삭제를_요청하지_않는다() {
		List<Long> activeActivityIds = List.of(10L);
		when(activityRepository.findIdsByMemberIdAndActivityStatusIn(MEMBER_ID, ACTIVE_STATUSES))
			.thenReturn(activeActivityIds);
		when(activityRepository.save(any(Activity.class))).thenThrow(new ActivityNotFoundException());
		stubTransactionTemplateToRunCallback();

		assertThatThrownBy(() -> activityService.create(createCommand(ActivityType.RUNNING)))
			.isInstanceOf(ActivityNotFoundException.class);

		verify(fileDeletePort, never()).deleteDirectory(any());
	}

	@Test
	void complete_성공적으로_생성된_타임라인이_있으면_트랙포인트가_가장_작은_이미지를_커버로_설정한다() {
		Long activityId = 1L;
		Activity activity = Activity.builder().id(activityId).memberId(MEMBER_ID).startedAt(Instant.now()).build();
		Timeline coverTimeline = Timeline.builder().id(100L).timelineImageObjectKey("smallest.jpg").build();

		when(activityRepository.findByIdAndMemberId(activityId, MEMBER_ID)).thenReturn(Optional.of(activity));
		when(timelineRepository.findFirstByActivityIdAndTimelineStatusOrderByTrackPointIndexAsc(
			activityId, TimelineStatus.SUCCESSFUL_CREATED))
			.thenReturn(Optional.of(coverTimeline));
		when(fileImageAccessUrlPort.generateImageUrl(any()))
			.thenReturn(new FileImageAccessUrlResult("https://image-url"));

		completeWithSynchronizationActive(completeCommand(activityId));

		assertThat(activity.getCoverImageObjectKey()).isEqualTo("smallest.jpg");
	}

	@Test
	void complete_성공적으로_생성된_타임라인이_없으면_커버이미지를_null로_설정한다() {
		Long activityId = 1L;
		Activity activity = Activity.builder().id(activityId).memberId(MEMBER_ID).startedAt(Instant.now()).build();

		when(activityRepository.findByIdAndMemberId(activityId, MEMBER_ID)).thenReturn(Optional.of(activity));
		when(timelineRepository.findFirstByActivityIdAndTimelineStatusOrderByTrackPointIndexAsc(
			activityId, TimelineStatus.SUCCESSFUL_CREATED))
			.thenReturn(Optional.empty());

		completeWithSynchronizationActive(completeCommand(activityId));

		assertThat(activity.getCoverImageObjectKey()).isNull();
		verifyNoInteractions(fileImageAccessUrlPort);
	}

	private void completeWithSynchronizationActive(CompleteActivityCommand command) {
		TransactionSynchronizationManager.initSynchronization();
		try {
			activityService.complete(command);
		} finally {
			TransactionSynchronizationManager.clearSynchronization();
		}
	}
}
