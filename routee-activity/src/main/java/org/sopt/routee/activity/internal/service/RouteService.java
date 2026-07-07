package org.sopt.routee.activity.internal.service;

import java.util.List;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.route.Route;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.exception.RouteAlreadyExistsException;
import org.sopt.routee.activity.internal.mapper.RouteMapper;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.repository.RouteRepository;
import org.sopt.routee.activity.internal.service.dto.command.CreateRouteCommand;
import org.sopt.routee.activity.internal.service.dto.result.RouteResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteService {

	private final ActivityRepository activityRepository;
	private final RouteRepository routeRepository;

	@Transactional
	public List<RouteResult> createRoutes(Long activityId, List<CreateRouteCommand> commands) {
		if (!activityRepository.existsById(activityId)) {
			throw new ActivityNotFoundException();
		}
		if (routeRepository.existsByActivityId(activityId)) {
			throw new RouteAlreadyExistsException();
		}
		Activity activity = activityRepository.getReferenceById(activityId);

		List<Route> routes = commands.stream()
			.map(command -> RouteMapper.toEntity(command, activity))
			.toList();

		return routeRepository.saveAll(routes).stream()
			.map(RouteMapper::toResult)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<RouteResult> getRoutes(Long activityId) {
		if (!activityRepository.existsById(activityId)) {
			throw new ActivityNotFoundException();
		}

		return routeRepository.findByActivityIdOrderBySequenceAsc(activityId).stream()
			.map(RouteMapper::toResult)
			.toList();
	}

	@Transactional
	public void deleteRoutesByMemberId(Long memberId) {
		routeRepository.deleteByMemberId(memberId);
	}
}
