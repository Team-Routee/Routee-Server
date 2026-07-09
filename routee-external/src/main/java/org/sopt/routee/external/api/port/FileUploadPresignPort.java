package org.sopt.routee.external.api.port;

import org.sopt.routee.external.api.command.FileUploadPresignCommand;
import org.sopt.routee.external.api.result.FileUploadPresignResult;

public interface FileUploadPresignPort {

	FileUploadPresignResult generatePutPresignedUrl(FileUploadPresignCommand command);
}
