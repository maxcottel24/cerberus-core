/*
 * Cerberus Copyright (C) 2013 - 2017 cerberustesting
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.core.api.dto.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.cerberus.core.api.dto.views.View;

@ToString
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(value = "Application")
public class ApplicationDTOV001 {

    @NotEmpty
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    @ApiModelProperty(position = 0)
    private String application;

    @ApiModelProperty(position = 1)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String description;

    @ApiModelProperty(position = 2)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private Integer sort;

    @ApiModelProperty(position = 3)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String type;

    @ApiModelProperty(position = 4)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String system;

    @ApiModelProperty(position = 5)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String subsystem;

    @ApiModelProperty(position = 6)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String svnurl;

    @ApiModelProperty(position = 7)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String bugTrackerUrl;

    @ApiModelProperty(position = 8)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String bugTrackerNewUrl;

    @ApiModelProperty(position = 11)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String bugTrackerConnector;

    @ApiModelProperty(position = 11)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String bugTrackerParam1;

    @ApiModelProperty(position = 11)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String bugTrackerParam2;

    @ApiModelProperty(position = 11)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String bugTrackerParam3;

    @ApiModelProperty(position = 9)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private Integer poolSize;

    @ApiModelProperty(position = 10)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String deploytype;

    @ApiModelProperty(position = 11)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String mavengroupid;

    @ApiModelProperty(position = 12)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private List<CountryEnvironmentParametersDTOV001> environments;

    @ApiModelProperty(position = 17)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String usrCreated;

    @ApiModelProperty(position = 19)
    @JsonView(value = {View.Public.GET.class})
    private String dateCreated;

    @ApiModelProperty(position = 20)
    @JsonView(value = {View.Public.GET.class, View.Public.PATCH.class, View.Public.PUT.class, View.Public.POST.class})
    private String usrModif;

    @ApiModelProperty(position = 21)
    @JsonView(value = {View.Public.GET.class})
    private String dateModif;
}
