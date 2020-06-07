package org.pclg.agenda.gui;

import java.sql.SQLException;

/**
 * @since 24/07/2017.
 */
public interface SimpleQueryExecutor {
    void setQuery(String query);
    void setOrderBy(String orderByClause);
    void setRecordFilter(String recordFilter);
    void setGroupSelectionPanel(GroupSelectionPanel groupSelectionPanel);   // FIXME: Esto es un flechazo horrible
    void showData(final AgendaGUI.ListCriterium criterium) throws SQLException;
}
