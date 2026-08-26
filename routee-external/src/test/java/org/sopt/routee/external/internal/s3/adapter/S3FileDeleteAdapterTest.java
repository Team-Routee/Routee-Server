package org.sopt.routee.external.internal.s3.adapter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.external.api.command.FileDeleteCommand;
import org.sopt.routee.external.api.command.FileDeleteDirectoryCommand;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.internal.s3.config.S3Properties;
import org.sopt.routee.external.internal.s3.exception.FileDeleteException;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Error;
import software.amazon.awssdk.services.s3.model.S3Object;

@ExtendWith(MockitoExtension.class)
class S3FileDeleteAdapterTest {

	@Mock
	private S3Client s3Client;

	private S3FileDeleteAdapter s3FileDeleteAdapter;

	@BeforeEach
	void setUp() {
		S3Properties properties = new S3Properties("routee-bucket", "ap-northeast-2", null, 10);
		s3FileDeleteAdapter = new S3FileDeleteAdapter(s3Client, properties);
	}

	@Test
	void deleteImage_타임라인_이미지를_삭제하면_원본과_람다가_생성한_리사이징_사이즈_전부를_삭제_요청한다() {
		FileDeleteCommand command = new FileDeleteCommand(FileUploadDirectory.TIMELINE, "9", "1", "timeline-image.jpg");
		when(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
			.thenReturn(DeleteObjectsResponse.builder().build());

		s3FileDeleteAdapter.deleteImage(command);

		ArgumentCaptor<DeleteObjectsRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
		verify(s3Client).deleteObjects(requestCaptor.capture());

		DeleteObjectsRequest request = requestCaptor.getValue();
		assertThat(request.bucket()).isEqualTo("routee-bucket");

		List<String> objectKeys = request.delete().objects().stream()
			.map(ObjectIdentifier::key)
			.toList();

		assertThat(objectKeys).containsExactlyInAnyOrder(
			"member/9/activity/1/timeline/original/timeline-image.jpg",
			"member/9/activity/1/timeline/small/timeline-image.jpg",
			"member/9/activity/1/timeline/medium/timeline-image.jpg",
			"member/9/activity/1/timeline/large/timeline-image.jpg"
		);
	}

	@Test
	void deleteImage_RECAP_이미지를_삭제하면_사이즈_구분_없이_단일_키만_삭제_요청한다() {
		FileDeleteCommand command = new FileDeleteCommand(FileUploadDirectory.RECAP, "9", "1", "recap-image.jpg");
		when(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
			.thenReturn(DeleteObjectsResponse.builder().build());

		s3FileDeleteAdapter.deleteImage(command);

		ArgumentCaptor<DeleteObjectsRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
		verify(s3Client).deleteObjects(requestCaptor.capture());

		List<String> objectKeys = requestCaptor.getValue().delete().objects().stream()
			.map(ObjectIdentifier::key)
			.toList();

		assertThat(objectKeys).containsExactly("member/9/activity/1/recap/recap-image.jpg");
	}

	@Test
	void deleteImage_S3_삭제_요청이_실패하면_FileDeleteException으로_변환한다() {
		FileDeleteCommand command = new FileDeleteCommand(FileUploadDirectory.RECAP, "9", "1", "recap-image.jpg");
		when(s3Client.deleteObjects(any(DeleteObjectsRequest.class))).thenThrow(new RuntimeException("boom"));

		assertThatThrownBy(() -> s3FileDeleteAdapter.deleteImage(command))
			.isInstanceOf(FileDeleteException.class);
	}

	@Test
	void deleteImage_일부_객체_삭제가_실패하면_FileDeleteException을_던진다() {
		FileDeleteCommand command = new FileDeleteCommand(FileUploadDirectory.RECAP, "9", "1", "recap-image.jpg");
		S3Error error = S3Error.builder()
			.key("member/9/activity/1/recap/recap-image.jpg")
			.code("AccessDenied")
			.message("Access Denied")
			.build();
		when(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
			.thenReturn(DeleteObjectsResponse.builder().errors(error).build());

		assertThatThrownBy(() -> s3FileDeleteAdapter.deleteImage(command))
			.isInstanceOf(FileDeleteException.class);
	}

	@Test
	void deleteDirectory_activity_디렉터리_아래_모든_객체를_삭제_요청한다() {
		FileDeleteDirectoryCommand command = new FileDeleteDirectoryCommand("9", "1");
		when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
			.thenReturn(ListObjectsV2Response.builder()
				.contents(
					S3Object.builder().key("member/9/activity/1/timeline/original/a.jpg").build(),
					S3Object.builder().key("member/9/activity/1/timeline/large/a.jpg").build()
				)
				.isTruncated(false)
				.build());
		when(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
			.thenReturn(DeleteObjectsResponse.builder().build());

		s3FileDeleteAdapter.deleteDirectory(command);

		ArgumentCaptor<ListObjectsV2Request> listRequestCaptor = ArgumentCaptor.forClass(ListObjectsV2Request.class);
		verify(s3Client).listObjectsV2(listRequestCaptor.capture());
		assertThat(listRequestCaptor.getValue().prefix()).isEqualTo("member/9/activity/1/");

		ArgumentCaptor<DeleteObjectsRequest> deleteRequestCaptor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
		verify(s3Client).deleteObjects(deleteRequestCaptor.capture());
		List<String> deletedKeys = deleteRequestCaptor.getValue().delete().objects().stream()
			.map(ObjectIdentifier::key)
			.toList();
		assertThat(deletedKeys).containsExactlyInAnyOrder(
			"member/9/activity/1/timeline/original/a.jpg",
			"member/9/activity/1/timeline/large/a.jpg"
		);
	}

	@Test
	void deleteDirectory_대상_객체가_없으면_삭제_요청을_보내지_않는다() {
		FileDeleteDirectoryCommand command = new FileDeleteDirectoryCommand("9", "1");
		when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
			.thenReturn(ListObjectsV2Response.builder().isTruncated(false).build());

		s3FileDeleteAdapter.deleteDirectory(command);

		verify(s3Client, never()).deleteObjects(any(DeleteObjectsRequest.class));
	}

	@Test
	void deleteDirectory_결과가_여러_페이지면_모두_순회하며_삭제한다() {
		FileDeleteDirectoryCommand command = new FileDeleteDirectoryCommand("9", "1");
		when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
			.thenReturn(ListObjectsV2Response.builder()
				.contents(S3Object.builder().key("member/9/activity/1/timeline/original/a.jpg").build())
				.isTruncated(true)
				.nextContinuationToken("token-1")
				.build())
			.thenReturn(ListObjectsV2Response.builder()
				.contents(S3Object.builder().key("member/9/activity/1/timeline/original/b.jpg").build())
				.isTruncated(false)
				.build());
		when(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
			.thenReturn(DeleteObjectsResponse.builder().build());

		s3FileDeleteAdapter.deleteDirectory(command);

		verify(s3Client, times(2)).listObjectsV2(any(ListObjectsV2Request.class));
		verify(s3Client, times(2)).deleteObjects(any(DeleteObjectsRequest.class));
	}

	@Test
	void deleteDirectory_목록_조회가_실패하면_FileDeleteException으로_변환한다() {
		FileDeleteDirectoryCommand command = new FileDeleteDirectoryCommand("9", "1");
		when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenThrow(new RuntimeException("boom"));

		assertThatThrownBy(() -> s3FileDeleteAdapter.deleteDirectory(command))
			.isInstanceOf(FileDeleteException.class);
	}

	@Test
	void deleteDirectory_삭제_요청이_실패하면_FileDeleteException으로_변환한다() {
		FileDeleteDirectoryCommand command = new FileDeleteDirectoryCommand("9", "1");
		when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
			.thenReturn(ListObjectsV2Response.builder()
				.contents(S3Object.builder().key("member/9/activity/1/timeline/original/a.jpg").build())
				.isTruncated(false)
				.build());
		when(s3Client.deleteObjects(any(DeleteObjectsRequest.class))).thenThrow(new RuntimeException("boom"));

		assertThatThrownBy(() -> s3FileDeleteAdapter.deleteDirectory(command))
			.isInstanceOf(FileDeleteException.class);
	}
}
