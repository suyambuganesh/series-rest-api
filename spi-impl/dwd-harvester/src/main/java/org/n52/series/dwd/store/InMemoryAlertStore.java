/*
 * Copyright (C) 2013-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public License
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package org.n52.series.dwd.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.n52.series.dwd.beans.AlertMessage;
import org.n52.series.dwd.beans.WarnCell;
import org.n52.series.dwd.rest.Alert;
import org.n52.series.dwd.rest.AlertCollection;
import org.n52.series.dwd.rest.WarnungAlert;

public class InMemoryAlertStore implements AlertStore {

    private AlertCollection currentAlerts;

    @Override
    public boolean isEmpty() {
        return getCurrentAlerts().getVorabInformation().isEmpty()
                || getCurrentAlerts().getWarnings().isEmpty();
    }

    @Override
    public AlertCollection getCurrentAlerts() {
        return currentAlerts == null
                ? new AlertCollection()
                : currentAlerts;
    }

    @Override
    public void updateCurrentAlerts(AlertCollection alertCollection) {
        this.currentAlerts = alertCollection;
    }

    @Override
    public List<WarnCell> getAllWarnCells() {
        List<WarnCell> all = new ArrayList<>();
        all.addAll(toWarnCells(getCurrentAlerts().getWarnings()));
        all.addAll(toWarnCells(getCurrentAlerts().getVorabInformation()));
        return Collections.unmodifiableList(all);
    }

    private <A extends Alert> Collection<WarnCell> toWarnCells(Map<String, List<A>> alerts) {
        List<WarnCell> warnCells = new ArrayList<>();
        for (Map.Entry<String, List<A>> entry : alerts.entrySet()) {
            warnCells.add(new WarnCell(entry.getKey()));
        }
        return warnCells;
    }

    @Override
    public List<AlertMessage> getAllAlerts() {
        List<AlertMessage> all = new ArrayList<>();
        all.addAll(toAlertMessages(getCurrentAlerts().getWarnings()));
        all.addAll(toAlertMessages(getCurrentAlerts().getVorabInformation()));
        return Collections.unmodifiableList(all);
    }

    private <A extends Alert> Collection<AlertMessage> toAlertMessages(Map<String, List<A>> alerts) {
        List<AlertMessage> messages = new ArrayList<>();
        for (Map.Entry<String, List<A>> entry : alerts.entrySet()) {
            final WarnCell warnCell = new WarnCell(entry.getKey());
            for (Alert alert : entry.getValue()) {
                messages.add(new AlertMessage(warnCell, alert));
            }
        }
        return messages;
    }

    @Override
    public DateTime getLastKnownAlertTime() {
        Long alertTime = getCurrentAlerts().getTime();
        return alertTime != null
                ? new DateTime(alertTime)
                : null; // TODO optionals
    }

}
