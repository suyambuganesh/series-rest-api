/**
 * Copyright (C) 2013-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.series.api.v1.db.srv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.n52.io.request.IoParameters;
import org.n52.io.response.OutputCollection;
import org.n52.io.response.ParameterOutput;
import org.n52.io.response.v1.ProcedureOutput;
import org.n52.sensorweb.spi.ParameterService;
import org.n52.series.api.v1.db.da.DbQueryV1;
import org.n52.series.api.v1.db.da.ProcedureRepository;
import org.n52.series.db.da.DataAccessException;
import org.n52.series.db.da.DbQuery;
import org.n52.series.db.srv.ServiceInfoAccess;
import org.n52.web.exception.InternalServerException;

public class ProceduresAccessService extends ServiceInfoAccess implements ParameterService<ProcedureOutput> {

    private OutputCollection<ProcedureOutput> createOutputCollection(List<ProcedureOutput> results) {
        return new OutputCollection<ProcedureOutput>(results) {
                @Override
                protected Comparator<ProcedureOutput> getComparator() {
                    return ParameterOutput.defaultComparator();
                }
            };
    }
    
    @Override
    public OutputCollection<ProcedureOutput> getExpandedParameters(IoParameters query) {
        try {
            DbQuery dbQuery = DbQueryV1.createFrom(query);
            ProcedureRepository repository = createProcedureRepository();
            List<ProcedureOutput> results = repository.getAllExpanded(dbQuery);
            return createOutputCollection(results);
        }
        catch (DataAccessException e) {
            throw new InternalServerException("Could not get procedure data.", e);
        }
    }

    @Override
    public OutputCollection<ProcedureOutput> getCondensedParameters(IoParameters query) {
        try {
            DbQuery dbQuery = DbQueryV1.createFrom(query);
            ProcedureRepository repository = createProcedureRepository();
            List<ProcedureOutput> results = repository.getAllCondensed(dbQuery);
            return createOutputCollection(results);
        }
        catch (DataAccessException e) {
            throw new InternalServerException("Could not get procedure data.",e );
        }
    }

    @Override
    public OutputCollection<ProcedureOutput> getParameters(String[] procedureIds) {
        return getParameters(procedureIds, IoParameters.createDefaults());
    }

    @Override
    public OutputCollection<ProcedureOutput> getParameters(String[] procedureIds, IoParameters query) {
        try {
            DbQuery dbQuery = DbQueryV1.createFrom(query);
            ProcedureRepository repository = createProcedureRepository();
            List<ProcedureOutput> results = new ArrayList<>();
            for (String procedureId : procedureIds) {
                results.add(repository.getInstance(procedureId, dbQuery));
            }
            return createOutputCollection(results);
        } catch (DataAccessException e) {
            throw new InternalServerException("Could not get procedure data.", e);
        }
    }

    @Override
    public ProcedureOutput getParameter(String procedureId) {
        return getParameter(procedureId, IoParameters.createDefaults());
    }

    @Override
    public ProcedureOutput getParameter(String procedureId, IoParameters query) {
        try {
            DbQuery dbQuery = DbQueryV1.createFrom(query);
            ProcedureRepository repository = createProcedureRepository();
            return repository.getInstance(procedureId, dbQuery);
        } catch (DataAccessException e) {
            throw new InternalServerException("Could not get procedure data", e);
        }
    }

    private ProcedureRepository createProcedureRepository() {
        return new ProcedureRepository(getServiceInfo());
    }

}
