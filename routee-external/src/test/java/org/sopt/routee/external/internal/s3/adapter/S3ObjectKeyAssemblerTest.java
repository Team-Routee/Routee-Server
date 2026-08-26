package org.sopt.routee.external.internal.s3.adapter;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;

class S3ObjectKeyAssemblerTest {

	@Test
	void assemble_PROFILE은_member_루트_아래에_키를_조립한다() {
		String key = S3ObjectKeyAssembler.assemble(FileUploadDirectory.PROFILE, null, "1", null, "profile.jpg");

		assertThat(key).isEqualTo("member/1/profile/profile.jpg");
	}

	@Test
	void assemble_TIMELINE은_member_루트_아래_activity_경로에_사이즈까지_포함해_키를_조립한다() {
		String key = S3ObjectKeyAssembler.assemble(
			FileUploadDirectory.TIMELINE, FileUploadImageSize.LARGE, "1", "2", "timeline.jpg"
		);

		assertThat(key).isEqualTo("member/1/activity/2/timeline/large/timeline.jpg");
	}

	@Test
	void assemble_RECAP은_member_루트_아래_activity_경로에_키를_조립한다() {
		String key = S3ObjectKeyAssembler.assemble(FileUploadDirectory.RECAP, null, "1", "2", "recap.jpg");

		assertThat(key).isEqualTo("member/1/activity/2/recap/recap.jpg");
	}

	@Test
	void assemble_TIMELINE은_imageSize가_없으면_예외를_던진다() {
		assertThatThrownBy(() ->
			S3ObjectKeyAssembler.assemble(FileUploadDirectory.TIMELINE, null, "1", "2", "timeline.jpg"))
			.isInstanceOf(NullPointerException.class);
	}

	@Test
	void assemble_TIMELINE은_activityId가_없으면_예외를_던진다() {
		assertThatThrownBy(() ->
			S3ObjectKeyAssembler.assemble(FileUploadDirectory.TIMELINE, FileUploadImageSize.LARGE, "1", null, "timeline.jpg"))
			.isInstanceOf(NullPointerException.class);
	}

	@Test
	void assemble_RECAP은_activityId가_없으면_예외를_던진다() {
		assertThatThrownBy(() ->
			S3ObjectKeyAssembler.assemble(FileUploadDirectory.RECAP, null, "1", null, "recap.jpg"))
			.isInstanceOf(NullPointerException.class);
	}

	@Test
	void assembleActivityDirectoryPrefix_member_루트_아래_activity_디렉터리_프리픽스를_조립한다() {
		String prefix = S3ObjectKeyAssembler.assembleActivityDirectoryPrefix("1", "2");

		assertThat(prefix).isEqualTo("member/1/activity/2/");
	}
}
