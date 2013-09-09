/**
 * ﻿Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.web.v1.ctrl;

import static org.n52.io.QueryParameters.createFromQuery;
import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_SERVICES;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;
import static org.n52.web.v1.ctrl.Stopwatch.startStopwatch;

import org.n52.io.IoParameters;
import org.n52.io.v1.data.ServiceOutput;
import org.n52.web.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_SERVICES, produces = {"application/json"})
public class ServicesParameterController extends ParameterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesParameterController.class);

    public ModelAndView getCollection(@RequestParam(required = false) MultiValueMap<String, String> query) {
        IoParameters map = createFromQuery(query);

        if (map.isExpanded()) {
            Stopwatch stopwatch = startStopwatch();
            Object[] result = serviceParameterService.getExpandedParameters(map);
            LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

            // TODO add paging

            return new ModelAndView().addObject(result);
        }
        else {
            Stopwatch stopwatch = startStopwatch();
            Object[] result = serviceParameterService.getCondensedParameters(map);
            LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

            // TODO add paging

            return new ModelAndView().addObject(result);
        }
    }

    public ModelAndView getItem(@PathVariable("item") String serviceId,
                                @RequestParam(required = false) MultiValueMap<String, String> query) {
        IoParameters map = createFromQuery(query);

        // TODO check parameters and throw BAD_REQUEST if invalid

        Stopwatch stopwatch = startStopwatch();
        ServiceOutput service = serviceParameterService.getParameter(serviceId, map);
        LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

        if (service == null) {
            throw new ResourceNotFoundException("Found no service with given id.");
        }

        return new ModelAndView().addObject(service);
    }

}