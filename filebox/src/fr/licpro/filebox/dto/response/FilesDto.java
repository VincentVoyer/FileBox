/*
 * Software property of MILLAU Julien. Copyright Copyright (c) 2014.
 */
package fr.licpro.filebox.dto.response;

import fr.licpro.filebox.dto.commons.FileDto;
import fr.licpro.filebox.dto.error.HttpExceptionDto;
import java.util.Date;
import java.util.List;

/**
 * File dto
 *
 * @author julien
 */
public class FilesDto extends HttpExceptionDto {

    /**
     * List of file
     */
    private List<FileDto> listFile;

    /**
     * Last update
     */
    private Date lastUpdate;

    public FilesDto(List<FileDto> listFile) {
        this.listFile = listFile;
    }

    public FilesDto() {
    }

    public List<FileDto> getListFile() {
        return listFile;
    }

    public void setListFile(List<FileDto> listFile) {
        this.listFile = listFile;
    }

    public Date getLastUpdate() {
		return lastUpdate;
	}
    
    public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}
