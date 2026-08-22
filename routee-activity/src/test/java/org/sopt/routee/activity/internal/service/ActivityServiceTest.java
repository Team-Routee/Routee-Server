package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
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
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.repository.RouteRepository;
import org.sopt.routee.activity.internal.repository.TimelineRepository;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;
import org.sopt.routee.activity.internal.service.dto.result.ActivityCreationTransactionResult;
import org.sopt.routee.activity.internal.service.dto.result.CreateActivityResult;
import org.sopt.routee.activity.internal.service.dto.vo.TimelineImageDeleteTarget;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileNameValidator;
import org.sopt.routee.external.api.command.FileDeleteCommand;
import org.sopt.routee.external.api.port.FileDeletePort;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
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

		verify(timelineRepository, never()).findImageDeleteTargetsByActivityIdIn(anyList());
		verify(timelineRepository, never()).deleteByActivityIdIn(anyList());
		verify(activityRepository, never()).deleteByIdIn(anyList());
		verifyNoInteractions(fileDeletePort);
	}

	@Test
	void create_활성_활동에_타임라인이_없어도_기존_활동을_폐기하고_새_활동을_생성한다() {
		List<Long> activeActivityIds = List.of(10L, 11L);
		when(activityRepository.findIdsByMemberIdAndActivityStatusIn(MEMBER_ID, ACTIVE_STATUSES))
			.thenReturn(activeActivityIds);
		when(timelineRepository.findImageDeleteTargetsByActivityIdIn(activeActivityIds)).thenReturn(List.of());
		when(activityRepository.save(any(Activity.class))).thenReturn(savedActivity(20L, ActivityType.HIKING));
		stubTransactionTemplateToRunCallback();

		CreateActivityResult result = activityService.create(createCommand(ActivityType.HIKING));

		assertThat(result.activityId()).isEqualTo(20L);
		InOrder inOrder = inOrder(activityRepository, timelineRepository);
		inOrder.verify(activityRepository).findIdsByMemberIdAndActivityStatusIn(MEMBER_ID, ACTIVE_STATUSES);
		inOrder.verify(timelineRepository).findImageDeleteTargetsByActivityIdIn(activeActivityIds);
		inOrder.verify(timelineRepository).deleteByActivityIdIn(activeActivityIds);
		inOrder.verify(activityRepository).deleteByIdIn(activeActivityIds);
		inOrder.verify(activityRepository).save(any(Activity.class));
		verifyNoInteractions(fileDeletePort);
	}

	@Test
	void create_타임라인_이미지가_있으면_DB_삭제_후_S3_삭제를_요청한다() throws InterruptedException {
		List<Long> activeActivityIds = List.of(10L, 11L);
		List<TimelineImageDeleteTarget> imageDeleteTargets = List.of(
			new TimelineImageDeleteTarget(10L, "10/first.jpg"),
			new TimelineImageDeleteTarget(11L, "11/second.jpg")
		);
		when(activityRepository.findIdsByMemberIdAndActivityStatusIn(MEMBER_ID, ACTIVE_STATUSES))
			.thenReturn(activeActivityIds);
		when(timelineRepository.findImageDeleteTargetsByActivityIdIn(activeActivityIds)).thenReturn(imageDeleteTargets);
		when(activityRepository.save(any(Activity.class))).thenReturn(savedActivity(20L, ActivityType.RUNNING));
		stubTransactionTemplateToRunCallback();

		CountDownLatch latch = new CountDownLatch(imageDeleteTargets.size());
		doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(fileDeletePort).deleteImage(any());

		activityService.create(createCommand(ActivityType.RUNNING));

		InOrder inOrder = inOrder(activityRepository, timelineRepository);
		inOrder.verify(activityRepository).findIdsByMemberIdAndActivityStatusIn(MEMBER_ID, ACTIVE_STATUSES);
		inOrder.verify(timelineRepository).findImageDeleteTargetsByActivityIdIn(activeActivityIds);
		inOrder.verify(timelineRepository).deleteByActivityIdIn(activeActivityIds);
		inOrder.verify(activityRepository).deleteByIdIn(activeActivityIds);
		inOrder.verify(activityRepository).save(any(Activity.class));
		assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();

		ArgumentCaptor<FileDeleteCommand> commandCaptor = ArgumentCaptor.forClass(FileDeleteCommand.class);
		verify(fileDeletePort, times(2)).deleteImage(commandCaptor.capture());
		assertThat(commandCaptor.getAllValues()).containsExactly(
			new FileDeleteCommand(FileUploadDirectory.TIMELINE, "10", "10/first.jpg"),
			new FileDeleteCommand(FileUploadDirectory.TIMELINE, "11", "11/second.jpg")
		);
	}

	@Test
	void create_일부_S3_삭제가_실패해도_나머지_삭제를_계속하고_예외를_전파하지_않는다() throws InterruptedException {
		List<Long> activeActivityIds = List.of(10L, 11L);
		List<TimelineImageDeleteTarget> imageDeleteTargets = List.of(
			new TimelineImageDeleteTarget(10L, "10/first.jpg"),
			new TimelineImageDeleteTarget(11L, "11/second.jpg")
		);
		when(activityRepository.findIdsByMemberIdAndActivityStatusIn(MEMBER_ID, ACTIVE_STATUSES))
			.thenReturn(activeActivityIds);
		when(timelineRepository.findImageDeleteTargetsByActivityIdIn(activeActivityIds)).thenReturn(imageDeleteTargets);
		when(activityRepository.save(any(Activity.class))).thenReturn(savedActivity(20L, ActivityType.RUNNING));
		stubTransactionTemplateToRunCallback();

		CountDownLatch latch = new CountDownLatch(imageDeleteTargets.size());
		AtomicInteger invocationCount = new AtomicInteger();
		doAnswer(invocation -> {
			latch.countDown();
			if (invocationCount.getAndIncrement() == 0) {
				throw new ActivityNotFoundException();
			}
			return null;
		}).when(fileDeletePort).deleteImage(any());

		CreateActivityResult result = activityService.create(createCommand(ActivityType.RUNNING));

		assertThat(result.activityId()).isEqualTo(20L);
		assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
		verify(fileDeletePort, times(2)).deleteImage(any());
	}

	@Test
	void create_새_활동_저장에_실패하면_S3_삭제를_요청하지_않는다() {
		List<Long> activeActivityIds = List.of(10L);
		when(activityRepository.findIdsByMemberIdAndActivityStatusIn(MEMBER_ID, ACTIVE_STATUSES))
			.thenReturn(activeActivityIds);
		when(timelineRepository.findImageDeleteTargetsByActivityIdIn(activeActivityIds))
			.thenReturn(List.of(new TimelineImageDeleteTarget(10L, "10/first.jpg")));
		when(activityRepository.save(any(Activity.class))).thenThrow(new ActivityNotFoundException());
		stubTransactionTemplateToRunCallback();

		assertThatThrownBy(() -> activityService.create(createCommand(ActivityType.RUNNING)))
			.isInstanceOf(ActivityNotFoundException.class);

		verify(fileDeletePort, never()).deleteImage(any());
	}
}
