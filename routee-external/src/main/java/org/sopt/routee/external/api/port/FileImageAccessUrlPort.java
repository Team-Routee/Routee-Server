package org.sopt.routee.external.api.port;

import org.sopt.routee.external.api.command.FileImageAccessUrlCommand;
import org.sopt.routee.external.api.result.FileImageAccessUrlResult;

public interface FileImageAccessUrlPort {

	FileImageAccessUrlResult generateImageUrl(FileImageAccessUrlCommand command);
}
