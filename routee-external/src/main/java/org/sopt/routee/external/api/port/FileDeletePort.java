package org.sopt.routee.external.api.port;

import org.sopt.routee.external.api.command.FileDeleteCommand;

public interface FileDeletePort {

	void deleteImage(FileDeleteCommand command);
}
