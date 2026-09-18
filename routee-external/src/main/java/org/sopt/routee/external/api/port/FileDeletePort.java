package org.sopt.routee.external.api.port;

import org.sopt.routee.external.api.command.FileDeleteCommand;
import org.sopt.routee.external.api.command.FileDeleteDirectoryCommand;

public interface FileDeletePort {

	void deleteImage(FileDeleteCommand command);

	void deleteDirectory(FileDeleteDirectoryCommand command);
}
