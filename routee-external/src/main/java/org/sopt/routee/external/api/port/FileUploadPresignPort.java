package org.sopt.routee.external.api.port;

public interface FileUploadPresignPort {

	String generatePutPresignedUrl(String objectKey);
}
